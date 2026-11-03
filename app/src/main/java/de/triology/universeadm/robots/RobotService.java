package de.triology.universeadm.robots;

import com.google.gson.Gson;
import com.github.legman.EventBus;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.pat.PATClient;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RobotService {
    private static final Gson GSON = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(RobotService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private final LDAPConfiguration configuration;
    private final LDAPHasher hasher;
    private final EventBus eventBus;
    private final LDAPConnectionStrategy strategy;
    private final Mapper<User> mapper;
    private final PATClient patClient;

    @Inject
    public RobotService(LDAPConnectionStrategy strategy, LDAPConfiguration configuration,
                        MapperFactory mapperFactory, PATClient patClient, LDAPHasher hasher, EventBus eventBus) {
        this.strategy = strategy;
        this.configuration = configuration;
        this.hasher = hasher;
        this.eventBus = eventBus;
        this.mapper = mapperFactory.createMapper(User.class, configuration.getUserBaseDN());
        this.patClient = patClient;
    }

    /** Returns the first CAS PAT per technical LDAP user, in CAS response order. */
    public List<RobotPAT> getRobots() throws LDAPException, IOException {
        List<RobotPAT> robots = new ArrayList<>();
        for (User user : getTechnicalUsers()) {
            RobotPAT pat = getFirstPAT(user);
            if (pat != null) {
                robots.add(pat);
            }
        }
        return robots;
    }

    /** Creates the technical LDAP user before creating their PAT in CAS. */
    public CreatedRobotPAT createRobot(CreateRobotRequest request) throws LDAPException, IOException {
        validateCreateRequest(request);
        String username = request.getDisplayName();
        LDAPInterface connection = strategy.get();
        String mail = createRobotMail(username, connection);
        User user = new User(username, username, username, username, mail, null,
            false, Collections.emptyList());
        Entry entry = mapper.convert(user);
        entry.setAttribute("technical", "TRUE");
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        String password = "aA1!" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        if (configuration.isRequirePreEncodedPasswords()) {
            entry.setAttribute("userPassword", hasher.hash(password));
        } else {
            entry.setAttribute("userPassword", password);
        }
        // Never roll back an existing user when the LDAP add fails.
        connection.add(entry);
        CreatedRobotPAT pat;
        try {
            pat = createPAT(username, request);
        } catch (IOException | RuntimeException e) {
            try {
                connection.delete(entry.getDN());
            } catch (LDAPException rollbackError) {
                e.addSuppressed(rollbackError);
                LOG.error("Failed to roll back technical user after PAT creation failed", rollbackError);
            }
            throw e;
        }
        eventBus.post(new UserEvent(user, EventType.CREATE));
        return pat;
    }

    String createRobotMail(String username, LDAPInterface connection) throws LDAPException {
        String normalized = username.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");
        if (normalized.isEmpty()) {
            normalized = "account";
        }
        String mailAttribute = mapper.getAttribute("mail").getLdapName();
        for (int number = 1; ; number++) {
            String disambiguator = number == 1 ? "" : "-" + number;
            // The local part must fit in 64 characters, including the -robot suffix.
            int maxPrefixLength = 64 - "-robot".length() - disambiguator.length();
            String prefix = normalized.substring(0, Math.min(normalized.length(), maxPrefixLength))
                .replaceAll("-$", "");
            String mail = prefix + disambiguator + "-robot@account.de";
            SearchRequest request = new SearchRequest(mapper.getParentDN(), SearchScope.SUB,
                Filter.createANDFilter(mapper.getBaseFilter(), Filter.createEqualityFilter(mailAttribute, mail)), "1.1");
            if (connection.search(request).getEntryCount() == 0) {
                return mail;
            }
        }
    }

    private void validateCreateRequest(CreateRobotRequest request) {
        if (request == null || request.getDisplayName() == null
            || !request.getDisplayName().matches("[a-zA-Z0-9_@.\\-]{2,64}")) {
            throw new IllegalArgumentException("displayName must be a valid username with 2 to 64 characters");
        }
        if (request.getExpiresAt() != null) {
            try {
                if (!Instant.parse(request.getExpiresAt()).isAfter(Instant.now())) {
                    throw new IllegalArgumentException("expiresAt must be in the future");
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("expiresAt must be an ISO-8601 timestamp", e);
            }
        }
    }

    private CreatedRobotPAT createPAT(String username, CreateRobotRequest request) throws IOException {
        Response response = patClient.createPAT(username, GSON.toJson(request));
        try {
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                throw new IOException("Failed to create technical user PAT in CAS");
            }
            CreatedRobotPAT pat = GSON.fromJson((String) response.getEntity(), CreatedRobotPAT.class);
            if (pat == null || pat.getId() == null || pat.getId().isEmpty()
                || pat.getToken() == null || pat.getToken().isEmpty()) {
                throw new IOException("CAS returned incomplete PAT creation data");
            }
            pat.setDisplayName(username);
            return pat;
        } catch (JsonParseException | IllegalStateException e) {
            throw new IOException("Invalid CAS PAT creation data", e);
        } finally {
            response.close();
        }
    }

    List<User> getTechnicalUsers() throws LDAPException {
        String usernameAttribute = mapper.getAttribute("username").getLdapName();
        String displayNameAttribute = mapper.getAttribute("displayName").getLdapName();
        Filter filter = Filter.createANDFilter(mapper.getBaseFilter(),
            Filter.createEqualityFilter("technical", "TRUE"));
        SearchRequest request = new SearchRequest(mapper.getParentDN(), SearchScope.SUB,
            filter, usernameAttribute, displayNameAttribute);
        List<User> users = new ArrayList<>();
        LDAPInterface connection = strategy.get();
        ASN1OctetString cookie = null;
        do {
            request.setControls(new SimplePagedResultsControl(100, cookie));
            SearchResult result = connection.search(request);
            for (SearchResultEntry entry : result.getSearchEntries()) {
                String username = entry.getAttributeValue(usernameAttribute);
                if (username == null || username.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, "Technical user has no username");
                }
                User user = new User(username);
                user.setDisplayName(entry.getAttributeValue(displayNameAttribute));
                users.add(user);
            }
            SimplePagedResultsControl paging = SimplePagedResultsControl.get(result);
            if (paging == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, "Missing LDAP paging response");
            }
            cookie = paging.moreResultsToReturn() ? paging.getCookie() : null;
        } while (cookie != null);
        users.sort(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER));
        return users;
    }

    private RobotPAT getFirstPAT(User user) throws IOException {
        Response response = patClient.getPATs(user.getUsername());
        try {
            if (response.getStatus() < 200 || response.getStatus() > 299) {
                throw new IOException("Failed to load technical user PAT metadata from CAS");
            }
            if (response.getStatus() == 204) {
                return null;
            }
            JsonArray pats = GSON.fromJson((String) response.getEntity(), JsonArray.class);
            if (pats == null) {
                throw new IOException("CAS returned no PAT metadata array");
            }
            if (pats.size() == 0) {
                return null;
            }
            RobotPAT pat = GSON.fromJson(pats.get(0).getAsJsonObject(), RobotPAT.class);
            if (pat.getId() == null || pat.getId().isEmpty()) {
                throw new IOException("CAS PAT metadata has no id");
            }
            String name = user.getDisplayName();
            pat.setDisplayName(name == null || name.trim().isEmpty() ? user.getUsername() : name);
            return pat;
        } catch (JsonParseException | IllegalStateException e) {
            throw new IOException("Invalid CAS PAT metadata", e);
        } finally {
            response.close();
        }
    }
}

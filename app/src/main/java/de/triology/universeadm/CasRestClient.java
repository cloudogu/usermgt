package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import org.apache.shiro.cas.CasAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Client for the CAS REST API.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @see <a href="https://apereo.github.io/cas/7.3.x/protocol/REST-Protocol.html">CAS REST API</a>
 */
public class CasRestClient {

    /**
     * URL path fragment for CAS service ticket creation
     */
    private static final String CAS_V1_TICKETS = "/v1/tickets";

    /**
     * UTF-8 encoding for URL encoding
     */
    private static final String ENCODING = "UTF-8";

    /**
     * HTTP request header name for Location
     */
    private static final String HEADER_LOCATION = "Location";

    /**
     * the logger for CasRestClient.
     */
    private static final Logger logger = LoggerFactory.getLogger(CasRestClient.class);

    /**
     * Constructs a CasRestClient.
     *
     * @param casServerUrl the base URL to CAS
     * @param serviceUrl   this service's base URL
     */
    public CasRestClient(String casServerUrl, String serviceUrl) {
        this(casServerUrl, serviceUrl, new DefaultCasRestUrlConnectionFactory());
    }

    /**
     * Constructs a CasRestClient with dependency injection for testing purposes
     */
    CasRestClient(String casServerUrl, String serviceUrl, CasRestUrlConnectionFactory connectionFactory) {
        this.casServerUrl = casServerUrl;
        this.serviceUrl = serviceUrl;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Creates a service ticket for the given username and password.
     *
     * @param username username
     * @param password password
     * @return service ticket
     */
    public String createServiceTicket(final String username, final String password) {
        String st = null;

        try {
            String tgt = createGrantingTicket(casServerUrl, username, password);

            logger.debug("TGT is: {}", tgt);

            st = createServiceTicket(tgt);

            logger.debug("ST is: {}", st);
        } catch (IOException ex) {
            throw new RuntimeException("cas validation failed", ex);
        }

        return st;
    }

    /**
     * appendCredentials enriches a HTTP request with basic auth credentials.
     *
     * @param connection the HTTP connection that should receive basic auth credentials
     * @param username   the user's login
     * @param password   the user's login secret
     * @throws IOException if errors occur when handling the HTTP connection
     */
    private void appendCredentials(HttpURLConnection connection, String username, String password) throws IOException {
        StringBuilder buffer = new StringBuilder();

        buffer.append("username=").append(encode(username));
        buffer.append("&password=").append(encode(password));

        try (BufferedWriter bwr = createWriter(connection)) {
            bwr.write(buffer.toString());
            bwr.flush();
        }
    }

    /**
     * appendServiceUrl is a helper method which writes to the connection
     *
     * @param connection the HTTP connection that should receive the service URL
     * @throws IOException if errors occur when handling the HTTP connection
     */
    private void appendServiceUrl(HttpURLConnection connection) throws IOException {
        String encodedServiceURL = "service=".concat(encode(serviceUrl));

        logger.debug("Service url is: {}", encodedServiceURL);

        try (BufferedWriter writer = createWriter(connection)) {
            writer.write(encodedServiceURL);
            writer.flush();
        }
    }

    /**
     * close closes the given connection.
     *
     * @param c the connection to be closed
     */
    private void close(HttpURLConnection c) {
        if (c != null) {
            c.disconnect();
        }
    }

    /**
     * createGrantingTicket creates a TGT from the remote CAS.
     *
     * @param casServerUrl the CAS base URL
     * @param username     the user's username
     * @param password     the user's login secret
     * @return a CAS ticket granting ticket
     * @throws IOException if errors occur when handling the HTTP connection
     */
    private String createGrantingTicket(String casServerUrl, String username, String password) throws IOException {
        HttpURLConnection connection = null;

        try {
            connection = open(casServerUrl + CAS_V1_TICKETS);
            appendCredentials(connection, username, password);

            int rc = connection.getResponseCode();

            switch (rc) {
                case HttpServletResponse.SC_UNAUTHORIZED:
                case HttpServletResponse.SC_FORBIDDEN:
                    throw new CasAuthenticationException("could not create granting ticket, web service returned " + rc);
                case HttpServletResponse.SC_CREATED:
                    break;
                default:
                    throw new RuntimeException("could not create granting ticket, web service returned " + rc);
            }

            String location = connection.getHeaderField(HEADER_LOCATION);

            if (Strings.isNullOrEmpty(location)) {
                throw new CasAuthenticationException("could not create granting ticket, web service returned no location header");
            }

            return extractTgtFromLocation(location);
        } finally {
            close(connection);
        }
    }

    /**
     * createReader creates a reader for the given HTTP connection.
     *
     * @param connection the connection
     * @return a reader
     * @throws IOException if errors occur during handling the connection stream.
     */
    private BufferedReader createReader(HttpURLConnection connection) throws IOException {
        return new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
    }

    /**
     * createServiceTicket creates a service ticket via the remote CAS service using a ticket granting ticket.
     *
     * @param tgt the CAS ticket granting ticket ID
     * @return the CAS service ticket ID
     * @throws IOException if errors during processing the connection or other stream occur
     */
    private String createServiceTicket(String tgt) throws IOException {
        String st = null;
        HttpURLConnection connection = null;

        try {
            connection = open(createServiceTicketUrl(tgt));
            appendServiceUrl(connection);

            int rc = connection.getResponseCode();

            switch (rc) {
                case HttpServletResponse.SC_UNAUTHORIZED:
                case HttpServletResponse.SC_FORBIDDEN:
                    throw new CasAuthenticationException("could not create service ticket, web service returned " + rc);
                case HttpServletResponse.SC_OK:
                    break;
                default:
                    throw new RuntimeException("could not create service ticket, web service returned " + rc);
            }

            String content;

            try (BufferedReader reader = createReader(connection)) {
                content = CharStreams.toString(reader);
            }

            if (Strings.isNullOrEmpty(content)) {
                throw new CasAuthenticationException("could not create service ticket, body is empty");
            }

            st = content.trim();

        } finally {
            close(connection);
        }

        return st;
    }

    /**
     * createServiceTicketUrl creates the URL towards the remote CAS service for creating a new service ticket.
     *
     * @param tgt the CAS ticket granting ticket
     * @return the full CAS URL to create a service ticket
     */
    private String createServiceTicketUrl(String tgt) {
        return casServerUrl + CAS_V1_TICKETS + "/" + tgt;
    }

    /**
     * createWriter creates a writer to add information to the HTTP request connection.
     *
     * @param connection the URL connection
     * @return a writer
     * @throws IOException if errors occur during handling the connection
     */
    private BufferedWriter createWriter(HttpURLConnection connection) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), Charsets.UTF_8));
    }

    /**
     * encode encodes the given value to an urlencoded string in UTF-8 for safe usage in HTTP requests.
     *
     * @param value the value to be urlencoded
     * @return the encoded string
     */
    private String encode(String value) {
        try {
            return URLEncoder.encode(value, ENCODING);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("failure during urlencoding value: " + value, ex);
        }
    }

    /**
     * extractTgtFromLocation extracts and returns a CAS TGT from a given location header
     *
     * @param location the content of the location header; must not be null
     * @return the CAS ticket granting ticket ID
     * @throws CasAuthenticationException if the given location is empty and doesn't contain a TGT
     */
    String extractTgtFromLocation(String location) {
        int index = location.lastIndexOf('/');

        if (index < 0) {
            throw new CasAuthenticationException("could not create granting ticket, web service returned invalid location header");
        }

        return location.substring(index + 1);
    }

    /**
     * open opens a connection to the remote CAS service
     *
     * @param url the full URL for which the HTTP request should be sent to
     * @return URL Connection
     * @throws IOException if errors occur when handling the HTTP connection
     */
    private HttpURLConnection open(final String url) throws IOException {
        HttpURLConnection connection = this.connectionFactory.create(url);

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        return connection;

    }

    /**
     * the CAS base URL
     */
    private final String casServerUrl;

    /**
     * the base URL of this service as it has been registered with CAS. Comes in via configuration injection.
     */
    private final String serviceUrl;

    private final CasRestUrlConnectionFactory connectionFactory;
}

interface CasRestUrlConnectionFactory {
    HttpURLConnection create(final String url) throws IOException;
}

class DefaultCasRestUrlConnectionFactory implements CasRestUrlConnectionFactory {
    public HttpURLConnection create(final String url) throws IOException {
        return (HttpURLConnection) new URL(url).openConnection();
    }
}

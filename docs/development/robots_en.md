# Technical accounts: architecture and API

Technical accounts enable automated access to the Cloudogu EcoSystem independently of an employee's personal account. The feature connects an LDAP user account with an access token in CAS. In the source code and API paths, technical accounts are referred to as `robots`.

This document describes the current implementation and the API for integrations. For instructions on using the interface, see [Technical accounts](../gui/robots_en.md).

## Components and responsibilities

| Component | Responsibility |
| --- | --- |
| `RobotResource` | Exposes the HTTP endpoints, checks permissions, and translates service errors into HTTP responses. |
| `RobotService` | Connects LDAP accounts with CAS tokens and manages creation, renewal, and deletion. |
| `PATClient` | Sends HTTP requests to the CAS PAT API. |
| LDAP | Stores the technical user account with the attribute `technical=TRUE`. |
| CAS | Manages access tokens and their metadata. |
| Frontend service `RobotsService` | Calls the User Management endpoints. |

The account and its token are separate objects. During renewal, the LDAP account remains in place while CAS creates a new token and deletes the previous one. Deleting through the Robots API removes both the addressed token and its associated technical account.

## Access and base URL

The API is available at the following base URL:

```text
https://<ces-host>/usermgt/api/robots
```

All operations require an authenticated account with the internal role `Universe Administrator`. Every method in `RobotResource` checks this role. The configured administrator role in the CAS configuration is mapped to this internal role. For integrations, this server-side role is what matters, even if the interface refers to the permission as a manager permission.

The following `curl` examples use an existing authenticated User Management session. `cookies.txt` represents a cookie file in a format supported by `curl`, containing valid session cookies. Authentication itself is not part of the Robots API. Replace the host, IDs, and sample data with values from your environment.

```bash
export ROBOTS_API='https://<ces-host>/usermgt/api/robots'
```

Requests with a JSON body use `Content-Type: application/json`. Responses containing data are returned as `application/json`. Unauthenticated requests may be rejected or redirected to the login page by security filters before reaching the resource.

## Endpoint overview

| Method | Path relative to the base URL | Purpose | Success status |
| --- | --- | --- | --- |
| `GET` | empty | List technical accounts with token metadata | `200 OK` |
| `GET` | `/{userId}/{id}` | Read one technical account's token metadata | `200 OK` |
| `POST` | empty | Create a technical account and its first token | `201 Created` |
| `PUT` | `/{username}` | Replace an existing token | `201 Created` |
| `DELETE` | `/{username}` | Delete a token and its associated technical account | `204 No Content` |

`username` is the **LDAP username** returned in `userId`. Encode it as a URL path segment. The username remains unchanged after renewal; only the CAS token ID changes. Renewal and deletion use the account’s first CAS token, as shown in the overview.

`GET /{userId}/{id}` uses the LDAP username from `userId` and the CAS token ID from `id`. Encode both as URL path segments. The backend looks up only that technical LDAP account and loads only its CAS tokens, then returns the matching token as a single metadata object without the secret `token` field. It returns `404` if the account or the requested token is missing. The display name follows the same rules as the overview. Example: `GET /usermgt/api/robots/datenabgleich/pat-123`. The frontend uses this endpoint for its detail view; the detail page URL includes the username and token ID.

## Retrieving accounts and token metadata

```bash
curl --cookie cookies.txt \
  --header 'Accept: application/json' \
  "$ROBOTS_API"
```

Example response with `200 OK`:

```json
[
  {
    "id": "pat-123",
    "userId": "datenabgleich",
    "displayName": "datenabgleich",
    "createdAt": "2026-11-10T10:00:00Z",
    "expiresAt": "2099-01-01T00:00:00Z",
    "scope": "/scm,/redmine"
  }
]
```

These values are examples. Actual IDs and timestamps are supplied by CAS.

| Field | Meaning |
| --- | --- |
| `id` | Token ID in CAS. |
| `userId` | LDAP username; used for renewal and deletion. |
| `displayName` | The LDAP display name in the overview; the username is used if the display name is empty. |
| `createdAt` | Token creation timestamp supplied by CAS. |
| `expiresAt` | Token expiry timestamp. The field may be absent if there is no expiry date. |
| `scope` | The token's scope, for example `/scm,/redmine` or `/*`. |

The overview never contains the `token` field. The backend does not return a `status` field either; the frontend derives the status from the expiry date. Gson serialization omits fields whose value is `null`.

### How the list is assembled

1. The service searches the configured LDAP user base using the user base filter combined with `technical=TRUE`.
2. The LDAP search reads pages of up to 100 entries and follows the paging cookies until the end. Usernames and display names are read through the configured attribute mapping.
3. The accounts are sorted by username, ignoring case.
4. For each account, the service requests token metadata from CAS.
5. Only the first token per account, in the order returned by CAS, is included in the overview.

Accounts without tokens are omitted. A CAS response of `204` is treated as an empty token list. If an LDAP or CAS query fails, the entire request fails; the API does not return an incomplete overview.

The public API does not provide pagination parameters. A list request may trigger multiple LDAP pages and one CAS request per technical account. The regular user overview filters out technical accounts.

## Creating a technical account

```bash
curl --cookie cookies.txt \
  --request POST \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --data '{
    "displayName": "datenabgleich",
    "expiresAt": "2099-01-01T00:00:00Z",
    "scope": "/scm,/redmine"
  }' \
  "$ROBOTS_API"
```

| Request field | Required | Behavior |
| --- | --- | --- |
| `displayName` | Yes | Also used as the username. Must contain 2 to 64 characters from `a–z`, `A–Z`, `0–9`, `_`, `@`, `.`, and `-`. |
| `expiresAt` | No | An ISO-8601 timestamp that can be parsed by `Instant.parse` and must be in the future, for example `2099-01-01T00:00:00Z`. If absent or `null`, no expiry date is sent to CAS. |
| `scope` | Yes in the frontend | The frontend sends a comma-separated list of Dogu paths or `/*` for all Dogus. `RobotService` does not validate this field itself; it forwards it to CAS. |

The timestamp is an absolute point in time. Values such as `30` or `30 days` are not accepted. The interface calculates the timestamp from the selected number of days.

Example response with `201 Created`:

```json
{
  "id": "pat-123",
  "userId": "datenabgleich",
  "displayName": "datenabgleich",
  "createdAt": "2026-11-10T10:00:00Z",
  "expiresAt": "2099-01-01T00:00:00Z",
  "scope": "/scm,/redmine",
  "token": "BEISPIEL-KEIN-ECHTER-SCHLUESSEL"
}
```

The service takes the metadata from CAS and sets `userId` and `displayName` to the LDAP username. It requires at least a non-empty `id` and a non-empty `token` from CAS. Other metadata may be absent if CAS does not provide it.

The secret token value is returned only in the successful response to creation or renewal. The Robots API provides no way to retrieve it again later.

### Workflow in LDAP and CAS

1. The service validates the name and expiry date.
2. It generates a unique technical email address, for example `datenabgleich-robot@account.de`. The name is converted to lowercase, and sequences of characters outside `a–z` and `0–9` are replaced with hyphens. A sequence number is added if there is a conflict. The part before `@` is limited to 64 characters.
3. The new LDAP user's username, display name, first name, and last name are set to the supplied name. The group list is initially empty, and the `technical` attribute is set to `TRUE`.
4. The service generates a random internal password. Depending on the LDAP configuration, it is hashed before storage. The password is not returned to the API client.
5. After the LDAP account has been created successfully, `PATClient` creates the token in CAS.
6. A `UserEvent` of type `CREATE` is published only after token creation succeeds.

If token creation fails, the service attempts to delete the LDAP account it just created. An existing account is not modified in the event of a name conflict. If rollback also fails, the additional error is logged; an LDAP account without a usable token may remain.

## Renewing an access token

Renewal replaces the token of an existing technical account. It does not modify the existing token object.

```bash
curl --cookie cookies.txt \
  --request PUT \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --data '{"expiredAt":"2099-02-01T00:00:00Z"}' \
  "$ROBOTS_API/datenabgleich"
```

**The field name here is `expiredAt`, whereas creation uses `expiresAt`.** This difference in spelling is part of the current implementation.

| Request body | Effect |
| --- | --- |
| `{"expiredAt":"2099-02-01T00:00:00Z"}` | Uses the specified future expiry timestamp. |
| `{}` | Preserves the existing expiry date. |
| `{"expiredAt":null}` | Removes the expiry date. |

An explicitly supplied timestamp that is invalid, empty, or not in the future results in `400 Bad Request`. The error messages refer to `expiresAt` because the same validation is used as for creation.

**An explicit `null` removes the expiry date; omitting `expiredAt` preserves it.** A token that already has no expiry date remains without one unless an explicit timestamp is supplied. For an expired token, provide a new future timestamp or explicitly remove expiration: without an override, the old expiry date is forwarded to CAS rather than automatically extended.

The successful response has status `201 Created` and the same structure as the creation response. It contains the **new ID** and the **new secret token value**. Continue using the same username for subsequent operations.

### Workflow and error handling

1. The service looks up the LDAP account by the supplied username and `technical=TRUE`. It loads only that account’s CAS tokens and uses the first one. If the account or token is missing, the API returns `404`.
2. It copies the old CAS token's display name and scope and applies the new expiry timestamp if supplied.
3. CAS first creates a new token for the same user.
4. The old token is then deleted in CAS.
5. Only then does the API return the new token. The LDAP account remains unchanged, and no user event is published.

If creating the replacement token fails, the old token is not deleted. If deleting the old token subsequently fails, the service attempts to remove the replacement token. This rollback attempt can also fail, in which case the failure is logged.

After successful renewal, the old token is invalid. Applications must be updated to use the new token. Renewal is not idempotent: another request using the same username replaces the current token again. If the successful response is lost, the new token value cannot be retrieved from the overview.

## Deleting a technical account

```bash
curl --cookie cookies.txt \
  --request DELETE \
  "$ROBOTS_API/datenabgleich"
```

On success, the API responds with `204 No Content` and no response body. Use the LDAP username for deletion, including after renewal.

The service looks up the technical LDAP account by username and loads only its CAS tokens. It then deletes the first token in CAS, deletes the account in LDAP, and publishes a `UserEvent` of type `REMOVE`.

If deleting the token fails, the LDAP account is retained. If LDAP deletion fails after successful CAS deletion, the token has already been removed while the account may still exist. There is no automatic recovery attempt for this case. A `REMOVE` event is published only after LDAP deletion succeeds.

The operation deletes the first CAS token of the specified account. It does not loop through and delete additional tokens belonging to the same account. The regular creation and renewal workflows are designed for one managed token per account.

## Status codes and error responses

| Status | Operations | Meaning |
| --- | --- | --- |
| `200 OK` | `GET` | Metadata list (possibly `[]`) or individual token metadata loaded successfully. |
| `201 Created` | `POST`, `PUT` | Account and token created, or replacement token created. |
| `204 No Content` | `DELETE` | Token and account deleted successfully. |
| `400 Bad Request` | `POST`, `PUT`, `DELETE` | Service validation failed, for example because of an invalid name, timestamp, or empty username. |
| `403 Forbidden` | All | The resource does not recognize an authenticated identity with the required role. Upstream filters may return a different response. |
| `404 Not Found` | `GET /{userId}/{id}`, `PUT`, `DELETE` | The specified technical LDAP account or token does not exist. |
| `409 Conflict` | `POST` | The LDAP username is already taken, including by a regular user account. |
| `500 Internal Server Error` | All | LDAP access failed. |
| `502 Bad Gateway` | All | CAS access failed, or the CAS response was invalid or incomplete. |

Example error responses:

```json
{"message":"displayName must be a valid username with 2 to 64 characters"}
```

```json
{"message":"A user with this name already exists"}
```

```json
{"message":"Failed to renew robot PAT in CAS"}
```

Validation and service errors usually contain a `message` field. The directly generated `403` and `404` responses have no body. Errors when reading JSON or in upstream filters may be generated by the framework and may look different.

CAS errors are not forwarded unchanged with their original HTTP status. For example, a creation request rejected by CAS is treated as an error by `RobotService`, and the resource responds with `502`.

## Internal CAS requests

`PATClient` appends `/api/users` to the configured CAS server URL and uses the following endpoints:

| Purpose | Internal request |
| --- | --- |
| Read token metadata | `GET <cas-server-url>/api/users/{username}/pats` |
| Create a token | `POST <cas-server-url>/api/users/{username}/pats` |
| Delete a token | `DELETE <cas-server-url>/api/users/{username}/pats/{id}` |

The username is encoded as a URL path segment. Creation sends `displayName`, optionally `expiresAt`, and `scope` as JSON. The internal CAS field is also named `expiresAt` during renewal: `RobotService` handles the translation from `expiredAt`.

For CAS requests, `PATClient` reads the Java system properties `cas.mfa.user` and `cas.mfa.password`. If both are set, it sends HTTP Basic Authentication using these service credentials. These credentials are separate from the API caller's session and the newly generated access token. Connection and read timeouts are each set to ten seconds.

## Limitations and integration notes

- LDAP and CAS do not share a transaction. The rollback attempts described above limit partial states but cannot completely prevent them if further errors occur.
- `POST` creates an account on each call and does not assign an idempotency ID. After a connection failure, check the actual state before retrying. A repeated call with the same name may result in `409`.
- The metadata overview shows at most one token per account. It is not a complete list of all tokens stored in CAS.
- The association used for renewal and deletion is determined from the technical LDAP account and its CAS tokens. A `userId` from CAS is not used as the sole trusted source for this purpose.
- This implementation does not assign group memberships to new technical accounts. A token's Dogu selection is separate from application permissions within a Dogu.
- API clients should store token values securely and avoid writing them to application logs. Lost token values must be replaced by renewing the token again.

## Relevant source files and tests

- [RobotResource.java](../../app/src/main/java/de/triology/universeadm/robots/RobotResource.java): Endpoints, role checks, and status codes.
- [RobotService.java](../../app/src/main/java/de/triology/universeadm/robots/RobotService.java): LDAP/CAS workflows, validation, and rollback attempts.
- [CreateRobotRequest.java](../../app/src/main/java/de/triology/universeadm/robots/CreateRobotRequest.java) and [RenewRobotRequest.java](../../app/src/main/java/de/triology/universeadm/robots/RenewRobotRequest.java): Request fields.
- [RobotPAT.java](../../app/src/main/java/de/triology/universeadm/robots/RobotPAT.java) and [CreatedRobotPAT.java](../../app/src/main/java/de/triology/universeadm/robots/CreatedRobotPAT.java): Response fields.
- [PATClient.java](../../app/src/main/java/de/triology/universeadm/pat/PATClient.java): HTTP integration with CAS.
- [Robots.ts](../../app/src/main/ui/src/services/Robots.ts): Frontend API calls.
- [RobotResourceTest.java](../../app/src/test/java/de/triology/universeadm/robots/RobotResourceTest.java) and [RobotServiceTest.java](../../app/src/test/java/de/triology/universeadm/robots/RobotServiceTest.java): Tests for status codes, validation, and error handling.

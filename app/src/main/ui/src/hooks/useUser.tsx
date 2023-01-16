import {useQuery, UseQueryResult} from "react-query";

const contextPath = process.env.PUBLIC_URL || "/usermgt";
const errorMsg = "Abruf der Benutzerdaten fehlgeschlagen";

export type User = {
    username: string;
    role: string;
}

// const test = {
//     "clientIpAddress": "192.168.56.1",
//     "isFromNewLogin": "false",
//     "mail": "christian.beyer@cloudogu.com",
//     "authenticationDate": "2023-01-16T11:28:27.254525Z",
//     "displayName": "admin",
//     "givenName": "admin",
//     "successfulAuthenticationHandlers": "CesGroupAwareLdapAuthenticationHandler",
//     "groups": ["cesManager", "cesAdmin"],
//     "admin": true,
//     "userAgent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
//     "cn": "admin",
//     "credentialType": "UsernamePasswordCredential",
//     "samlAuthenticationStatementAuthMethod": "urn:oasis:names:tc:SAML:1.0:am:password",
//     "principal": "gary",
//     "authenticationMethod": "CesGroupAwareLdapAuthenticationHandler",
//     "surname": "admin",
//     "serverIpAddress": "172.18.0.8",
//     "longTermAuthenticationRequestTokenUsed": "false",
//     "username": "gary"
// }

// https://answer.cloud.itz.in.bund.de
// https://admin.answer.cloud.itz.in.bund.de

export function useUser(): UseQueryResult<User> {
    return useQuery(["user"], () => {
        return new Promise<User>((resolve, reject) => {
            fetch(contextPath + `/api/subject`).then(function (response) {
                return response.json().then(function (jsonContent) {
                    if (!jsonContent) {
                        return {};
                    }
                    if (!response.ok) {
                        reject(new Error(errorMsg));
                    } else {
                        resolve(jsonContent as User);
                    }
                });
            }).catch((e) => {
                reject(e);
            });
        });
    });
}

import {useQuery, UseQueryResult} from "react-query";

const contextPath = process.env.PUBLIC_URL || "/admin";
const errorMsg = "Abruf der Benutzerdaten fehlgeschlagen";

export type User = {
    name: string;
    role: string;
}

// https://answer.cloud.itz.in.bund.de
// https://admin.answer.cloud.itz.in.bund.de

export function useUser(): UseQueryResult<User> {
    return useQuery(["user"], () => {
        return new Promise<User>((resolve, reject) => {
            fetch(contextPath + `/api/user`).then(function (response) {
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

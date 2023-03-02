import {QueryOptions} from "../hooks/useAPI";
import {Axios} from "../api/axios";

export type PasswordPolicy = {
    Rules: {
        Rule: string,
        Type: "regex",
        Name: string,
        Variables: {
            Name: string;
            Value: string
        }[]
    }[],
}

export const defaultPasswordPolicy: PasswordPolicy = {
    Rules: [
        {Rule: "", Type: "regex", Name: "", Variables: [{Name: "", Value: ""}]}
    ]
}

export const ValidationSchemaService = {
    async get(signal?: AbortSignal, _?: QueryOptions): Promise<PasswordPolicy> {
        return new Promise<PasswordPolicy>(async (resolve, reject) => {
            try {
                const response = await Axios<PasswordPolicy>('/account/passwordpolicy', {
                    signal: signal
                });
                if (response.status < 200 || response.status > 299) {
                    reject(new Error("failed to load validation schema: " + response.status));
                }
                resolve(response.data);
            } catch (e) {
                reject(e)
            }
        });
    },
}
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
};

export const ValidationSchemaService = {
    async get(signal?: AbortSignal): Promise<PasswordPolicy> {
        const response = await Axios<PasswordPolicy>("/account/passwordpolicy", {
            signal: signal
        });
        if (response.status < 200 || response.status > 299) {
            throw new Error("failed to load validation schema: " + response.status);
        }

        if (!response.data?.Rules) {
            return defaultPasswordPolicy;
        }

        return response.data;
    },
};

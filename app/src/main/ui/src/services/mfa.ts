// mfaService.ts
import {Axios} from "../api/axios";

export type Mfa = {
    username: string;
    name: string;
}

// MfaService provides methods for managing multifactor authentication (MFA) credentials.
export const MfaService = {
    async list(signal?: AbortSignal): Promise<Mfa[]> {
        const response = await Axios.get<Mfa[]>("/mfa", {
            signal: signal,
        } as any);
        console.log("response", response.data);

        if (response.status < 200 || response.status > 299) {
            throw new Error("failed to load mfa credentials: " + response.status);
        }

        return response.data;
    },

    async deleteByUserName(userName: string): Promise<void> {

        if (!userName) {
            throw new Error("the user name must not be empty");
        }

        const response = await Axios.delete<void>(
            `/cas/actuator/gauthCredentialRepository/${encodeURIComponent(userName)}`,
            {}
        );

        if (response.status < 200 || response.status > 299) {
            throw new Error("failed to delete mfa credentials: " + response.status);
        }
    }
};

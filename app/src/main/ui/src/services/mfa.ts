// mfaService.ts
import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";

export type Mfa = {
    username: string;
    name: string;
}

// MfaService provides methods for managing multifactor authentication (MFA) credentials.
export const MfaService = {
    async get(username: string, signal?: AbortSignal): Promise<Mfa> {
        const response = await Axios.get<Mfa>(`/mfa/${username}`, {
            signal: signal,
        } as any);

        if (isSuccessStatus(response.status)) {
            throw new Error("failed to load mfa credentials: " + response.status);
        }

        return response.data;
    },

    async delete(username: string): Promise<void> {

        if (!username) {
            throw new Error("the user name must not be empty");
        }

        const response = await Axios.delete<void>(
            `/mfa/${username}`,
            {}
        );

        if (isSuccessStatus(response.status)) {
            throw new Error("failed to delete mfa credentials: " + response.status);
        }
    }
};

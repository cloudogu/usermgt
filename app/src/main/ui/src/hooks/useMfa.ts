// useMfa.ts
import {Mfa, MfaService} from "../services/mfa";
import { useAPI } from "./useAPI";

export type mfaResult = {
    mfa: Mfa | undefined,
    isMfaLoading: boolean,
    mfaError: Error | undefined,
}

// Get mfa data for the given user
export function useMfa(username?: string): mfaResult {
    if (!username) {
        return {
            mfa: undefined,
            isMfaLoading: false,
            mfaError: new Error("no username given for useMfa"),
        };
    }
    const {
        data: mfaList,
        isLoading: isMfaLoading,
        error: mfaError,
    } = useAPI<Mfa>((signal) => MfaService.get(username, signal));

    return {
        mfa: mfaList,
        isMfaLoading,
        mfaError,
    };
}

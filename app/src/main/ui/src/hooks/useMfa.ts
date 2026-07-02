// useMfa.ts
import {useState} from "react";
import { MfaService} from "../services/mfa";
import { useAPI } from "./useAPI";
import type {Mfa} from "../services/mfa";

export type mfaResult = {
    mfa: Mfa | undefined,
    isMfaLoading: boolean,
    mfaError: Error | undefined,
    reloadMfa: () => void,
}

// Get mfa data for the given user
export function useMfa(username?: string): mfaResult {
    const [reloadTrigger, setReloadTrigger] = useState(0);

    if (!username) {
        return {
            mfa: undefined,
            isMfaLoading: false,
            mfaError: new Error("no username given for useMfa"),
            reloadMfa: () => {},
        };
    }
    const {
        data: mfaList,
        isLoading: isMfaLoading,
        error: mfaError,
    } = useAPI<Mfa, number>((signal) => MfaService.get(username, signal), reloadTrigger);

    return {
        mfa: mfaList,
        isMfaLoading,
        mfaError,
        reloadMfa: () => setReloadTrigger(prev => prev + 1),
    };
}

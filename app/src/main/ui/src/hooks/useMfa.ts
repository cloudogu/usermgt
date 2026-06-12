// useMfa.ts
import {Mfa, MfaService} from "../services/mfa";
import { useAPI } from "./useAPI";

export type mfaResult = {
    mfaList: Mfa[] | undefined,
    isMfaLoading: boolean,
    mfaError: Error | undefined,
}

// Add mfa data to users. Returns the users as they are if no mfa data is available.
export function useMfa(): mfaResult {
    const {
        data: mfaList,
        isLoading: isMfaLoading,
        error: mfaError,
    } = useAPI<Mfa[]>((signal) => MfaService.list(signal));
    console.log(mfaList);
    console.log(isMfaLoading);
    console.log(mfaError);
    return {
        mfaList,
        isMfaLoading,
        mfaError,
    };
}

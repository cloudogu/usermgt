import {useMemo} from "react";
import {PATService} from "../services/PATs";
import {useAPI} from "./useAPI";
import {useDogus} from "./useDogus";
import type {DoguOption} from "../services/Dogus";

export type PersonalAccessToken = {
    id: string;
    displayName: string;
    status: "active" | "expired";
    createdAt: string;
    expiresAt: string;
};

export type PAT = {
    dogus: DoguOption[];
    tokens: PersonalAccessToken[];
};

export type PATResult = {
    pat: PAT | undefined;
    isPATLoading: boolean;
    patError: Error | undefined;
};

export function usePAT(username?: string): PATResult {
    const {
        doguOptions,
        isLoading: areDogusLoading,
        error: doguError,
    } = useDogus();
    const {
        data: patMetadata,
        isLoading: arePATsLoading,
        error: patError,
    } = useAPI(PATService.getAll);

    const tokens = useMemo<PersonalAccessToken[]>(() => (patMetadata ?? []).map(token => ({
        id: token.id,
        displayName: token.displayName,
        status: token.expiresAt && Date.parse(token.expiresAt) <= Date.now() ? "expired" : "active",
        createdAt: token.createdAt,
        expiresAt: token.expiresAt ?? "-",
    })), [patMetadata]);

    return {
        pat: username ? {
            dogus: doguOptions,
            tokens,
        } : undefined,
        isPATLoading: areDogusLoading || arePATsLoading,
        patError: username
            ? doguError ?? patError
            : new Error("no username given for usePAT"),
    };
}

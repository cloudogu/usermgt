import {formatDate} from "./i18nHelpers";
import type {DoguOption} from "../services/Dogus";
import type {PATMetadata} from "../services/PATs";

export function parsePATScope(scope?: string | null) {
    const scopeParts = new Set((scope ?? "").split(",").map(part => part.trim()).filter(Boolean));
    const allDogus = scopeParts.has("/*");
    const scopedDoguNames = new Set([...scopeParts].map(part => part.replace(/^\//, "")));

    return {scopeParts, allDogus, scopedDoguNames};
}

export function resolveScopedDogus(scope: string | null | undefined, doguOptions: DoguOption[]) {
    const {scopeParts, allDogus, scopedDoguNames} = parsePATScope(scope);
    const selectedDogus = doguOptions.filter(dogu => scopedDoguNames.has(dogu.value));
    const doguGroups = [
        {key: "base", dogus: selectedDogus.filter(dogu =>
            !["Administration", "Administration Apps", "Development", "Development Apps"].includes(dogu.category))},
        {key: "development", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Development" || dogu.category === "Development Apps")},
        {key: "administration", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Administration" || dogu.category === "Administration Apps")},
    ].filter(group => group.dogus.length > 0);
    const hasUnknownDogus = !allDogus && [...scopedDoguNames].some(part =>
        !doguOptions.some(dogu => dogu.value === part));

    return {scopeParts, allDogus, doguGroups, hasUnknownDogus};
}

export function isPATExpired(expiresAt?: string | null): boolean {
    return !!expiresAt && Date.parse(expiresAt) <= Date.now();
}

export function formatPATTimeHint(
    pat: Pick<PATMetadata, "createdAt" | "expiresAt"> | undefined,
    labels: {createdAt: string; expiresAt: string; neverExpires: string},
): string {
    if (!pat) return "";

    return `${labels.createdAt} ${formatDate(pat.createdAt)}・${pat.expiresAt
        ? `${labels.expiresAt} ${formatDate(pat.expiresAt)}`
        : labels.neverExpires}`;
}

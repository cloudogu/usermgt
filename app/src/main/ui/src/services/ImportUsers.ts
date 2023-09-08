import {isAxiosError} from "axios";

import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import type {User} from "./Users";
import type {QueryOptions} from "../hooks/useAPI";
import type {RefetchResponse} from "../hooks/usePaginatedData";
import type {AxiosError, AxiosResponse} from "axios";

export const IMPORT_PARSING_ERROR = 100;
export const IMPORT_FIELD_CONVERSION_ERROR = 101;
export const IMPORT_MISSING_FIELD_ERROR = 102;
export const IMPORT_VALIDATION_ERROR = 200;
export const IMPORT_UNIQUE_FIELD_ERROR = 201;
export const IMPORT_FIELD_FORMAT_ERROR = 202;
export const IMPORT_ERROR_UNKNOWN_1 = 203; //TODO implement
export const IMPORT_ERROR_UNKNOWN_2 = 204; //TODO implement

export type ImportErrorCode =
    typeof IMPORT_PARSING_ERROR |
    typeof IMPORT_FIELD_CONVERSION_ERROR |
    typeof IMPORT_MISSING_FIELD_ERROR |
    typeof IMPORT_VALIDATION_ERROR |
    typeof IMPORT_UNIQUE_FIELD_ERROR |
    typeof IMPORT_ERROR_UNKNOWN_1 |
    typeof IMPORT_ERROR_UNKNOWN_2 |
    typeof IMPORT_FIELD_FORMAT_ERROR;

export type ImportedUser = User

/** THE IMPORT PART */
export interface ImportError {
    message: string;
    errorCode: ImportErrorCode;
    lineNumber: number;
    params: {
        columns: string[];
    };
}

export interface ImportUsersResponseDto {
    created: ImportedUser[],
    updated: ImportedUser[],
    errors: ImportError[],
    timestamp: number,
    importID: string,
    filename: string,
}

export interface ImportUsersResponse extends Omit<ImportUsersResponseDto, "timestamp"> {
    timestamp: Date,
}

/** THE PROTOCOL PART */

export interface ImportSummaryDto {
    importID: string;
    filename: string;
    summary: {
        created: number;
        updated: number;
        skipped: number;
    };
    timestamp: number;
}

export interface ImportSummary extends Omit<ImportSummaryDto, "timestamp"> {
    timestamp: Date;
}

export type SummariesModel = {
    entries: ImportSummaryDto[],
    start: number,
    limit: number,
    totalEntries: number,
};
export const ImportUsersService = {
    async listSummaries(signal?: AbortSignal, opts?: QueryOptions): Promise<RefetchResponse<ImportSummary[]>> {
        const summariesResponse = await Axios.get<SummariesModel>("/users/import/summaries", {
            params: opts,
            signal: signal
        } as any);

        return {
            data: summariesResponse.data.entries.map(s => ({...s, timestamp: new Date(s.timestamp || 0)})),
            pagination: {
                start: summariesResponse.data.start,
                limit: summariesResponse.data.limit,
                totalEntries: summariesResponse.data.totalEntries,
            }
        };
    },
    async deleteSummary(summary: ImportSummary): Promise<void> {
        return Axios.delete(`/users/import/${summary.importID}`, {});
    },
    async getImportDetails(id: string, signal?: AbortSignal): Promise<AxiosResponse<ImportUsersResponse>> {
        const result = await Axios.get<ImportUsersResponseDto>(`/users/import/${id}`, {
            headers: {
                "Content-Type": "multipart/form-data"
            },
            signal: signal
        });

        return {
            ...result,
            data: {
                ...result.data,
                timestamp: new Date(result.data.timestamp),
            },
        };
    },
    async importCsv(file: File): Promise<AxiosResponse<ImportUsersResponse>> {
        try {
            const formData = new FormData();
            formData.append("file", file, file.name);
            const result = await Axios.post<ImportUsersResponseDto>("/users/import", formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });

            return {
                ...result,
                data: {
                    ...result.data,
                    timestamp: new Date(result.data.timestamp),
                },
            };
        } catch (e: AxiosError | unknown) {
            if (isAxiosError(e)) {
                const axiosError = e as AxiosError;
                if (axiosError.response?.status === 400) {
                    throw new Error(t("usersImport.notification.invalidFile", {file: file.name}));
                }
            }
            throw new Error(t("usersImport.notification.genericError", {file: file.name}));
        }
    }
};

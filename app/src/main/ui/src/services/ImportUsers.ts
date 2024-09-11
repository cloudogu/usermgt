import {isAxiosError} from "axios";

import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import type {User} from "./Users";
import type {QueryOptions} from "../hooks/useAPI";
import type {PaginationResponse} from "../hooks/usePaginationTableState";
import type {AxiosError, AxiosResponse} from "axios";

export const ERR_CODE_MISSING_FIELD_ERROR = 1000;
export const ERR_CODE_WRITE_RESULT_ERROR = 1001;
export const ERR_CODE_FIELD_LENGTH_ERROR = 2000;
export const ERR_CODE_MISSING_REQUIRED_FIELD_ERROR = 2001;
export const ERR_CODE_GENERIC_VALIDATION_ERROR = 2002;
export const ERR_CODE_UNIQUE_FIELD_ERROR = 3000;
export const ERR_CODE_UNIQUE_MAIL_ERROR = 3001;
export const ERR_CODE_FIELD_FORMAT_ERROR = 4000;
export const ERR_CODE_FIELD_FORMAT_TOO_LONG_ERROR = 4001;
export const ERR_CODE_FIELD_FORMAT_TOO_SHORT_ERROR = 4002;
export const ERR_CODE_FIELD_FORMAT_INVALID_CHARACTERS_ERROR = 4003;

export type ImportErrorCode =
    typeof ERR_CODE_MISSING_FIELD_ERROR | typeof ERR_CODE_WRITE_RESULT_ERROR |
    typeof ERR_CODE_FIELD_LENGTH_ERROR | typeof ERR_CODE_MISSING_REQUIRED_FIELD_ERROR |
    typeof ERR_CODE_GENERIC_VALIDATION_ERROR | typeof ERR_CODE_UNIQUE_FIELD_ERROR |
    typeof ERR_CODE_UNIQUE_MAIL_ERROR | typeof ERR_CODE_FIELD_FORMAT_ERROR |
    typeof ERR_CODE_FIELD_FORMAT_TOO_LONG_ERROR | typeof ERR_CODE_FIELD_FORMAT_TOO_SHORT_ERROR |
    typeof ERR_CODE_FIELD_FORMAT_INVALID_CHARACTERS_ERROR;

export type ImportedUser = User

/** THE IMPORT PART */
export interface ImportError {
    message: string;
    errorCode: ImportErrorCode;
    lineNumber: number;
    params: {
        columns: string[];
        values: string[];
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

export type SummariesModel = PaginationResponse<ImportSummary>

export const ImportUsersService = {
    async query(signal?: AbortSignal, opts?: QueryOptions): Promise<PaginationResponse<ImportSummary>> {
        const summariesResponse = await Axios.get<SummariesModel>("/users/import/summaries", {
            params: opts,
            signal: signal
        } as any);

        const response = summariesResponse.data;
        response.data = response.data.map(s => ({...s, timestamp: new Date(s.timestamp || 0)}));

        return response;
    },
    async delete(id: string): Promise<void> {
        return Axios.delete(`/users/import/${id}`, {});
    },
    createDownloadLink(summary: ImportSummary | ImportUsersResponse): string {
        return `/usermgt/api/users/import/${summary.importID}/download`;
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
                const errorCode = e.response?.data.errorCode ?? -1;
                if (errorCode === ERR_CODE_MISSING_FIELD_ERROR || errorCode === ERR_CODE_WRITE_RESULT_ERROR) {
                    throw new Error(t(`usersImportResult.msg.code-${errorCode}`));
                }
                if (axiosError.response?.status === 400) {
                    throw new Error(t("usersImport.notification.invalidFile", {file: file.name}));
                }
            }
            throw new Error(t("usersImport.notification.genericError", {file: file.name}));
        }
    }
};

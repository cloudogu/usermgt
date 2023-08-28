import {isAxiosError} from "axios";
import {t} from "../helpers/i18nHelpers";
import type {User} from "./Users";
import type {QueryOptions} from "../hooks/useAPI";
import type {PagedModel} from "@cloudogu/ces-theme-tailwind";
import type {AxiosError, AxiosResponse} from "axios";
import {mock} from "node:test";

export const IMPORT_PARSING_ERROR = 100;
export const IMPORT_FIELD_CONVERSION_ERROR = 101;
export const IMPORT_MISSING_FIELD_ERROR = 102;
export const IMPORT_VALIDATION_ERROR = 200;
export const IMPORT_UNIQUE_FIELD_ERROR = 201;
export const IMPORT_FIELD_FORMAT_ERROR = 202;
export type ImportErrorCode = 100 | 101 | 102 | 200 | 201 | 202;

export type ImportedUser = User

export interface ImportError {
    message: string;
    code: ImportErrorCode;
    lineNumber: number;
    params: {
        columns: string[];
    };
}

export interface ImportUsersResponse {
    created: ImportedUser[],
    updated: ImportedUser[],
    errors: ImportError[],
    timestamp: Date,
}

export interface ImportUsersResponseDto {
    created: ImportedUser[],
    updated: ImportedUser[],
    errors: ImportError[],
    timestamp: number,
}

export interface ImportProtocol {
    id: string;
    name: string;
    timestamp: Date;
    result: {
        created: number;
        updated: number;
        skipped: number;
    },
}

export interface ImportProtocolDto {
    id: string;
    name: string;
    timestamp: number;
    result: {
        created: number;
        updated: number;
        skipped: number;
    },
}

export type ProtocolsDtoModel = PagedModel & {
    protocols: ImportProtocolDto[];
}

export type ProtocolsModel = PagedModel & {
    protocols: ImportProtocol[];
}

const mockResponse: ImportUsersResponse = {
    created: [
        {
            displayName: "Super Admin",
            external: true,
            mail: "super@admin.de",
            givenname: "Mr.Super",
            memberOf: ["user", "user2", "user3", "user4", "admin", "superadmin", "megaadmin", "ultraadmin", "godadmin", "godhimself"],
            password: "",
            pwdReset: true,
            surname: "Admin",
            username: "SuperAdmin",
        },
        {
            displayName: "Super Admin",
            external: true,
            mail: "super@admin.de",
            givenname: "Mr.Super",
            memberOf: ["user"],
            password: "",
            pwdReset: true,
            surname: "Admin",
            username: "SuperAdmin",
        },
        {
            displayName: "Super Admin",
            external: true,
            mail: "super@admin.de",
            givenname: "Mr.Super",
            memberOf: ["user", "asdasdaasdasdasdasdasdasdasdDsss"],
            password: "",
            pwdReset: true,
            surname: "Admin",
            username: "SuperAdmin",
        }
    ],
    updated: [
        {
            displayName: "Super Duper Man",
            external: true,
            mail: "super@duper.com",
            givenname: "Mr.Superman",
            memberOf: ["superadmin"],
            password: "",
            pwdReset: true,
            surname: "Man",
            username: "SuperMan",
        }
    ],
    errors: [
        {
            code: IMPORT_PARSING_ERROR,
            lineNumber: 1,
            message: "could not parse line",
            params: {
                columns: ["MAIL"]
            }
        },
        {
            code: IMPORT_FIELD_CONVERSION_ERROR,
            lineNumber: 2,
            message: "asdf is not a valid value for field external",
            params: {
                columns: ["EXTERNAL"]
            }
        },
        {
            code: IMPORT_MISSING_FIELD_ERROR,
            lineNumber: 3,
            message: "asdf is not a valid value for field external",
            params: {
                columns: ["MAIL"]
            }
        },
        {
            code: IMPORT_VALIDATION_ERROR,
            lineNumber: 4,
            message: "some fields are invalid",
            params: {
                columns: ["USERNAME", "MAIL"]
            }
        },
        {
            code: IMPORT_UNIQUE_FIELD_ERROR,
            lineNumber: 5,
            message: "the mail must be unique",
            params: {
                columns: ["MAIL"]
            }
        },
        {
            code: IMPORT_FIELD_FORMAT_ERROR,
            lineNumber: 6,
            message: "some fields are invalid",
            params: {
                columns: ["USERNAME", "MAIL"]
            }
        },
    ],
    timestamp: new Date(1692879385304),
};

export const ImportUsersService = {
    async listImportProtocols(signal?: AbortSignal, opts?: QueryOptions): Promise<ProtocolsModel> {
        return {
            protocols: [
                {
                    id: "a5105f0e-4561-11ee-be56-0242ac120002",
                    timestamp: new Date(1692879385304),
                    name: "import-default-users.csv",
                    result: {
                        created: 1,
                        updated: 2,
                        skipped: 7,
                    },
                },
                {
                    id: "a5105f0e-4561-11ee-be56-0242ac120002",
                    timestamp: new Date(1692879386304),
                    name: "import-special-users.csv",
                    result: {
                        created: 3,
                        updated: 12,
                        skipped: 2,
                    },
                },
                {
                    id: "a5105f0e-4561-11ee-be56-0242ac120002",
                    timestamp: new Date(1692879389304),
                    name: "import-more-users.csv",
                    result: {
                        created: 15,
                        updated: 3,
                        skipped: 0,
                    },
                },
            ],
            pagination: {
                pageCount: 1,
                current: 1,
            },
        };
    },
    async deleteProtocol(protocol: ImportProtocol): Promise<void> {
    },
    async getImportDetails(protocol: ImportProtocol): Promise<ImportUsersResponse>{
        return mockResponse;
    },
    async importCsv(file: File): Promise<AxiosResponse<ImportUsersResponse>> {
        try {
            const formData = new FormData();
            formData.append("file", file, file.name);
            // return await Axios.post<ImportUsersResponseDto>("/users/import", formData, {
            //     headers: {
            //         "Content-Type": "multipart/form-data"
            //     }
            // });

            // TODO: map dto to normal

            return {
                config: undefined as any,
                headers: undefined as any,
                request: undefined,
                status: 200,
                statusText: "",
                data: mockResponse,
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

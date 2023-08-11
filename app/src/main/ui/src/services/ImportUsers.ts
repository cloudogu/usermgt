import {AxiosResponse, isAxiosError} from "axios";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import type {AxiosError} from "axios";

export interface ImportUsersResponse {
    summary: {
        CREATED: number;
        UPDATED: number;
        SKIPPED: number;
    },
    errors: string[];
}

export const ImportUsersService = {
    async save(file: File): Promise<AxiosResponse<ImportUsersResponse>> {
        try {
            const formData = new FormData();
            formData.append("file", file, file.name);
            return await Axios.post<ImportUsersResponse>("/users/:import", formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });
        } catch (e: AxiosError | unknown) {
            if (isAxiosError(e)) {
                const axiosError = e as AxiosError;
                if (axiosError.response?.status === 409) {
                    throw axiosError.response.data;
                }
            }
            throw new Error(t("newUser.notification.error", {username: file.name}));
        }
    }
};

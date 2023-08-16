import type {AxiosError} from "axios";
import {AxiosResponse, isAxiosError} from "axios";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {ImportUsersResponse} from "../hooks/useUserImportCsv";


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
                if (axiosError.response?.status === 400) {
                    throw new Error(t("usersImport.notification.invalidFile", {file: file.name}));
                }
            }
            throw new Error(t("usersImport.notification.genericError", {file: file.name}));
        }
    }
};

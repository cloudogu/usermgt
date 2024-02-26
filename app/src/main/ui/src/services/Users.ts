import {defaultPaginationData} from "@cloudogu/deprecated-ces-theme-tailwind";
import {isAxiosError} from "axios";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {emptyUser} from "./Account";
import type {QueryOptions} from "../hooks/useAPI";
import {PaginationError, PaginationErrorResponse, PaginationResponse} from "../hooks/usePaginatedData";
import type {PagedModel} from "@cloudogu/deprecated-ces-theme-tailwind";
import type {AxiosError} from "axios";

export type UsersResponse = PaginationResponse<User>

export type User = {
    displayName: string,
    givenname: string,
    mail: string,
    surname: string,
    username: string,
    password: string,
    pwdReset: boolean,
    external: boolean,
    memberOf: string[];
};

export type UsersModel = PagedModel & {
    users: User[];
}

export enum UserConstraints {
    // eslint-disable-next-line autofix/no-unused-vars
    UniqueEmail = "UNIQUE_EMAIL",
    // eslint-disable-next-line autofix/no-unused-vars
    UniqueUsername = "UNIQUE_USERNAME",
}

export type UsersConstraintsError = {
    constraints: UserConstraints[];
}

export function isUsersConstraintsError(error: UsersConstraintsError | Error): error is UsersConstraintsError {
    return (error as UsersConstraintsError).constraints !== undefined;
}

export const DefaultUsersModel: UsersModel = {users: [], pagination: defaultPaginationData};

export const UsersService = {
    async query(signal?: AbortSignal, opts?: QueryOptions): Promise<PaginationResponse<User>> {
        const usersResponse = await Axios.get<UsersResponse|PaginationErrorResponse>("/users", {
            params: (opts?.exclude) ? {...opts, exclude: (opts?.exclude || []).join(",")} : opts,
            signal: signal
        } as any);
        if (usersResponse.status < 200 || usersResponse.status > 299) {
            if (usersResponse.status === 400) {
                const errorResponse = usersResponse.data as PaginationErrorResponse;
                throw new PaginationError(errorResponse);
            }

            throw new Error("failed to load user data: " + usersResponse.status);
        }
        return usersResponse.data as UsersResponse;
    },
    async get(signal?: AbortSignal, username?: string): Promise<User> {
        if (!username) {
            throw new Error("the user name must not be empty");
        }
        const userResponse = await Axios.get<User>(`/users/${username}`, {
            signal: signal
        } as any);
        if (userResponse.status < 200 || userResponse.status > 299) {
            throw new Error("failed to load user data: " + userResponse.status);
        }
        return userResponse.data as User;
    },
    async save(user: User): Promise<string> {
        try {
            await Axios.post("/users", removeNonRelevantUserFields(user));
            return t("newUser.notification.success", {username: user.username});
        } catch (e: AxiosError | unknown) {
            if (isAxiosError(e)) {
                const axiosError = e as AxiosError;
                if (axiosError.response?.status === 409) {
                    throw axiosError.response.data as UsersConstraintsError;
                }
            }
            throw new Error(t("newUser.notification.error", {username: user.username}));
        }
    },
    async update(user: User): Promise<string> {
        try {

            await Axios.put(`/users/${user.username}`, removeNonRelevantUserFields(user));
            return t("newUser.notification.success", {username: user.username});
        } catch (e: AxiosError | unknown) {
            throw new Error(t("newUser.notification.error", {username: user.username}));
        }
    },
    async delete(userName: string): Promise<void> {
        const usersResponse = await Axios.delete<UsersResponse>(`/users/${userName}`);
        if (usersResponse.status !== 204) {
            throw new Error("failed to delete user: " + usersResponse.status);
        }
    }
};

function removeNonRelevantUserFields(user: User) {
    return Object
        .keys(emptyUser)
        .reduce((newUser: any, key) => {
            newUser[key] = user[key as keyof User];
            return newUser;
        }, {});
}

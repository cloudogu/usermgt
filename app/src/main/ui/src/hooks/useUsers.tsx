import {DefaultUsersModel, UsersService} from "../services/Users";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";
import type {UsersModel} from "../services/Users";

export const useUsers = (opts: QueryOptions):{users: UsersModel, isLoading: boolean} => {
    const {data, isLoading} = useAPI<UsersModel, QueryOptions>(UsersService.find, opts);
    const users = data ?? DefaultUsersModel;
    return {users, isLoading};
};

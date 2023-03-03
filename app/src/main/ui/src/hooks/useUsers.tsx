import {DefaultUsersModel, UsersService} from "../services/Users";
import {useAPI} from "./useAPI";
import type {QueryOptions, StateSetter} from "./useAPI";
import type {UsersModel} from "../services/Users";

export const useUsers = (opts: QueryOptions): [UsersModel, boolean, StateSetter<UsersModel>] => {
    const [users, isLoading, setUsers] = useAPI<UsersModel>(UsersService.get, opts);
    return [users ?? DefaultUsersModel, isLoading, setUsers];
};

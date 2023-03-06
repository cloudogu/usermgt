import {QueryOptions, StateSetter, useAPI} from "./useAPI";
import {DefaultUsersModel, UsersModel, UsersService} from "../services/Users";

export const useUsers = (opts: QueryOptions): [UsersModel, boolean, StateSetter<UsersModel>] => {
    const [users, isLoading, setUsers] = useAPI<UsersModel>(UsersService.get, opts)
    return [users ?? DefaultUsersModel, isLoading, setUsers]
}

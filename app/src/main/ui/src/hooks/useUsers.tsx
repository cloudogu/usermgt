import {QueryOptions, StateSetter, useAPI} from "./useAPI";
import {User, UsersAPI} from "../api/UsersAPI";

export const useUsers = (opts: QueryOptions): [User[], boolean, StateSetter<User[]>] => {
    const [users, isLoading, setUsers] = useAPI<User[]>(UsersAPI.getAll, opts)
    return [users ?? [], isLoading, setUsers]
}

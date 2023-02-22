import {useAPI} from "./useAPI";
import {User, UsersAPI} from "../api/UsersAPI";

export const useUsers = (): [User[], boolean] => {
    const [users, isLoading] = useAPI<User[]>(UsersAPI.getAll)
    return [users ?? [], isLoading]
}

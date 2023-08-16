import {emptyUser} from "../services/Account";
import { UsersService} from "../services/Users";
import {useAPI} from "./useAPI";
import type {User} from "../services/Users";

export function useUser(username?: string) {
    const {data, isLoading, setData: setUser} = useAPI<User, string>(UsersService.get, username ?? "");
    const user = data ?? emptyUser;
    return {user, isLoading, setUser};
}

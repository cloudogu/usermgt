import {AccountService, emptyUser} from "../services/Account";
import {useAPI} from "./useAPI";
import type {ApiAccount} from "../services/Account";
import {User, UsersService} from "../services/Users";

export function useUser(username?: string) {
    const {data, isLoading, setData:setUser} = useAPI<User, string>(UsersService.getOne, username ?? "");
    const user = data ?? emptyUser;
    return {user, isLoading, setUser};
}

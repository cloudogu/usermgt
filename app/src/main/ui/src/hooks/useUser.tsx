import {emptyUser} from "../services/Account";
import {useAPI} from "./useAPI";
import {User, UsersService} from "../services/Users";

export function useUser(username?: string) {
    const {data, isLoading, setData: setUser} = useAPI<User, string>(UsersService.get, username ?? "");
    const user = data ?? emptyUser;
    return {user, isLoading, setUser};
}

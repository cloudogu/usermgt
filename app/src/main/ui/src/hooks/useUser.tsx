import {StateSetter, useAPI} from "./useAPI";
import {ApiUser, CasUserAPI} from "../api/CasUserAPI";

export const useUser = (): [ApiUser, boolean, StateSetter<ApiUser>] => {
    const [user, isLoading, setUser] = useAPI<ApiUser>(CasUserAPI.get)
    return [user ?? {principal: "default"}, isLoading, setUser]
}
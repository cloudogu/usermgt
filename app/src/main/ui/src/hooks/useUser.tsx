import {StateSetter, useAPI} from "./useAPI";
import {CasUser, CasUserAPI} from "../api/CasUserAPI";

export const useUser = (): [CasUser, boolean, StateSetter<CasUser>] => {
    const [user, isLoading, setUser] = useAPI<CasUser>(CasUserAPI.get)
    return [user ?? {principal: "default"}, isLoading, setUser]
}
import {StateSetter, useAPI} from "./useAPI";
import {CasUser, CasUserService} from "../services/CasUser";

export const useCasUser = (): [CasUser, boolean, StateSetter<CasUser>] => {
    const [user, isLoading, setUser] = useAPI<CasUser>(CasUserService.get)
    return [user ?? {principal: "default"}, isLoading, setUser]
}
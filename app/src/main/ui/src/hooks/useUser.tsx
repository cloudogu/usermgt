import {useAPI} from "./useAPI";
import {ApiUser, CasUserAPI} from "../api/CasUserAPI";

export const useUser = (): [ApiUser, boolean] => {
    const [user, isLoading] = useAPI<ApiUser>(CasUserAPI.get)
    return [user ?? {principal: "default"}, isLoading]
}
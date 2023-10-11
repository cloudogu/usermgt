import { CasUserService} from "../services/CasUser";
import { useAPI} from "./useAPI";
import type {CasUser} from "../services/CasUser";

export const useCasUser = () => {
    const {data} = useAPI<CasUser>(CasUserService.get);
    const user = data ?? {principal: "default", admin: false, loading: true};
    return {user};
};
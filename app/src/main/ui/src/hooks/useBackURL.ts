import {useLocation} from "react-router-dom";

export const BACK_URL_PARAM="backURL";

export function useBackURL(){
    const {search} = useLocation();
    const params = new URLSearchParams(search);
    const backURL = params.get(BACK_URL_PARAM);
    return {backURL};
}
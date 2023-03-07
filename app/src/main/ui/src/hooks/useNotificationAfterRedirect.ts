import {useEffect} from "react";
import {useLocation} from "react-router-dom";

export function useNotificationAfterRedirect(notify: (_message:string, _type:"primary"|"danger") => void) {
    const {state} = useLocation();
    useEffect(() => {
        state && state.alert && notify(state.alert.message, state.alert.variant);
        window.history.replaceState({}, document.title);
    }, []);
}
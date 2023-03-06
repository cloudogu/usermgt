import {useEffect} from "react";
import {useLocation} from "react-router-dom";

export function useNotificationAfterRedirect(notify: (_message:string, _type:"primary"|"danger") => void) {
    const {state} = useLocation();
    useEffect(() => {
        state && notify(state.message, state.variant);
        window.history.replaceState({}, document.title);
    }, []);
}
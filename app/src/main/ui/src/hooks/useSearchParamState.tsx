import {useCallback, useEffect} from "react";
import {useSearchParams} from "react-router-dom";

export default function useSearchParamState(urlParamName: string, defaultValue: string) {
    const [searchParams, setSearchParams] = useSearchParams();
    const state = searchParams.get(urlParamName) ?? defaultValue;
    const setState = useCallback((newState: string) => {
        const searchParams = new URLSearchParams(window.location.search);
        searchParams.set(urlParamName, newState);
        setSearchParams(searchParams);
    }, [setSearchParams, window.location.search]);

    useEffect(() => {
        if (`${state}` !== searchParams.get(urlParamName)) {
            setState(`${state}`);
        }
    }, [state]);

    return [
        state,
        setState,
    ] as const;
}
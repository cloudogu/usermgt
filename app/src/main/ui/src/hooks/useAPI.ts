import {useEffect, useState} from "react";

export type QueryOptions = { page: number; page_size: number; query: string; exclude?: string[]; context?: string }

export type AbortableCallbackWithArgs<T, Y> = (_signal?: AbortSignal, _args?: Y) => Promise<T>
export type AbortableCallback<T> = (_signal?: AbortSignal) => Promise<T>

export interface UseApiResponse<T> {
    data: T | undefined,
    setData: (_: T) => void,
    isLoading: boolean,
    error: any
}

export function useAPI<T, Y>(_callBack: AbortableCallbackWithArgs<T, Y>, _args?: Y): UseApiResponse<T>;
export function useAPI<T>(_callBack: AbortableCallback<T>): UseApiResponse<T>;

export function useAPI<T, Y>(callBack: AbortableCallbackWithArgs<T, Y> | AbortableCallback<T>, args?: Y): UseApiResponse<T> {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error>();
    useEffect(() => {
        const abortController = new AbortController();
        setIsLoading(true);
        callBack(abortController?.signal, args)
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => {
                if (!abortController.signal.aborted) {
                    setError(err);
                    console.error(err);
                }
            });
        return () => {
            abortController.abort();
        };
    }, [args]);

    return {data, setData, isLoading, error};
}

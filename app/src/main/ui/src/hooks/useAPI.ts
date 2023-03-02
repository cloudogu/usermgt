import {Dispatch, SetStateAction, useEffect, useState} from "react";
import {CanceledError} from "axios";

export type QueryOptions = {start: number; limit: number; query: string;}

export type StateSetter<T> = Dispatch<SetStateAction<T | undefined>>;
export type AbortableCallbackWithOptions<T> = (signal?: AbortSignal, opts?: QueryOptions) => Promise<T>

export function useAPI<T>(callBack: AbortableCallbackWithOptions<T>, opts?: QueryOptions): [T | undefined , boolean, StateSetter<T>] {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    useEffect(() => {
        const abortController = new AbortController();
        setIsLoading(true);
        callBack(abortController?.signal, opts)
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => {
                if(err instanceof CanceledError) {
                    // ignore canceled requests
                } else {
                    console.error(err)
                }
                });
        return () => {
            abortController.abort();
        }
    }, [opts]);

    return [data, isLoading, setData];
}
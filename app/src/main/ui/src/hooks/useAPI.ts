import { useEffect, useState} from "react";
import type {Dispatch, SetStateAction} from "react";

export type QueryOptions = {start: number; limit: number; query: string;}

export type StateSetter<T> = Dispatch<SetStateAction<T | undefined>>;
export type AbortableCallbackWithOptions<T> = (_signal?: AbortSignal, _opts?: QueryOptions, _args?: any) => Promise<T>
export type AbortableCallbackWithArgs<T> = (_signal?: AbortSignal, _args?: any) => Promise<T>
export type AbortableCallback<T> = (_signal?: AbortSignal) => Promise<T>

export function useAPI<T>(_callBack: AbortableCallbackWithOptions<T>, _opts: QueryOptions): [T | undefined , boolean, StateSetter<T>];
export function useAPI<T>(_callBack: AbortableCallbackWithArgs<T>, _args?: any): [T | undefined , boolean, StateSetter<T>];
export function useAPI<T>(_callBack: AbortableCallback<T>, _args?: any): [T | undefined , boolean, StateSetter<T>];

export function useAPI<T>(callBack: AbortableCallbackWithOptions<T> | AbortableCallbackWithArgs<T>, opts?: QueryOptions, args?: any): [T | undefined , boolean, StateSetter<T>] {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    useEffect(() => {
        const abortController = new AbortController();
        setIsLoading(true);
        callBack(abortController?.signal, opts, args)
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => {
                if(!abortController.signal.aborted) {
                    console.error(err);
                }
            });
        return () => {
            abortController.abort();
        };
    }, [opts]);

    return [data, isLoading, setData];
}
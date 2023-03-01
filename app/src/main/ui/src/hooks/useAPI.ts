import {Dispatch, SetStateAction, useEffect, useState} from "react";

export class QueryOptions {
    private readonly start?: number;
    private readonly limit?: number;
    private readonly query?: string;

    constructor(start?: number, limit?: number, query?: string) {
        this.start = start;
        this.limit = limit;
        this.query = query;
    }

    get queryString(): string {
        return this.query ?? "";
    }

}

export type StateSetter<T> = Dispatch<SetStateAction<T | undefined>>;
export type AbortableCallbackWithOptions<T> = (signal?: AbortSignal, opts?: QueryOptions) => Promise<T>

export function useAPI<T>(callBack: AbortableCallbackWithOptions<T>, opts?: QueryOptions): [T | undefined , boolean, StateSetter<T>] {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const abortController = new AbortController();
    useEffect(() => {
        callBack(abortController?.signal, opts)
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => console.error(err));
        return () => {
            abortController?.abort();
        }
    }, [opts]);

    return [data, isLoading, setData];
}
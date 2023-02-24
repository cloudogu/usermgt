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

    public toString = () : string => {
        return `start: ${this.start} | limit: ${this.limit} | query: ${this.query}`;
    }
}

export type StateSetter<T> = Dispatch<SetStateAction<T | undefined>>;

export function useAPI<T>(callBack: (opts?: QueryOptions) => Promise<T>, opts?: QueryOptions): [T | undefined , boolean, StateSetter<T>] {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    useEffect(() => {
        callBack(opts)
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => console.error(err));
    }, [opts]);

    return [data, isLoading, setData];
}
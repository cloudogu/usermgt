import {useEffect, useState} from "react";

export function useAPI<T>(callBack: () => Promise<T>): [T | undefined , boolean] {
    const [data, setData] = useState<T>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    useEffect(() => {
        callBack()
            .then(data => {
                setData(data);
                setIsLoading(false);
            })
            .catch(err => console.error(err));
    }, []);

    return [data, isLoading];
}
import {useMemo} from "react";
import {DogusService, toDoguOptions} from "../services/Dogus";
import {useAPI} from "./useAPI";

export function useDogus() {
    const {data, isLoading, error} = useAPI(DogusService.getAll);
    const dogus = data ?? [];
    const doguOptions = useMemo(() => toDoguOptions(dogus), [dogus]);

    return {dogus, doguOptions, isLoading, error};
}

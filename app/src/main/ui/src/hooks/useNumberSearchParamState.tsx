import useSearchParamState from "./useSearchParamState";

export default function useNumberSearchParamState(urlParamName: string, defaultValue: number) {
    const {state, setState, synchronized} = useSearchParamState(urlParamName, `${defaultValue}`);
    return {state: Number(state), setState: ((value: number) => setState(`${value}`)), synchronized};
}
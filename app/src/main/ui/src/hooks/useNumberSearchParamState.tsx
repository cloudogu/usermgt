import useSearchParamState from "./useSearchParamState";

export default function useNumberSearchParamState(urlParamName: string, defaultValue: number) {
    const [state, setState] = useSearchParamState(urlParamName, `${defaultValue}`);
    return [Number(state), ((value: number) => setState(`${value}`))] as const;
}
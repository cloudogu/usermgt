import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";

export type Dogu = {
    name: string;
    displayName: string;
    tags: string[];
};

export type DoguOption = {
    value: string;
    label: string;
    tags: string[];
};

export const DogusService = {
    async getAll(signal?: AbortSignal): Promise<Dogu[]> {
        const response = await Axios.get<Dogu[]>("/dogus", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load dogus: " + response.status);
        }
        return response.data;
    },
};

export const toDoguOptions = (dogus: Dogu[]): DoguOption[] => dogus
    .map(dogu => ({value: dogu.name, label: dogu.displayName || dogu.name, tags: dogu.tags}))
    .sort((left, right) => left.label.localeCompare(right.label));

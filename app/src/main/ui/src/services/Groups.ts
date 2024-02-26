import {isAxiosError} from "axios";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import type {QueryOptions} from "../hooks/useAPI";
import {PaginationError, PaginationErrorResponse, PaginationResponse} from "../hooks/usePaginatedData";
import type {AxiosError, AxiosResponse} from "axios";

export type GroupsResponse = PaginationResponse<Group>;

export type Group = {
    name: string;
    description: string;
    members: string[];
    isSystemGroup?: boolean;
}

export type UndeletableGroupsResponse = string[];

export const GroupsService = {
    async query(signal?: AbortSignal, opts?: QueryOptions): Promise<PaginationResponse<Group>> {
        try {
            const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
                params: (opts?.exclude) ? {...opts, exclude: (opts?.exclude || []).join(",")} : opts,
                signal: signal
            } as any);

            const undeletableGroupsResponse = await Axios<UndeletableGroupsResponse>("/groups/undeletable", {
                signal: signal
            } as any);

            const groupsData = groupsResponse.data;
            const undeletableGroupsData = undeletableGroupsResponse.data;
            groupsData.data = mapSystemGroups(groupsData.data, undeletableGroupsData);

            return groupsData;
        } catch (err: any) {
            const status = err.response.status;
            if (status === 400) {
                const errorResponse = err.response.data as PaginationErrorResponse;
                throw new PaginationError(errorResponse);
            }

            throw new Error("failed to load group data: " + err.message);
        }
    },
    async get(signal?: AbortSignal, groupName?: string): Promise<Group> {
        if (!groupName) {
            throw new Error("the group name must not be empty");
        }
        const groupResponse: AxiosResponse<any> = await Axios.get<Group>(`/groups/${groupName}`, {
            signal: signal
        } as any);
        if (groupResponse.status < 200 || groupResponse.status > 299) {
            throw new Error("failed to load group data: " + groupResponse.status);
        }// set empty string if given name is null
        // formik expects each form value to be at least undefined or empty
        if (groupResponse.data.description === null) {
            groupResponse.data.description = "";
        }
        return groupResponse.data;
    },
    async save(group: Group): Promise<void> {
        try {
            await Axios.post("/groups", group);
        } catch (e: AxiosError | unknown) {
            if (isAxiosError(e)) {
                const axiosError = e as AxiosError;
                if (axiosError.response?.status === 409) {
                    throw new Error(t("newGroup.notification.errorDuplicate"));
                }
            }
            throw new Error(t("newGroup.notification.error"));
        }
    },
    async update(group: Group): Promise<void> {
        try {
            await Axios.put(`/groups/${group.name}`, group);
        } catch (e: AxiosError | unknown) {
            throw new Error(t("newGroup.notification.error"));
        }
    },
    async delete(groupName: string): Promise<void> {
        const response = await Axios.delete(`/groups/${groupName}`);
        if (response.status !== 204) {
            throw new Error(`failed to delete group '${groupName}': ${response.status}`);
        }
    },
};

const mapSystemGroups = (groups: Group[], undeletableGroups: UndeletableGroupsResponse): Group[] => groups.map(grp => {
    if (undeletableGroups.includes(grp.name)) {
        grp.isSystemGroup = true;
    }
    return grp;
});

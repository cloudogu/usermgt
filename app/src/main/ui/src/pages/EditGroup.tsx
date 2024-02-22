import {H1, useAlertNotification} from "@cloudogu/deprecated-ces-theme-tailwind";
import React from "react";
import {useNavigate, useParams} from "react-router-dom";
import * as Yup from "yup";
import {GroupForm} from "../components/groups/GroupForm";
import {t} from "../helpers/i18nHelpers";
import {useBackURL} from "../hooks/useBackURL";
import {useGroup} from "../hooks/useGroup";
import { GroupsService} from "../services/Groups";
import type {Group} from "../services/Groups";
import type { FormHandlerConfig} from "@cloudogu/deprecated-ces-theme-tailwind";

export function EditGroup() {
    const {groupName} = useParams();
    const {group, isLoading} = useGroup(groupName);
    const {notification, notify} = useAlertNotification();
    const {backURL} = useBackURL();
    const navigate = useNavigate();

    const handlerConfig: FormHandlerConfig<Group> = {
        enableReinitialize: true,
        initialValues: group,
        validationSchema: Yup.object(),
        onSubmit: (group: Group) => {
            GroupsService.update(group).then(() => {
                navigate(backURL ?? "/groups", {state: successData(group)});
            }).catch((e: Error) => {
                notify(e.message, "danger");
            });
        }
    };

    return <>
        <H1>{t("pages.groupsEdit")}</H1>
        {notification}
        {!isLoading &&
            <GroupForm
                group={group ?? {name: "", description: "", members: []}}
                config={handlerConfig}
            />
        }
    </>;
}

const successData = (group: Group) => ({alert: {
    message: t("editGroup.notification.success", {groupName: group.name}),
    variant: "primary"
}});

import { H1, useAlertNotification} from "@cloudogu/ces-theme-tailwind";
import {useNavigate} from "react-router-dom";
import {t} from "../helpers/i18nHelpers";
import {useBackURL} from "../hooks/useBackURL";
import {useGroupValidationSchema} from "../hooks/useGroupValidationSchema";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import { GroupsService} from "../services/Groups";
import {GroupForm} from "../components/groups/GroupForm";
import type {Group} from "../services/Groups";
import type {FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";

export function NewGroup(props: { title: string }) {
    useSetPageTitle(props.title);
    const {notification, notify} = useAlertNotification();
    const {backURL} = useBackURL();
    const navigate = useNavigate();
    const validationSchema = useGroupValidationSchema();
    const handlerConfig: FormHandlerConfig<Group> = {
        enableReinitialize: true,
        initialValues: {name: "", description: "", members: []},
        validationSchema: validationSchema,
        onSubmit: (group: Group) => {
            GroupsService.save(group).then(() => {
                navigate(backURL ?? "/groups", {state: successData(group)});
            }).catch((e: Error) => {
                notify(e.message, "danger");
            });
        }
    };
    return <>
        <H1>{t("pages.groupsNew")}</H1>
        {notification}
        <GroupForm
            group={{name: "", description: "", members: []}}
            config={handlerConfig}
        />
    </>;
}

const successData = (group: Group) => ({alert: {
    message: t("newGroup.notification.success", {groupName: group.name}),
    variant: "primary"
}});
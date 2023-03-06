import {  H1} from "@cloudogu/ces-theme-tailwind";
import {useParams} from "react-router-dom";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useGroup} from "../../hooks/useGroup";
import {useNotificationAfterRedirect} from "../../hooks/useNotificationAfterRedirect";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {GroupsService} from "../../services/Groups";
import {GroupForm} from "./GroupForm";

export function EditGroup(props: { title: string }) {
    useSetPageTitle(props.title);
    const { notification, notify } = useChangeNotification();
    useNotificationAfterRedirect(notify);
    const {groupName} = useParams();
    const {data:group} = useGroup(groupName);
    return <>
        <H1>{t("pages.groupsEdit")}</H1>
        {notification}
        <GroupForm
            group={group ?? {name: "", description: "", members: []}}
            onSubmit={GroupsService.update}
            notify={notify}
        />
    </>;
}
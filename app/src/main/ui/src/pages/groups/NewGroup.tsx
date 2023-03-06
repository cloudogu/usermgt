import {H1} from "@cloudogu/ces-theme-tailwind";
import {useNavigate} from "react-router-dom";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {GroupsService} from "../../services/Groups";
import {GroupForm} from "./GroupForm";
import type {Group} from "../../services/Groups";

export function NewGroup(props: { title: string }) {
    useSetPageTitle(props.title);
    const {notification, notify} = useChangeNotification();
    const navigate = useNavigate();

    const notifyAfterRedirect = (message: string, variant: "primary" | "danger", group?: Group) => {
        notify(message, variant);
        if (group && group.name) {
            navigate(`/groups/${group.name}/edit`, {
                state: {
                    message: message,
                    variant: variant
                }
            });
        }
    };

    const handleSubmit = (grp: Group): Promise<void> => GroupsService.save(grp);
    return <>
        <H1>{t("pages.groupsNew")}</H1>
        {notification}
        <GroupForm
            group={{name: "", description: "", members: []}}
            onSubmit={handleSubmit}
            notify={notifyAfterRedirect}
        />
    </>;
}
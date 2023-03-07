import {H1} from "@cloudogu/ces-theme-tailwind";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {GroupsService} from "../../services/Groups";
import {GroupForm} from "./GroupForm";

export function NewGroup(props: { title: string }) {
    useSetPageTitle(props.title);
    const {notification} = useChangeNotification();

    return <>
        <H1>{t("pages.groupsNew")}</H1>
        {notification}
        <GroupForm
            group={{name: "", description: "", members: []}}
            onSubmit={GroupsService.save}
        />
    </>;
}
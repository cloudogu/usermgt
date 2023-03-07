import {Button, H1} from "@cloudogu/ces-theme-tailwind";
import {cl} from "dynamic-class-list";
import React from "react";
import {useNavigate, useParams} from "react-router-dom";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {t} from "../../helpers/i18nHelpers";
import {useBackURL} from "../../hooks/useBackURL";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useConfirmation} from "../../hooks/useConfirmation";
import {useGroup} from "../../hooks/useGroup";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {GroupsService} from "../../services/Groups";
import {GroupForm} from "./GroupForm";

export function EditGroup(props: { title: string }) {
    useSetPageTitle(props.title);
    const {groupName} = useParams();
    const {group, isLoading} = useGroup(groupName);
    const {notification, notify} = useChangeNotification();
    const {open, setOpen, setTargetName} = useConfirmation();
    const {backURL} = useBackURL();
    const navigate = useNavigate();

    const openConfirmationDialog = (groupName: string): void => {
        setOpen(true);
        setTargetName(groupName);
    };
    const onDelete = () => {
        GroupsService.delete(group.name).then(() => {
            navigate(backURL ?? "/groups", {
                state: {
                    alert: {
                        message: t("groups.notification.success", {groupName: group.name}),
                        variant: "primary"
                    }
                }
            });
        }).catch(() => {
            notify(t("groups.notification.error", {groupName: group.name}), "danger");
        });
    };
    return <>
        <div className={"flex justify-between"}>
            <H1>{t("pages.groupsEdit")}</H1>
            <Button variant={"danger"} type={"button"} className={cl("mt-5 mb-2.5 uppercase")}
                onClick={() => openConfirmationDialog(groupName ?? "")}>
                {t("editGroup.buttons.remove")}
            </Button>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
            onClose={() => setOpen(false)}
            onConfirm={onDelete}
            title={t("groups.confirmation.title")}
            message={t("groups.confirmation.message", {groupName: groupName})}/>
        {!isLoading ?
            <GroupForm
                group={group ?? {name: "", description: "", members: []}}
                onSubmit={GroupsService.update}
            /> : <></>
        }
    </>;
}
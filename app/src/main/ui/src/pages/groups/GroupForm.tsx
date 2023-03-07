import {Button, Form, H2, Table, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import * as Yup from "yup";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {DeleteButton} from "../../components/DeleteButton";
import {t} from "../../helpers/i18nHelpers";
import {useBackURL} from "../../hooks/useBackURL";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useConfirmation} from "../../hooks/useConfirmation";
import {useGroupValidationSchema} from "../../hooks/useGroupValidationSchema";
import {Prompt} from "../../hooks/usePrompt";
import type {Group} from "../../services/Groups";

type GroupFormProps = {
    group: Group;
    onSubmit: (_: Group) => Promise<void>;
}

export function GroupForm({group, onSubmit}: GroupFormProps) {
    const validationSchema = useGroupValidationSchema();
    const {notification, notify} = useChangeNotification();
    const {backURL} = useBackURL();
    const navigate = useNavigate();
    const isNewGroup = !group.name;
    const [members, setMembers] = useState(group.members);
    const handler = useFormHandler<Group>({
        enableReinitialize: true,
        initialValues: group,
        validationSchema: isNewGroup ? validationSchema : Yup.object(),
        onSubmit: (group: Group, {setFieldError}) => {
            onSubmit(group).then(() => {
                navigate(backURL ?? "/groups", {
                    state: {
                        alert: {
                            message: t("editGroup.notification.success", {groupName: group.name}),
                            variant: "primary"
                        }
                    }
                });
            }).catch((e: Error) => {
                setFieldError("name", "duplicate");
                notify(e.message, "danger");
            });
        }
    });

    const {open, setOpen: toggleModal, targetName: username, setTargetName: setUsername} = useConfirmation();
    const openConfirmationDialog = (username: string): void => {
        setUsername(username);
        toggleModal(true);
    };

    const onConfirmDeleteMember = async (memberName: string) => {
        setMembers(currentMembers => removeMember(currentMembers, memberName));
        toggleModal(false);
    };
    return <>
        <ConfirmationDialog open={open ?? false}
            onClose={() => toggleModal(false)}
            onConfirm={async () => {
                await onConfirmDeleteMember(username ?? "");
            }}
            title={t("users.confirmation.title")}
            message={t("users.confirmation.message", {username: username})}/>
        <Prompt when={handler.dirty} message={"Halt Stop!"} />
        {notification}
        <Form handler={handler}>
            <Form.ValidatedTextInput type={"text"} name={"name"} disabled={!isNewGroup}>
                {t("editGroup.labels.name")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextArea name={"description"}>
                {t("editGroup.labels.description")}
            </Form.ValidatedTextArea>
            <H2>Mitglieder</H2>
            <Table className="my-4 text-sm">
                <Table.Head>
                    <Table.Head.Tr className={"uppercase"}>
                        <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                        <Table.Head.Th className="w-0"/>
                    </Table.Head.Tr>
                </Table.Head>
                <Table.Body>
                    {(members ?? []).map(user => createUsersRow(user, openConfirmationDialog))}
                </Table.Body>
            </Table>
            <div className={"my-4"}>
                <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>
                    {t("editGroup.buttons.save")}
                </Button>
                <Button variant={"secondary"} type={"button"} className={"ml-4"}
                    onClick={() => navigate(backURL ?? "/groups")}>
                    {t("editGroup.buttons.back")}
                </Button>
            </div>
        </Form>
    </>;
}

function createUsersRow(user: string, onDelete: (_username: string) => void) {
    return <Table.Body.Tr key={user}>
        <Table.Body.Td className="font-bold">{user}</Table.Body.Td>
        <Table.Body.Td className="flex justify-center">
            <DeleteButton
                type={"button"}
                title={t("users.table.actions.delete")}
                onClick={() => onDelete(user)}/>
        </Table.Body.Td>
    </Table.Body.Tr>;
}

const removeMember = (members: string[], toRemove: string) => {
    const index = members.indexOf(toRemove);
    if (index <= 0) {
        return members;
    }
    members.splice(index, 1);
    return members;
};

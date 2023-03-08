import {
    Button,
    Form,
    H2, SearchbarAutocomplete,
    Table,
    useFormHandler
} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {DeleteButton} from "../../components/DeleteButton";
import {t} from "../../helpers/i18nHelpers";
import {useBackURL} from "../../hooks/useBackURL";
import {useConfirmation} from "../../hooks/useConfirmation";
import {Prompt} from "../../hooks/usePrompt";
import {UsersService} from "../../services/Users";
import type {Group} from "../../services/Groups";
import type {FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";

type GroupFormProps<T> = {
    group: Group;
    config: FormHandlerConfig<T>
}

export function GroupForm({group, config}: GroupFormProps<Group>) {
    const {backURL} = useBackURL();
    const navigate = useNavigate();
    const isNewGroup = !group.name;
    const handler = useFormHandler<Group>(config);
    const {open, setOpen: toggleModal, targetName: username, setTargetName: setUsername} = useConfirmation();
    const [users, setUsers] = useState<string[]>([]);
    const openConfirmationDialog = (username: string): void => {
        setUsername(username);
        toggleModal(true);
    };

    const onConfirmDeleteMember = (memberName: string) => {
        const newMembers = removeMember([...handler.values.members], memberName);
        handler.setValues({...handler.values, members: newMembers});
        toggleModal(false);
    };

    const addMember = (value: string) => {
        const newMembers = [...handler.values.members, value];
        handler.setValues({...handler.values, members: newMembers});
    };
    return <>
        <ConfirmationDialog open={open ?? false}
            onClose={() => toggleModal(false)}
            onConfirm={async () => {
                await onConfirmDeleteMember(username ?? "");
            }}
            title={t("users.confirmation.title")}
            message={t("users.confirmation.message", {username: username})}/>
        <Prompt when={handler.dirty && !handler.isSubmitting} message={"Das Formular enthält ungespeicherte Änderungen. Wollen Sie die Seite wirklich verlassen?"} />
        <Form handler={handler}>
            <Form.ValidatedTextInput type={"text"} name={"name"} disabled={!isNewGroup}>
                {t("groups.labels.name")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextArea name={"description"}>
                {t("groups.labels.description")}
            </Form.ValidatedTextArea>
            <H2>Mitglieder</H2>
            <SearchbarAutocomplete
                searchResults={users.map(x => <SearchbarAutocomplete.SearchResult type={"button"} key={x} value={x} />)}
                onSelectItem={(val: string, item) => {
                    addMember(val);
                    item.value = "";
                    setUsers([]);
                }}
                onTrigger={async (val) => {
                    const userData = await UsersService.get(undefined, {start: 0, limit: 5, query: val});
                    const newUsers = userData.users.map(x => x.username);
                    const shouldUpdateResultList = containsNewUsers(users, newUsers);
                    if (shouldUpdateResultList){
                        setUsers(newUsers);
                    }
                }}
                onCancelSelection={(item) => {
                    item.value = "";
                    setUsers([]);
                }}>
                Mitglied hinzufügen
            </SearchbarAutocomplete>
            <Table className="my-4 text-sm">
                <Table.Head>
                    <Table.Head.Tr className={"uppercase"}>
                        <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                        <Table.Head.Th className="w-0"/>
                    </Table.Head.Tr>
                </Table.Head>
                <Table.Body>
                    {(handler.values.members ?? []).map((user) => <Table.Body.Tr key={user}>
                        <Table.Body.Td className="font-bold">{user}</Table.Body.Td>
                        <Table.Body.Td className="flex justify-center">
                            <DeleteButton
                                type={"button"}
                                title={t("users.table.actions.delete")}
                                onClick={() => openConfirmationDialog(user)}/>
                        </Table.Body.Td>
                    </Table.Body.Tr>)}
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

const containsNewUsers = (oldList: string[], newList: string[]) => {
    if (oldList.length < newList.length) {
        return true;
    }
    for (const newItem in newList){
        if(oldList.indexOf(newItem) >= 0){
            return true;
        }
    }
    return false;
};

const removeMember = (members: string[], toRemove: string) => {
    const index = members.indexOf(toRemove);
    if (index < 0) {
        return members;
    }
    members.splice(index, 1);
    return members;
};

import {
    Button,
    Form,
    H2, 
    useFormHandler
} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate} from "react-router-dom";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {t} from "../../helpers/i18nHelpers";
import {useBackURL} from "../../hooks/useBackURL";
import {useConfirmation} from "../../hooks/useConfirmation";
import {Prompt} from "../../hooks/usePrompt";
import {UsersService} from "../../services/Users";
import {Members} from "./Members";
import type {QueryOptions} from "../../hooks/useAPI";
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
        const memberAlreadyAdded = handler.values.members.indexOf(value) >= 0;
        if (memberAlreadyAdded) {
            return;
        }
        const newMembers = [...handler.values.members, value];
        handler.setValues({...handler.values, members: newMembers});
    };

    const loadMembers = async (opts: QueryOptions): Promise<string[]> => {
        const userData = await UsersService.get(undefined, opts);
        return userData.users.map(x => x.username);
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
            <H2>{`${t("groups.labels.members")} (${handler.values.members.length})`}</H2>
            <Members entries={handler.values.members} loadFn={loadMembers} addEntry={addMember} removeEntry={openConfirmationDialog} />
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

const removeMember = (members: string[], toRemove: string) => {
    const index = members.indexOf(toRemove);
    if (index < 0) {
        return members;
    }
    members.splice(index, 1);
    return members;
};

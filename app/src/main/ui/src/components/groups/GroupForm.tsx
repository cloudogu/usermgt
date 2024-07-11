import {Button, Form, H2, ListWithSearchbar, useFormHandler} from "@cloudogu/deprecated-ces-theme-tailwind";
import {TrashIcon} from "@heroicons/react/24/outline";
import {useMemo} from "react";
import {useNavigate} from "react-router-dom";
import {t} from "../../helpers/i18nHelpers";
import {useBackURL} from "../../hooks/useBackURL";
import {useConfirmation} from "../../hooks/useConfirmation";
import {Prompt} from "../../hooks/usePrompt";
import { UsersService} from "../../services/Users";
import {ConfirmationDialog} from "../ConfirmationDialog";
import type {Group} from "../../services/Groups";
import type {User} from "../../services/Users";
import type {FormHandlerConfig} from "@cloudogu/deprecated-ces-theme-tailwind";

type GroupFormProps<T> = {
    group: Group;
    config: FormHandlerConfig<T>
}

const MAX_SEARCH_RESULTS = 10;

export function GroupForm({group, config}: GroupFormProps<Group>) {
    const {backURL} = useBackURL();
    const navigate = useNavigate();
    const isNewGroup = !group.name;
    // Workaround with cast to any. Our interface does not accept validateOnbChange/Blur-values but the underlying lib does.
    const _handler = useFormHandler<Group>({...config, validateOnChange: false, validateOnBlur: false} as any);

    // As we now only validate on submit but the inputs of the old theme change state to "success" if touched and no error exists, we now have to make touched dependent of submit count
    const handler = useMemo(() => {
        const mockTouched = new Proxy<Map<string, boolean>>(new Map(), {
            get: (_, key) =>  _handler.submitCount > 0 && (_handler.errors as any)[key] !== undefined,
        });

        return {..._handler, touched: mockTouched as any};
    }, [_handler]);

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

    const loadMembers = async (searchValue: string): Promise<string[]> => {
        const userData = await UsersService.query(undefined, {page: 1, page_size: MAX_SEARCH_RESULTS, query: searchValue, exclude: handler.values.members});
        return userData.data.map((x: User) => x.username);
    };

    return <>
        <ConfirmationDialog open={open ?? false}
            data-testid="remove-member-dialog"
            onClose={() => toggleModal(false)}
            onConfirm={() => {
                onConfirmDeleteMember(username ?? "");
            }}
            title={t("groups.labels.removeMember")}
            message={t("groups.labels.removeMemberConfirmationMessage", {username: username})}/>
        <Prompt when={handler.dirty && !handler.isSubmitting} message={t("generic.notification.form.prompt")} />
        <Form handler={handler} data-testid="group-from">
            <Form.ValidatedTextInput type={"text"} name={"name"} disabled={!isNewGroup} data-testid="name" placeholder={t("groups.placeholder.name")}>
                {t("groups.labels.name")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextArea name={"description"} data-testid="description" placeholder={t("groups.placeholder.description")}>
                {t("groups.labels.description") + " (" + t("general.optional") + ")"}
            </Form.ValidatedTextArea>
            <H2>{`${t("groups.labels.members")} (${handler.values.members.length})`}</H2>
            <ListWithSearchbar
                data-testid="members"
                items={handler.values.members}
                addItem={addMember}
                removeItem={openConfirmationDialog}
                queryItems={loadMembers}
                tableTitle={t("groups.labels.name")}
                addLable={t("groups.labels.addMember") + " (" + t("general.optional") + ")"}
                removeLable={t("groups.labels.removeMember")}
                emptyItemsLable={t("groups.labels.emptyMembers")}
                removeIcon={<TrashIcon className={"w-6 h-6"}/>}
            />
            <div className={"my-4"}>
                <Button variant={"primary"} type={"submit"} disabled={!handler.dirty} data-testid="save-button">
                    {t("editGroup.buttons.save")}
                </Button>
                <Button variant={"secondary"} type={"button"} className={"ml-4"}
                    onClick={() => navigate(backURL ?? "/groups")}  data-testid="back-button">
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

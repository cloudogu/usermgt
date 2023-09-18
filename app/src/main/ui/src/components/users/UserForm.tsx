import {Button, Form, H2, ListWithSearchbar} from "@cloudogu/ces-theme-tailwind";
import {TrashIcon} from "@heroicons/react/24/outline";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import {useConfirmation} from "../../hooks/useConfirmation";
import {Prompt} from "../../hooks/usePrompt";
import useUserFormHandler from "../../hooks/useUserFormHandler";
import {GroupsService} from "../../services/Groups";
import {ConfirmationDialog} from "../ConfirmationDialog";
import type {User} from "../../services/Users";
import type {NotifyFunction, UseFormHandlerFunctions} from "@cloudogu/ces-theme-tailwind";

const MAX_SEARCH_RESULTS = 10;

export type OnSubmitUserForm<T extends User> = (_values: T, _notify: NotifyFunction, _handler: UseFormHandlerFunctions<T>) => Promise<void> | void;

export interface UserFormProps<T extends User> {
    initialUser: T;
    additionalButtons?: JSX.Element;
    disableUsernameField?: boolean;
    onSubmit: OnSubmitUserForm<T>;
    backButton?: boolean;
    groupsReadonly?: boolean;
    passwordReset?: boolean;
}

export default function UserForm<T extends User>(props: UserFormProps<T>) {
    const {
        handler,
        notification,
        notify
    } = useUserFormHandler<T>(props.initialUser, (values: T) => props.onSubmit(values, notify, handler));
    const {open, setOpen: toggleModal, targetName: groupName, setTargetName: setGroupName} = useConfirmation();

    const addGroup = (groupName: string): void => {
        if (handler.values.memberOf.indexOf(groupName) < 0) {
            const newGroups = [...handler.values.memberOf, groupName];
            handler.setValues({...handler.values, memberOf: newGroups});
        }
    };

    const openConfirmationRemoveGroupDialog = (groupName: string): void => {
        setGroupName(groupName);
        toggleModal(true);
    };

    const removeGroup = (groupName: string): void => {
        const groups = [...handler.values.memberOf];
        const index = groups.indexOf(groupName);
        if (index >= 0) {
            groups.splice(index, 1);
            handler.setFieldValue("memberOf", groups);
        }
        toggleModal(false);
    };

    const queryGroups = async (searchValue: string): Promise<string[]> => {
        const groupsData = await GroupsService.list(
            undefined,
            {
                start: 0,
                limit: MAX_SEARCH_RESULTS,
                query: searchValue,
                exclude: handler.values.memberOf ?? [],
            }
        );
        return groupsData.data.map(x => x.name);
    };

    const renderGroupsList = (readonly = false, pageSize = 5) => (
        <ListWithSearchbar
            data-testid="groups"
            readonly={readonly}
            items={handler.values.memberOf}
            addItem={addGroup}
            removeItem={openConfirmationRemoveGroupDialog}
            queryItems={queryGroups}
            tableTitle={t("groups.table.name")}
            addLable={t("users.labels.addGroup")}
            removeLable={t("users.labels.removeGroup")}
            emptyItemsLable={t("users.labels.emptyGroups")}
            removeIcon={<TrashIcon className={"w-6 h-6"}/>}
            pageSize={pageSize}
        />);

    return <>
        <ConfirmationDialog
            open={open ?? false}
            data-testid="remove-group-dialog"
            onClose={() => toggleModal(false)}
            onConfirm={async () => {
                await removeGroup(groupName ?? "");
            }}
            title={t("users.labels.removeGroup")}
            message={t("users.labels.removeGroupConfirmationMessage", {groupName: groupName})}/>
        <Prompt when={handler.dirty && !handler.isSubmitting} message={t("generic.notification.form.prompt")}/>
        <Form handler={handler}>
            {notification}
            <Form.ValidatedTextInput type={"text"} name={"username"} disabled={props.disableUsernameField ?? true}
                data-testid="username" placeholder={t("users.placeholder.username")}>
                {t("editUser.labels.username")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"text"} name={"givenname"} data-testid="givenname"
                placeholder={t("users.placeholder.givenname")}>
                {t("editUser.labels.givenName")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"text"} name={"surname"} data-testid="surname"
                placeholder={t("users.placeholder.surname")}>
                {t("editUser.labels.surname")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"text"} name={"displayName"} data-testid="displayName"
                placeholder={t("users.placeholder.displayName")}>
                {t("editUser.labels.displayName")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"text"} name={"mail"} data-testid="mail"
                placeholder={t("users.placeholder.mail")}>
                {t("editUser.labels.email")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"password"} name={"password"} data-testid="password"
                placeholder={t("users.placeholder.password")}>
                {t("editUser.labels.password")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextInput type={"password"} name={"confirmPassword"} data-testid="confirmPassword"
                placeholder={t("users.placeholder.confirmPassword")}>
                {t("editUser.labels.confirmPassword")}
            </Form.ValidatedTextInput>

            <>
                {props.passwordReset &&
                    <>
                        <Form.ValidatedCheckboxLabelRight name={"pwdReset"} data-testid="pwdReset">
                            {t("editUser.labels.mustChangePassword")}
                        </Form.ValidatedCheckboxLabelRight>
                        <div className={"invisible"}>
                            <Form.ValidatedCheckboxLabelRight name={"external"} data-testid="external">
                                {t("editUser.labels.external")}
                            </Form.ValidatedCheckboxLabelRight>
                        </div>
                    </>
                }
            </>

            {props.groupsReadonly ? <></> :
                <>
                    <H2>{t("users.labels.groups")} ({handler.values.memberOf.length})</H2>
                    {renderGroupsList()}
                </>
            }

            <div className={"my-4"}>
                <Button variant={"primary"} type={"submit"} disabled={!handler.dirty} data-testid="save-button">
                    {t("editUser.buttons.save")}
                </Button>
                {props.additionalButtons as JSX.Element}
            </div>
        </Form>

        {props.groupsReadonly ?
            <>
                <H2>{t("users.labels.myGroups")} ({handler.values.memberOf.length})</H2>
                {renderGroupsList(true, 10)}
            </>
            : <></>
        }
    </>;
}

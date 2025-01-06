import {deprecated_Form as Form, Details} from "@cloudogu/ces-theme-tailwind";
import {Button, H2, ListWithSearchbar} from "@cloudogu/deprecated-ces-theme-tailwind";
import {TrashIcon} from "@heroicons/react/24/outline";
import { useState} from "react";
import {twMerge} from "tailwind-merge";
import {t} from "../../helpers/i18nHelpers";
import {useConfirmation} from "../../hooks/useConfirmation";
import {Prompt} from "../../hooks/usePrompt";
import useUserFormHandler from "../../hooks/useUserFormHandler";
import {GroupsService} from "../../services/Groups";
import {ConfirmationDialog} from "../ConfirmationDialog";
import {useApplicationContext} from "../contexts/ApplicationContext";
import HelpLink from "../helpLink";
import type {Group} from "../../services/Groups";
import type {User} from "../../services/Users";
import type {NotifyFunction, UseFormHandlerFunctions} from "@cloudogu/deprecated-ces-theme-tailwind";
import type {ChangeEvent} from "react";

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
    const {handler, notification, notify} = useUserFormHandler<T>(props.initialUser, (values: T) => props.onSubmit(values, notify, handler));
    const {open, setOpen: toggleModal, targetName: groupName, setTargetName: setGroupName} = useConfirmation();
    const [formDisabled, setFormDisabled] = useState(true);

    const {admin} = useApplicationContext().casUser;

    const originalChangeFunction = handler.handleChange;

    handler.handleChange = (e:ChangeEvent) => {
        originalChangeFunction(e);
        hasEmptyRequiredFields();
    };

    const addGroup = (groupName: string): void => {
        if (handler.values.memberOf.indexOf(groupName) < 0) {
            const newGroups = [...handler.values.memberOf, groupName];
            handler.setValues({...handler.values, memberOf: newGroups});
            hasEmptyRequiredFields();
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
            hasEmptyRequiredFields();
        }
        toggleModal(false);
    };

    const queryGroups = async (searchValue: string): Promise<string[]> => {
        const groupsData = await GroupsService.query(undefined, {
            page: 1,
            page_size: MAX_SEARCH_RESULTS,
            query: searchValue,
            exclude: handler.values.memberOf ?? [],
        });
        return groupsData.data.map((x: Group) => x.name);
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
            addLable={t("users.labels.addGroup") + " (" + t("general.optional") + ")"}
            removeLable={t("users.labels.removeGroup")}
            emptyItemsLable={t("users.labels.emptyGroups")}
            removeIcon={<TrashIcon className={"w-6 h-6"}/>}
            pageSize={pageSize}
        />
    );

    const hasEmptyRequiredFields = (): void => {
        const form = document.forms.item(0);
        console.log("Check for null values");
        if (form) {
            const inputs: NodeListOf<HTMLInputElement> = form.querySelectorAll("input:required");
            for (const input of inputs) {
                if (!input.value) {
                    setFormDisabled(true);
                    return;
                }
            }
            setFormDisabled(false);
            return;
        }
        setFormDisabled(true);
        return;
    };

    return (
        <>
            <ConfirmationDialog
                open={open ?? false}
                data-testid="remove-group-dialog"
                onClose={() => toggleModal(false)}
                onConfirm={async () => {
                    await removeGroup(groupName ?? "");
                }}
                title={t("users.labels.removeGroup")}
                message={t("users.labels.removeGroupConfirmationMessage", {groupName: groupName})}
            />
            <Prompt when={handler.dirty && !handler.isSubmitting} message={t("generic.notification.form.prompt")}/>
            <Form handler={handler}>
                {notification}
                {(props.initialUser.external) && (
                    <span className={"font-bold"}>
                        {t("users.externalUserWarning")}
                    </span>
                )}
                <Form.ValidatedTextInput required type={"text"} name={"username"} disabled={props.disableUsernameField ?? true} data-testid="username" placeholder={t("users.placeholder.username")} hint={t("users.hint.username")} >
                    {t("editUser.labels.username")}
                </Form.ValidatedTextInput>
                <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"text"} name={"givenname"} data-testid="givenname" placeholder={t("users.placeholder.givenname")}  >
                    {t("editUser.labels.givenName")}
                </Form.ValidatedTextInput>
                <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"text"} name={"surname"} data-testid="surname" placeholder={t("users.placeholder.surname")} >
                    {t("editUser.labels.surname")}
                </Form.ValidatedTextInput>
                <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"text"} name={"displayName"} data-testid="displayName" placeholder={t("users.placeholder.displayName")} hint={t("users.hint.displayName")} >
                    {t("editUser.labels.displayName")}
                </Form.ValidatedTextInput>
                <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"text"} name={"mail"} data-testid="mail" placeholder={t("users.placeholder.mail")} >
                    {t("editUser.labels.email")}
                </Form.ValidatedTextInput>
                {!props.initialUser.external &&
                    <>
                        <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"password"} name={"password"} data-testid="password" placeholder={t("users.placeholder.password")} >
                            {t("editUser.labels.password")}
                        </Form.ValidatedTextInput>
                        <Form.ValidatedTextInput required disabled={props.initialUser.external} type={"password"} name={"confirmPassword"} data-testid="confirmPassword" placeholder={t("users.placeholder.confirmPassword")} >
                            {t("editUser.labels.confirmPassword")}
                        </Form.ValidatedTextInput>
                    </>
                }

                <>
                    {(props.passwordReset && !props.initialUser.external) && (
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
                    )}
                </>

                {props.groupsReadonly ? (
                    <></>
                ) : (
                    <>
                        <H2>
                            {t("users.labels.groups")} ({handler.values.memberOf.length})
                        </H2>
                        {renderGroupsList()}
                    </>
                )}

                <div className={"my-4"}>
                    <Button variant={"primary"} type={"submit"} disabled={formDisabled} data-testid="save-button">
                        {t("editUser.buttons.save")}
                    </Button>
                    {props.additionalButtons as JSX.Element}
                </div>
            </Form>

            {
                props.groupsReadonly ? (
                    <>
                        <H2>
                            {t("users.labels.myGroups")} ({handler.values.memberOf.length})
                        </H2>
                        {renderGroupsList(true, 10)}
                    </>
                ) : (
                    <></>
                )
            }
            {
                admin ? (
                    <>
                        <hr/>
                        <Details className={twMerge("py-2")}>
                            <Details.Summary>
                                <Details.Summary.Arrow/>
                                {t("users.steps.title")}
                            </Details.Summary>
                            <Details.Content>
                                {t("users.steps.text")}
                                <HelpLink/>
                            </Details.Content>
                        </Details>
                    </>
                ) : (
                    <></>
                )
            }
        </>
    );
}

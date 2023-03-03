import {Button, Form, H1, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {useNavigate} from "react-router-dom";
import {t} from "../../helpers/i18nHelpers";
import {useGroupValidationSchema} from "../../hooks/useGroupValidationSchema";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import { GroupsService} from "../../services/Groups";
import type {Group} from "../../services/Groups";

export function NewGroupForm(props: { title: string, edit?: boolean }) {
    useSetPageTitle(props.title);
    const validationSchema = useGroupValidationSchema();
    const handler = useFormHandler<Group>({
        enableReinitialize: true,
        initialValues: {name: "", description: "", members: []},
        validationSchema: validationSchema,
        onSubmit: (group: Group) => {
            GroupsService.save(group).then(() => {
                navigate("/groups", {
                    state: {
                        type: "primary",
                        message: t("newGroup.notification.success")
                    }
                });
            }).catch(() => {
                navigate("/groups", {
                    state: {
                        type: "danger",
                        message: t("newGroup.notification.error")
                    }
                });
            });
        }
    });
    const navigate = useNavigate();
    return <>
        <H1>{t("pages.groupsNew")}</H1>
        <Form handler={handler}>
            <Form.ValidatedTextInput type={"text"} name={"name"}>
                {t("editGroup.labels.name")}
            </Form.ValidatedTextInput>
            <Form.ValidatedTextArea name={"description"}>
                {t("editGroup.labels.description")}
            </Form.ValidatedTextArea>
            <div className={"my-4"}>
                <Button variant={"primary"} type={"submit"}
                    disabled={!handler.dirty}>{t("editUser.buttons.save")}</Button>
                <Button variant={"secondary"} type={"button"} className={"ml-4"} onClick={() => navigate(-1)}>
                    {t("newGroup.buttons.back")}
                </Button>
            </div>
        </Form>
    </>;
}
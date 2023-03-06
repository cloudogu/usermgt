import {Button, Form,  useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {cl} from "dynamic-class-list";
import {useNavigate} from "react-router-dom";
import * as Yup from "yup";
import {t} from "../../helpers/i18nHelpers";
import {useGroupValidationSchema} from "../../hooks/useGroupValidationSchema";
import {GroupsService} from "../../services/Groups";
import type {Group} from "../../services/Groups";

type GroupFormProps = {
    group: Group;
    onSubmit: (_:Group) => Promise<void>;
    notify: (_message:string, _type:"primary"|"danger", _group?: Group) => void;
}

export function GroupForm({group, onSubmit, notify}: GroupFormProps){
    const validationSchema = useGroupValidationSchema();
    const navigate = useNavigate();
    const isNewGroup = !group.name;
    const handler = useFormHandler<Group>({
        enableReinitialize: true,
        initialValues: group,
        validationSchema: isNewGroup ? validationSchema : Yup.object(),
        onSubmit: (group: Group) => {
            onSubmit(group).then(() => {
                notify(t("editGroup.notification.success", {groupName: group.name}), "primary", group);
            }).catch((e: Error) => {
                notify(e.message, "danger");
            });
        }
    });
    const onDelete = () => {
        GroupsService.delete(group.name).then(() => {
            navigate("/groups", {
                state: {
                    message: t("groups.notification.success", {groupName: group.name}),
                    variant: "primary"
                }
            });
        }).catch(() => {
            notify(t("groups.notification.error", {groupName: group.name}), "danger");
        });
    };
    return <Form handler={handler}>
        <Form.ValidatedTextInput type={"text"} name={"name"} disabled={!isNewGroup}>
            {t("editGroup.labels.name")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextArea name={"description"}>
            {t("editGroup.labels.description")}
        </Form.ValidatedTextArea>
        <div className={"my-4"}>
            <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>
                {t("editGroup.buttons.save")}
            </Button>
            <Button variant={"warning"} type={"button"} className={cl(
                "ml-4",
                isNewGroup ? "hidden" : ""
            )} onClick={onDelete}>
                {t("editGroup.buttons.remove")}
            </Button>
            <Button variant={"secondary"} type={"button"} className={"ml-4"} onClick={() => navigate("/groups")}>
                {t("editGroup.buttons.back")}
            </Button>
        </div>
    </Form>;
}

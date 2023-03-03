import {Button, Form, H1, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {useNavigate, useParams} from "react-router-dom";
import * as Yup from "yup";
import {t} from "../../helpers/i18nHelpers";
import {useGroup} from "../../hooks/useGroup";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {GroupsService} from "../../services/Groups";
import type {Group} from "../../services/Groups";

export function EditGroupForm(props: { title: string, edit?: boolean }) {
    useSetPageTitle(props.title);
    const {groupName} = useParams();
    const [group] = useGroup(groupName ?? "");
    const handler = useFormHandler<Group>({
        enableReinitialize: true,
        initialValues: group ?? {name: "", description: "", members: []},
        validationSchema: Yup.object(),
        onSubmit: (group: Group) => {
            GroupsService.update(group).then(() => {
                navigate("/groups", {
                    state: {
                        type: "primary",
                        message: t("editGroup.notification.success", {groupName: groupName})
                    }
                });
            }).catch(() => {
                navigate("/groups", {
                    state: {
                        type: "danger",
                        message: t("editGroup.notification.error", {groupName: groupName})
                    }
                });
            });
        }
    });
    const navigate = useNavigate();
    return <>
        <H1>{group?.name}</H1>
        <Form handler={handler}>
            <Form.ValidatedTextArea name={"description"}>
                {t("editGroup.labels.description")}
            </Form.ValidatedTextArea>
            <div className={"my-4"}>
                <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>
                    {t("editGroup.buttons.save")}
                </Button>
                <Button variant={"warning"} type={"button"} className={"ml-4"} onClick={() => {
                    GroupsService.delete(group?.name ?? "").then(() => {
                        navigate("/groups", {
                            state: {
                                type: "primary",
                                message: t("groups.notification.success", {groupName: groupName})
                            }
                        });
                    }).catch(() => {
                        navigate("/groups", {
                            state: {
                                type: "danger",
                                message: t("groups.notification.error", {groupName: groupName})
                            }
                        });
                    });
                }}>
                    {t("editGroup.buttons.remove")}
                </Button>
                <Button variant={"secondary"} type={"button"} className={"ml-4"} onClick={() => navigate(-1)}>
                    {t("editGroup.buttons.back")}
                </Button>
            </div>
        </Form>
    </>;
}
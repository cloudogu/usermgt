import {Button, H1, LoadingIcon} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate, useParams} from "react-router-dom";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useBackURL} from "../hooks/useBackURL";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {useUser} from "../hooks/useUser";
import { UsersService} from "../services/Users";
import type {User} from "../services/Users";

export default function EditUser(props: { title: string }) {
    useSetPageTitle(props.title);
    const {username} = useParams();
    const {user, isLoading} = useUser(username);
    const navigate = useNavigate();
    const {backURL} = useBackURL();

    return <>
        <H1 className="uppercase">{t("pages.usersEdit")}</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm<User>
                initialUser={user}
                editGroups={true}
                passwordReset={true}
                onSubmit={(user, notify) =>
                    UsersService.update(user)
                        .then((msg: string) => {
                            navigate(backURL ?? "/users", {
                                state: {
                                    alert: {
                                        message: msg,
                                        variant: "primary"
                                    }
                                }
                            });
                        }).catch((error: Error) => {
                            notify(error.message, "danger");
                        })}
                additionalButtons={
                    <Button variant={"secondary"} type={"button"} className={"ml-4"}
                        onClick={() => navigate(backURL ?? "/users")}>
                        {t("editGroup.buttons.back")}
                    </Button>
                }
            />
        }
    </>;
}

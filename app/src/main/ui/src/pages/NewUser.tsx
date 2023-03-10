import {Form, H1} from "@cloudogu/ces-theme-tailwind";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import UserForm from "../components/users/UserForm";
import {User, UsersService} from "../services/Users";
import {t} from "../helpers/i18nHelpers";
import {AccountService, emptyUser} from "../services/Account";
import {useState} from "react";

export default function NewUser(props: { title: string }) {
    useSetPageTitle(props.title);
    const [user, setUser] = useState(emptyUser);


    return <>
        <H1 className="uppercase">{t("pages.usersNew")}</H1>
        <UserForm<User>
            initialUser={user}
            onSubmit={(user, notify, handler) => {
                return UsersService.save(user)
                    .then((msg: string) => {
                        notify(msg, "primary");
                        setUser(user);
                        handler.resetForm(user);
                    }).catch((error: Error) => {
                        notify(error.message, "danger");
                    });
            }}
        >
            <Form.ValidatedCheckboxLabelRight name={"pwdReset"}>
                {t("editUser.labels.mustChangePassword")}
            </Form.ValidatedCheckboxLabelRight>
        </UserForm>
    </>;
}


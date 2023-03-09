import {H1, LoadingIcon} from "@cloudogu/ces-theme-tailwind";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import UserForm from "../components/users/UserForm";
import {useUser} from "../hooks/useUser";
import {User} from "../services/Users";
import {useParams} from "react-router-dom";
import {t} from "../helpers/i18nHelpers";

export default function EditUser(props: { title: string }) {
    useSetPageTitle(props.title);
    const {username} = useParams();
    const {setUser, user, isLoading} = useUser(username);

    return <>
        <H1 className="uppercase">{t("pages.usersEdit")}</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm<User> initialUser={user} onUserChange={setUser} saveUser={async () => {
                return "success"
            }}></UserForm>
        }
    </>;
}


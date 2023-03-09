import {H1, LoadingIcon} from "@cloudogu/ces-theme-tailwind";
import {useAccount} from "../hooks/useAccount";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import UserForm from "../components/users/UserForm";
import {AccountService} from "../services/Account";

export default function Account(props: { title: string }) {
    useSetPageTitle(props.title);
    const {account, isLoading, setAccount} = useAccount();

    return <>
        <H1 className="uppercase">Account</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm initialUser={account} onUserChange={setAccount} saveUser={AccountService.update}></UserForm>
        }
    </>;
}


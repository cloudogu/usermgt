import {H1, LoadingIcon} from "@cloudogu/ces-theme-tailwind";
import UserForm from "../components/users/UserForm";
import {useAccount} from "../hooks/useAccount";
import {AccountService} from "../services/Account";
import type {User} from "../services/Users";

export default function Account() {
    const {account, isLoading, setAccount} = useAccount();

    return <>
        <H1 className="uppercase">Account</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm<User>
                initialUser={account}
                groupsReadonly={true}
                onSubmit={(user, notify, handler) => AccountService.update(user)
                    .then((msg: string) => {
                        notify(msg, "primary");
                        setAccount(user);
                        handler.resetForm({values: user});
                    }).catch((error: Error) => {
                        notify(error.message, "danger");
                    })}
            />
        }
    </>;
}


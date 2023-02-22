import {useValidationSchema} from "../../hooks/useValidationSchema";
import {useAccount} from "../../hooks/useAccount";
import AccountForm from "./AccountForm";
import {useEffect} from "react";
import {H1, LoadingIcon} from "@cloudogu/ces-theme-tailwind";

export default function Account(props: { title: string }) {
    const validationSchema = useValidationSchema()
    const {account, isLoading, setAccount} = useAccount();

    useEffect(() => {
        (document.title = props.title)
    }, []);

    return <>
        <H1 className="uppercase">Account</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <AccountForm account={account} setAccount={setAccount} validationSchema={validationSchema}></AccountForm>
        }
    </>;
}

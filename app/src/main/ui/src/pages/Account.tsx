import {useValidationSchema} from "../hooks/useValidationSchema";
import {useAccount} from "../hooks/useAccount";
import AccountForm from "./account/accountForm";
import {useEffect} from "react";



export default function Account(props: any) {
    const validationSchema = useValidationSchema();
    const {account, isLoading} = useAccount();

    useEffect(() => {(document.title = props.title)}, [])

    if (isLoading) {
        return (
            <div>Loading</div>
        )
    }
    return (
        <AccountForm account={account} validationSchema={validationSchema} resetOnSave={true}></AccountForm>
    )
}

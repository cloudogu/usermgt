import {useValidationSchema} from "../../hooks/useValidationSchema";
import {useAccount} from "../../hooks/useAccount";
import AccountForm from "./AccountForm";
import {useEffect} from "react";

export default function Account(props: any) {
    const validationSchema = useValidationSchema();
    const {account, isLoading, setAccount} = useAccount();

    useEffect(() => {(document.title = props.title)}, []);

    if (isLoading) {
        return (
            <div>Loading</div>
        )
    }
    return (
        <AccountForm account={account} validationSchema={validationSchema} setAccount={setAccount}></AccountForm>
    )
}

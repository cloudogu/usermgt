import {useValidationSchema} from "../hooks/useValidationSchema";
import {useAccount} from "../hooks/useAccount";
import AccountForm from "./account/accountForm";

export default function Account() {
    const validationSchema = useValidationSchema();
    const {account, isLoading} = useAccount();

    if (isLoading) {
        return (
            <div>Loading</div>
        )
    }
    return (
        <AccountForm account={account} validationSchema={validationSchema} resetOnSave={true}></AccountForm>
    )
}

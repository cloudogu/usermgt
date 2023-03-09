import {useAlertNotification, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {AccountModel, ApiAccount} from "../services/Account";
import {useValidationSchema} from "./useValidationSchema";

export interface useUserFormHandlerResponse {
    handler: ReturnType<typeof useFormHandler<AccountModel>>;
    notification: JSX.Element;
}

export default function useUserFormHandler(
    initialUser: AccountModel,
    callback: (account: AccountModel) => Promise<string>,
    setAccount: (_account: ApiAccount) => void): useUserFormHandlerResponse {

    const validationSchema = useValidationSchema();
    const {notification, notify} = useAlertNotification();

    const handler = useFormHandler<AccountModel>(
        {
            initialValues: {
                ...initialUser,
                confirmPassword: initialUser.password,
                hiddenPasswordField: initialUser.password,
            },
            validationSchema: validationSchema,
            enableReinitialize: true,
            onSubmit: (values: any) => {
                callback(values).then((msg: string) => {
                    notify(msg, "primary");
                    setAccount(values);
                    handler.resetForm(values);
                }).catch((error: Error) => {
                    notify(error.message, "danger");
                });
            },
        }
    );

    return {handler: handler, notification: notification};
}
import {useAlertNotification, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {useValidationSchema} from "./useValidationSchema";
import {User} from "../services/Users";

export interface useUserFormHandlerResponse<T extends User> {
    handler: ReturnType<typeof useFormHandler<T>>;
    notification: JSX.Element;
}

export default function useUserFormHandler<T extends User>(
    initialUser: T,
    callback: (_user: T) => Promise<string>,
    setAccount: (_user: T) => void): useUserFormHandlerResponse<T> {

    const validationSchema = useValidationSchema();
    const {notification, notify} = useAlertNotification();

    const handler = useFormHandler<T>(
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
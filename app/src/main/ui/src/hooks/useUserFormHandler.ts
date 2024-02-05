import {useAlertNotification, useFormHandler} from "@cloudogu/deprecated-ces-theme-tailwind";
import {useValidationSchema} from "./useValidationSchema";
import type {User} from "../services/Users";
import type {NotifyFunction, UseFormHandlerFunctions} from "@cloudogu/deprecated-ces-theme-tailwind";

export interface useUserFormHandlerResponse<T extends User> {
    handler: UseFormHandlerFunctions<T>;
    notification: JSX.Element;
    notify: NotifyFunction;
}

export default function useUserFormHandler<T extends User>(
    initialUser: T,
    onSubmit: any): useUserFormHandlerResponse<T> {

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
            onSubmit: onSubmit,
        }
    );

    return {handler: handler, notification: notification, notify: notify};
}
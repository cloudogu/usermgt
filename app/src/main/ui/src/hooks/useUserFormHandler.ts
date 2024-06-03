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

    // Workaround with cast to any. Our interface does not accept validateOnbChange/Blur-values but the underlying lib does.
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
            validateOnChange: false,
            validateOnBlur: false
        } as any
    );

    // As we now only validate on submit but the inputs of the old theme change state to "success" if touched and no error exists, we now have to make touched dependent of submit count
    const mockTouched = new Proxy<Map<string, boolean>>(new Map(), {
        get: () =>  handler.submitCount > 0,
    });

    return {handler: {...handler, touched: mockTouched as any}, notification: notification, notify: notify};
}

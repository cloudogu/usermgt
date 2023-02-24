import React, {useState} from "react";
import {Alert} from "@cloudogu/ces-theme-tailwind";

export const useChangeNotification = (): [JSX.Element, (m: string) => void, (m: string) => void] => {
    const [notification, setNotification] = useState<JSX.Element>(<></>);
    const notify = (message: string, variant: "primary" | "danger") => setNotification(<Alert
        variant={variant}
        onClose={() => {
            setNotification(<></>);
        }}>
        {message}
    </Alert>);
    const success = (message: string) => notify(message, "primary");
    const error = (message: string) => notify(message, "danger");

    return [notification, success, error];
}
import {Alert} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";

export const useChangeNotification = () => {
    const [notification, setNotification] = useState<JSX.Element>(<></>);
    const notify = (message: string, variant: "primary" | "danger") => setNotification(<Alert
        variant={variant}
        onClose={() => {
            setNotification(<></>);
        }}>
        {message}
    </Alert>);
    const clearNotification = () => setNotification(<></>);

    return {notification, clearNotification, notify};
};

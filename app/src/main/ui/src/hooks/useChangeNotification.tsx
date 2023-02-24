import React, {useState} from "react";
import {Alert} from "@cloudogu/ces-theme-tailwind";

export const useChangeNotification = (): [JSX.Element, (m: string) => void, (m: string) => void] => {
    const [notification, setNotification] = useState<JSX.Element>(<></>);
    const success = (message: string) => setNotification(<Alert
        variant={"primary"}
        onClose={() => {
            setNotification(<></>);
        }}>
        {message}
    </Alert>);
    const error = (message: string) => setNotification(<Alert
        variant={"danger"}
        onClose={() => {
            setNotification(<></>);
        }}>
        {message}
    </Alert>);

    return [notification, success, error];
}
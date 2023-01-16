import {useUser} from "../hooks/useUser";
import {UserIcon} from "@heroicons/react/24/outline";
import React from "react";


export function User() {
    const {data: user, isLoading, isError} = useUser();

    let content;
    if (isLoading) {
        content = (
            <div className={"animate-spin border rounded-full border-l-transparent w-4 h-4"}/>
        )
    } else if ((!isLoading && !user) || isError) {
        content = <div>
            <div aria-hidden={true}>
                <span id={"user-field-username"}>Fehler</span>
            </div>

        </div>
    } else {
        content =
            <div aria-hidden={true}>
                <UserIcon/>
                <span id={"user-field-username"}>{user?.name}</span>
            </div>

    }

    return (<>{content}</>)
}
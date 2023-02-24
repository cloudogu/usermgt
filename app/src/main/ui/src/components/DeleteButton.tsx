import React, {ComponentPropsWithoutRef} from "react";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";

type ButtonProps = ComponentPropsWithoutRef<"button"> & {}

export function DeleteButton(props: ButtonProps) {
    return <button {...props}
                   className={"enabled:text-text-primary enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed"}
    >
        <TrashIcon className={"w-6 h-6"}/>
    </button>
}

export function EditButton(props: ButtonProps) {
    return <button {...props}
                   className={"enabled:text-text-primary enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed"}
    >
        <PencilIcon className={"w-6 h-6"}/>
    </button>
}
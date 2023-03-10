import React, {ComponentPropsWithoutRef} from "react";
import {Link} from "react-router-dom";
import {PencilIcon} from "@heroicons/react/24/outline";

export default function EditLink(props: ComponentPropsWithoutRef<typeof Link>){
    return (
        <Link {...props} className={"text-text-primary hover:text-text-primary-hover disabled:text-text-primary-disabled disabled:cursor-not-allowed"}>
            <PencilIcon className={"w-6 h-6"}/>
        </Link>
    );
}
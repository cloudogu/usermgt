import {useState} from "react";
import {StateSetter} from "./useAPI";

export const useConfirmation = (): [boolean|undefined, StateSetter<boolean>, string|undefined, StateSetter<string>] => {
    const [open, setOpen] = useState<boolean|undefined>(false);
    const [targetName, setTargetName] = useState<string|undefined>("");

    return [open, setOpen, targetName, setTargetName]
}
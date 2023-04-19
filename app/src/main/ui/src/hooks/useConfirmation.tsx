import {useState} from "react";

export const useConfirmation = () => {
    const [open, setOpen] = useState<boolean|undefined>(false);
    const [targetName, setTargetName] = useState<string|undefined>("");

    return {open, setOpen, targetName, setTargetName};
};
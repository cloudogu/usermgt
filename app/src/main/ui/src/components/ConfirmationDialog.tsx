import {Modal, H3, Button} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useRef, useState} from "react";
import {twMerge} from "tailwind-merge";
import {t} from "../helpers/i18nHelpers";

export type ConfirmationDialogProps = {
    open: boolean;
    title: string;
    message: string;
    onClose: () => void
    onConfirm: () => void
    className: string;
}

export function ConfirmationDialog({
                                       open, title, message,
                                       onClose, onConfirm, ...props
                                   }: ConfirmationDialogProps) {
    const [disable, setDisable] = useState(false)
    const buttonRef = useRef<HTMLButtonElement>(null);
    const onClick = async () => {
        setDisable(true);
        await onConfirm();
        setDisable(false);
    }

    useEffect(() => {
        if(open) {
            buttonRef.current?.focus();
        }
    }, [open])
    return <>
        <Modal {...props} open={open}>
            <Modal.Header>
                <H3 className="uppercase">{title}</H3>
            </Modal.Header>
            <Modal.Body>
                <p>{message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={"danger"} onClick={onClick} disabled={disable}
                        className={"uppercase"}>{t("modal.confirm")}</Button>
                <Button variant={"secondary"} onClick={onClose} disabled={disable}
                        className={"ml-5"}>{t("modal.cancel")}</Button>
            </Modal.Footer>
        </Modal>
        <div className={twMerge("w-full h-full z-50 top-0 left-0 fixed",
            "bg-alert-secondary-font bg-opacity-40 backdrop-filter backdrop-blur-3xl backdrop-opacity-70 backdrop:bg-alert-secondary-font",
            (open ?? false) ? "" : "hidden")}/>
    </>
}
import {Modal, H3, Button} from "@cloudogu/ces-theme-tailwind";
import React from "react";
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
    return <>
        <Modal {...props} open={open} onClose={onClose}>
            <Modal.Header>
                <H3 className="uppercase">{title}</H3>
            </Modal.Header>
            <Modal.Body>
                <p>{message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={"danger"} className={"uppercase"} onClick={onConfirm}>
                    {t("modal.confirm")}
                </Button>
                <Button variant={"secondary"} onClick={onClose} className="ml-5">{t("modal.cancel")}</Button>
            </Modal.Footer>
        </Modal>
        <div className={twMerge("w-full h-full z-50 top-0 left-0 fixed",
            "bg-alert-secondary-font bg-opacity-40 backdrop-filter backdrop-blur-3xl backdrop-opacity-70 backdrop:bg-alert-secondary-font",
            (open ?? false) ? "" : "hidden")}/>
    </>
}
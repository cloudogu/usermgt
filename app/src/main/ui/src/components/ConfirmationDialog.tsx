import {Modal, H3, Button} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useRef, useState} from "react";
import {t} from "../helpers/i18nHelpers";

export type ConfirmationDialogProps = {
    open: boolean;
    title: string;
    message: string;
    onClose: () => void
    onConfirm: () => void
    className?: string;
    "data-testid"?: string;
}

export function ConfirmationDialog({
    open, title, message,
    onClose, onConfirm, ...props
}: ConfirmationDialogProps) {
    const [disable, setDisable] = useState(false);
    const buttonRef = useRef<HTMLButtonElement>(null);
    const onClick = async () => {
        setDisable(true);
        await onConfirm();
        setDisable(false);
    };

    useEffect(() => {
        if(open) {
            buttonRef.current?.focus();
        }
    }, [open]);

    return <>
        <Modal {...props} open={open}>
            <Modal.Header>
                <H3 className="uppercase">{title}</H3>
            </Modal.Header>
            <Modal.Body>
                <p className="break-words">{message}</p>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={"danger"} onClick={onClick} disabled={disable}
                    className={"uppercase"}>{t("modal.confirm")}</Button>
                <Button variant={"secondary"} onClick={onClose} disabled={disable} ref={buttonRef}
                    className={"ml-5"}>{t("modal.cancel")}</Button>
            </Modal.Footer>
        </Modal>
    </>;
}
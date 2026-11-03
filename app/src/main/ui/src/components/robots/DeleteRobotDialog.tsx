import {Button, SegmentedDialog} from "@cloudogu/ces-theme-tailwind";
import {t} from "../../helpers/i18nHelpers";
import type {PersonalAccessToken} from "../../hooks/usePAT";
import type {RefObject} from "react";

export type DeleteRobotDialogProps = {
    pat: Pick<PersonalAccessToken, "displayName">;
    onClose: () => void;
    onConfirm: () => Promise<void>;
    returnFocusRef?: RefObject<HTMLButtonElement>;
};

export default function DeleteRobotDialog({pat, onClose, onConfirm, returnFocusRef}: DeleteRobotDialogProps) {
    return (
        <SegmentedDialog open={true} onOpenChange={(open) => !open && onClose()} variant="danger">
            <SegmentedDialog.Content
                className="border-danger"
                showDefaultCloseIcon
                onCloseAutoFocus={event => {
                    if (returnFocusRef?.current?.isConnected) {
                        event.preventDefault();
                        returnFocusRef.current.focus();
                    }
                }}
            >
                <SegmentedDialog.Content.Header className="border-danger text-danger">
                    <SegmentedDialog.Content.Header.Title>
                        {t("robots.createrobot.modal.delete.headline")}
                    </SegmentedDialog.Content.Header.Title>
                </SegmentedDialog.Content.Header>
                <SegmentedDialog.Content.Body className="text-default-text flex flex-col gap-4">
                    <p>{t("robots.createrobot.modal.delete.hint.top")}</p>
                    <p className="rounded px-5 py-1 bg-neutral-weak text-lg  text-center">
                        {pat.displayName}
                    </p>
                    <p>{t("robots.createrobot.modal.delete.hint.bottom")}</p>
                </SegmentedDialog.Content.Body>
                <SegmentedDialog.Content.Footer className="bg-danger-weaker">
                    <SegmentedDialog.Close asChild>
                        <Button color="danger" variant="primary" size="regular" onClick={onConfirm}>
                            {t("robots.createrobot.modal.delete.confirm")}
                        </Button>
                    </SegmentedDialog.Close>
                    <SegmentedDialog.Close asChild>
                        <Button color="danger" variant="secondary" size="regular" onClick={onClose}>
                            {t("robots.createrobot.modal.delete.cancel")}
                        </Button>
                    </SegmentedDialog.Close>
                </SegmentedDialog.Content.Footer>
            </SegmentedDialog.Content>
        </SegmentedDialog>
    );
}

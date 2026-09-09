import {Button, SegmentedDialog} from "@cloudogu/ces-theme-tailwind";
import {t} from "../../helpers/i18nHelpers";
import type {PersonalAccessToken} from "../../hooks/usePAT";

export type DeletePATDialogProps = {
    pat: PersonalAccessToken;
    onClose: () => void;
    onConfirm: () => Promise<void>;
};

export default function DeletePATDialog({pat, onClose, onConfirm}: DeletePATDialogProps) {
    return (
        <SegmentedDialog open={true} onOpenChange={(open) => !open && onClose()} variant="danger">
            <SegmentedDialog.Content className="border-danger" showDefaultCloseIcon>
                <SegmentedDialog.Content.Header className="border-danger text-danger">
                    <SegmentedDialog.Content.Header.Title>
                        {t("security.createpat.modal.delete.headline")}
                    </SegmentedDialog.Content.Header.Title>
                </SegmentedDialog.Content.Header>
                <SegmentedDialog.Content.Body className="text-default-text">
                    <p>{t("security.createpat.modal.delete.hint.top")}</p>
                    <div className="rounded bg-neutral-weak">
                        <h2 className="desktop:text-desktop-xl mobile:text-mobile-xl text-center">{pat.displayName}</h2>
                    </div>
                    <p>{t("security.createpat.modal.delete.hint.bottom")}</p>
                </SegmentedDialog.Content.Body>
                <SegmentedDialog.Content.Footer className="bg-danger-weaker">
                    <SegmentedDialog.Close asChild>
                        <Button color="danger" variant="primary" size="regular" onClick={onConfirm}>
                            {t("security.createpat.modal.delete.confirm")}
                        </Button>
                    </SegmentedDialog.Close>
                    <SegmentedDialog.Close asChild>
                        <Button color="danger" variant="secondary" size="regular" onClick={onClose}>
                            {t("security.createpat.modal.delete.cancel")}
                        </Button>
                    </SegmentedDialog.Close>
                </SegmentedDialog.Content.Footer>
            </SegmentedDialog.Content>
        </SegmentedDialog>
    );
}

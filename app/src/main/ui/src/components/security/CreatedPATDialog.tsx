import {
    Button,
    CesIconCheck,
    CesIconCopy,
    CesIconInfo,
    Input,
    Label,
    SegmentedDialog,
} from "@cloudogu/ces-theme-tailwind";
import {useState} from "react";
import {t, tWithParams} from "../../helpers/i18nHelpers";
import type {CreatePATResponse} from "../../services/PATs";
import MessageBox from "../MessageBox";

export type CreatedPATDialogProps = {
    pat: CreatePATResponse;
    onClose: () => void;
};

function formatExpiration(expiresAt: string | null): string {
    if (!expiresAt) {
        return t("security.createpat.select.expires.option.never");
    }

    const expirationDate = new Date(expiresAt);
    if (Number.isNaN(expirationDate.getTime())) {
        return expiresAt;
    }

    const remainingDays = Math.max(0, Math.ceil((expirationDate.getTime() - Date.now()) / (24 * 60 * 60 * 1000)));
    return tWithParams("security.createpat.modal.success.expires.text", remainingDays, expirationDate.toLocaleDateString("de-DE"));
}

export default function CreatedPATDialog({pat, onClose}: CreatedPATDialogProps) {
    const [copied, setCopied] = useState(false);

    const copyToken = async () => {
        await navigator.clipboard.writeText(pat.token);
        setCopied(true);
    };

    return (
        <SegmentedDialog open={true} onOpenChange={(open) => !open && onClose()} variant="standard">
            <SegmentedDialog.Content className="border-success" showDefaultCloseIcon>
                <SegmentedDialog.Content.Header className="border-success text-success">
                    <SegmentedDialog.Content.Header.Title>
                        {t("security.createpat.modal.success.headline")}
                    </SegmentedDialog.Content.Header.Title>
                </SegmentedDialog.Content.Header>
                <SegmentedDialog.Content.Body className="text-success">
                    <MessageBox text={t("security.createpat.modal.success.hint")} icon="info" color="success"/>
                    <div className="self-stretch grid grid-cols-[max-content_1fr] items-baseline gap-x-4 gap-y-2 text-default-text text-base leading-6">
                        <div className="font-bold">{t("security.createpat.modal.success.displayname.label")}</div>
                        <div className="font-normal">{pat.displayName}</div>
                        <div className="font-bold">{t("security.createpat.modal.success.expires.label")}</div>
                        <div className="font-normal">{formatExpiration(pat.expiresAt)}</div>
                    </div>
                    <div className="border-b-2 border-neutral-weak my-4" />
                    <Label text="Zugriffsschlüssel" variant="neutral" className="w-full text-default-text font-bold">
                        <div className="flex w-full">
                            <Input
                                type="text"
                                id="created-pat-token"
                                variant="neutral"
                                value={pat.token}
                                readOnly
                                className="min-w-0 flex-1 rounded-r-none font-normal"
                            />
                            <Button
                                type="button"
                                color="neutral"
                                variant="primary"
                                size="regular"
                                className="rounded-l-none whitespace-nowrap"
                                onClick={copyToken}
                                aria-describedby="pat-created-success-hint"
                                aria-label={t("security.createpat.modal.success.pat.arialabel")}
                            >
                                {copied ? <CesIconCheck/> : <CesIconCopy/>}
                            </Button>
                        </div>
                    </Label>
                </SegmentedDialog.Content.Body>
                <SegmentedDialog.Content.Footer className="bg-success-weaker">
                    <SegmentedDialog.Close asChild>
                        <Button color="success" variant="primary" size="regular" data-testid="security-created-pat-close">
                            {t("security.createpat.modal.success.pat.button")}
                        </Button>
                    </SegmentedDialog.Close>
                </SegmentedDialog.Content.Footer>
            </SegmentedDialog.Content>
        </SegmentedDialog>
    );
}

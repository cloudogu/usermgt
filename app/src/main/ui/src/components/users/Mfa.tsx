import {CesIconShieldCheck, CesIconShieldWarning} from "@cloudogu/ces-theme-tailwind";
import {Button} from "@cloudogu/deprecated-ces-theme-tailwind";
import React, {useState} from "react";
import {t} from "../../helpers/i18nHelpers";
import {ConfirmationDialog} from "../ConfirmationDialog";
import type {Mfa} from "../../services/mfa";

export type MfaManagementProps = {
    username?: string;
    mfa?: Mfa;
    // eslint-disable-next-line autofix/no-unused-vars
    onDelete: (username: string | undefined) => Promise<void>;
    className?: string;
};

// MfaManagement displays a management interface for the multifactor authentication (MFA) of a user.
export function MfaManagement({username, mfa, onDelete, className}: MfaManagementProps) {
    const [dialogOpen, setDialogOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    const isMfaEnabled = !!mfa && !!mfa.name;

    const handleDeleteClick = () => {
        if (isMfaEnabled) {
            setDialogOpen(true);
        }
    };

    const handleConfirmDelete = async () => {
        setIsDeleting(true);
        try {
            await onDelete(username);
            setDialogOpen(false);
        } finally {
            setIsDeleting(false);
        }
    };

    return (
        <div className={className}>
            <div className="mb-4">
                {isMfaEnabled ? (
                    <div>
                        <div className="flex items-center gap-2 mb-2">
                            <CesIconShieldCheck className="w-6 h-6 text-success" />
                            <p>
                                {t("users.mfa.status.enabled", { username })}
                            </p>
                        </div>
                        {mfa.name && (
                            <p className="text-sm text-gray-600 ml-8">
                                {t("users.mfa.device.name")}: {mfa.name}
                            </p>
                        )}
                    </div>
                ) : (
                    <div className="flex items-center gap-2">
                        <CesIconShieldWarning className="w-6 h-6 text-gray-600" />
                        <p className="text-gray-600">
                            {t("users.mfa.status.disabled", { username })}
                        </p>
                    </div>
                )}
            </div>

            <Button
                variant="secondary"
                onClick={handleDeleteClick}
                disabled={!isMfaEnabled || isDeleting}
                className="uppercase"
            >
                {t("users.mfa.buttons.delete")}
            </Button>

            <ConfirmationDialog
                open={dialogOpen}
                title={t("users.mfa.delete.confirmation.title")}
                message={t("users.mfa.delete.confirmation.message", {username})}
                onClose={() => setDialogOpen(false)}
                onConfirm={handleConfirmDelete}
                data-testid="mfa-delete-dialog"
            />
        </div>
    );
}

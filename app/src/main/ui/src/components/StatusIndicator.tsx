import React from 'react';
import {t} from '../helpers/i18nHelpers';
import Badge from './Badge';

export type StatusIndicatorProps = {
    text: string;
    variant?: string;
    className?: string
};

export function StatusIndicator({text, variant = 'primary', className = ''}: StatusIndicatorProps) {
    const isActive = text === 'active';
    const displayText = t(isActive ? 'security.overview.table.statustype.active' : 'security.overview.table.statustype.expired');
    const  color = isActive ? 'brand' : 'neutral';


    return <Badge text={displayText} variant={variant} color={color} className={className} ariaText={`${t("security.overview.table.status")}: ${displayText}`}/>;
}

export default StatusIndicator;

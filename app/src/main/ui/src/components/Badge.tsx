import React from 'react';

export type StatusIndicatorProps = {
    text: string;
    variant?: string;
    color?: string;
    className?: string
};

export function Badge({text, variant = 'primary', color = 'brand', className = ''}: StatusIndicatorProps) {
    const isActive = color === 'brand';
    if ('secondary' === variant) {
        return (
            <span className={`rounded-full px-2 py-1 h-5 desktop:text-desktop-small mobile:text-mobile-small ${isActive ? 'bg-brand-weaker' : 'bg-neutral-weak'} ${className}`}>
                {text}
            </span>
        );
    }
    return (
        <span className={`px-2 py-1 desktop:text-desktop mobile:text-mobile ${isActive ? 'bg-brand-weaker' : 'bg-neutral-weak'} ${className}`}>
            {text}
        </span>
    );
}

export default Badge;

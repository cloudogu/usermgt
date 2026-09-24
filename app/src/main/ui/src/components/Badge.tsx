import React from 'react';

export type StatusIndicatorProps = {
    text: string;
    ariaText?: string
    variant?: string;
    color?: string;
    className?: string
};

export function Badge({text, ariaText, variant = 'primary', color = 'brand', className = ''}: StatusIndicatorProps) {
    const isActive = color === 'brand';
    if ('secondary' === variant) {
        return (
            <span className={`rounded-full px-2 py-1 h-5 desktop:text-desktop-small mobile:text-mobile-small ${isActive ? 'bg-brand-weaker' : 'bg-neutral-weak'} ${className}`}>
                {ariaText ? <span className="sr-only"> {ariaText}</span> : ""}
                {text}
            </span>
        );
    }
    return (
        <span className={`rounded-sm px-5 py-1 desktop:text-desktop-xl mobile:text-xl ${isActive ? 'bg-brand-weaker' : 'bg-neutral-weak'} ${className}`}>
            {ariaText ? <span className="sr-only"> {ariaText}</span> : ""}
            {text}
        </span>
    );
}

export default Badge;

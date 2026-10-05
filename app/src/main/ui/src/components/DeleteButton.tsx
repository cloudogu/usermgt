import {CesIconTrash} from '@cloudogu/ces-theme-tailwind';
import React, {forwardRef} from 'react';
import type {ComponentPropsWithoutRef} from 'react';

type ButtonProps = ComponentPropsWithoutRef<'button'>

export const DeleteButton = forwardRef<HTMLButtonElement, ButtonProps>((props, ref) =>
    <IconButton {...props} ref={ref}>
        <CesIconTrash className={'w-6 h-6'}/>
    </IconButton>
);
DeleteButton.displayName = 'DeleteButton';

const IconButton = forwardRef<HTMLButtonElement, ButtonProps>(({children, ...props}, ref) =>
    <button {...props} ref={ref}
        className={`hover:!text-brand focus-visible:!text-brand-strong active:!text-brand-strong disabled:cursor-not-allowed text-neutral focus-visible:ces-focused rounded-sm outline-none ${props.className}`}
    >
        {children}
    </button>
);
IconButton.displayName = 'IconButton';

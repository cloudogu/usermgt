import React, {forwardRef} from 'react';
import type {ComponentPropsWithoutRef} from 'react';
import {CesIconTrash} from "@cloudogu/ces-theme-tailwind";

type ButtonProps = ComponentPropsWithoutRef<'button'>

export const DeleteButton = forwardRef<HTMLButtonElement, ButtonProps>((props, ref) =>
    <IconButton {...props} ref={ref}>
        <CesIconTrash className={'w-6 h-6'}/>
    </IconButton>
);
DeleteButton.displayName = 'DeleteButton';

const IconButton = forwardRef<HTMLButtonElement, ButtonProps>(({children, ...props}, ref) =>
    <button {...props} ref={ref}
        className={`enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed focus-visible:ces-focused ${props.className}`}
    >
        {children}
    </button>
);
IconButton.displayName = 'IconButton';

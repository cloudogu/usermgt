import {TrashIcon} from '@heroicons/react/24/outline';
import React, {forwardRef} from 'react';
import type {ComponentPropsWithoutRef} from 'react';

type ButtonProps = ComponentPropsWithoutRef<'button'>

export const DeleteButton = forwardRef<HTMLButtonElement, ButtonProps>((props, ref) =>
    <IconButton {...props} ref={ref}>
        <TrashIcon className={'w-6 h-6'}/>
    </IconButton>
);
DeleteButton.displayName = 'DeleteButton';

const IconButton = forwardRef<HTMLButtonElement, ButtonProps>(({children, ...props}, ref) =>
    <button {...props} ref={ref}
        className={'enabled:text-text-primary enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed'}
    >
        {children}
    </button>
);
IconButton.displayName = 'IconButton';
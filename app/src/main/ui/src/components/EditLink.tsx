import {PencilIcon} from '@heroicons/react/24/outline';
import React from 'react';
import {Link} from 'react-router-dom';
import {twMerge} from 'tailwind-merge';
import type {ComponentPropsWithoutRef} from 'react';

export default function EditLink(props: ComponentPropsWithoutRef<typeof Link>) {
    return (
        <Link {...props}
            className={
                twMerge(
                    'text-text-primary hover:text-text-primary-hover disabled:text-text-primary-disabled disabled:cursor-not-allowed',
                    props.className
                )
            }>
            <PencilIcon className={'w-6 h-6'}/>
        </Link>
    );
}
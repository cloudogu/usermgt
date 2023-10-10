import React from 'react';
import {useSetPageTitle} from '../hooks/useSetPageTitle';
import type {ReactElement} from 'react';

export default function TitledResource(props: {pageName: string, children: ReactElement}){
    useSetPageTitle(props.pageName);
    return (<>
        {props.children}
    </>);
}
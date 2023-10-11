import {H1} from '@cloudogu/ces-theme-tailwind';
import React, {useContext} from 'react';
import {t} from '../helpers/i18nHelpers';
import {ApplicationContext} from '../main';
import TitledPage from './TitledPage';

export default function ProtectedResource(props: {pageName: string, children: React.JSX.Element}){
    const {casUser} = useContext(ApplicationContext);
    const pageTitle = casUser?.admin ? props.pageName : `${t('pages.accessDenied.title')}: ${props.pageName}`;
    return <TitledPage pageName={pageTitle}>
        {!casUser.loading && !casUser.admin ?
            <H1 className="uppercase text-label-danger-font">{t('pages.accessDenied.text')}</H1>
            :
            <>
                {props.children}
            </>
        }
    </TitledPage>;
}
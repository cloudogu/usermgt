import {H1} from '@cloudogu/deprecated-ces-theme-tailwind';
import React, {useContext} from 'react';
import {t} from '../helpers/i18nHelpers';
import TitledPage from './TitledPage';
import { ApplicationContext } from './contexts/ApplicationContext';

export default function ProtectedResource(props: {pageName: string, children: React.JSX.Element}){
    const {casUser} = useContext(ApplicationContext);
    const pageTitle = casUser?.admin ? props.pageName : `${t('pages.accessDenied.title')}: ${props.pageName}`;
    return <TitledPage pageName={pageTitle}>
        {!casUser.loading && !casUser.admin ?
            <H1 className="uppercase text-label-danger-font" data-testid="access-denied-message">{t('pages.accessDenied.text')}</H1>
            :
            <>
                {props.children}
            </>
        }
    </TitledPage>;
}
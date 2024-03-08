import LaunchIcon from '@mui/icons-material/Launch';
import {Icon} from '@mui/material';
import injectSheet from 'react-jss';
import {twMerge} from 'tailwind-merge';
import type {AnchorHTMLAttributes} from 'react';
import {getLocale, translate, translateToPlainString} from "@cloudogu/ces-theme-tailwind";

const styles = {
    helpIcon: {
        fontSize: '1.4rem',
    },
    helpIconLink: {
        maxHeight: '50px',
    }
};

export type HelpLinkProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
    classes: any
};

function HelpLink({classes, ...props}: HelpLinkProps) {

    const locale = getLocale().includes('de') ? 'de' : 'en';
    const handbookLink = getLocale().includes('de') ? `https://docs.cloudogu.com/${locale}/usermanual/usermanagement/#synchronisation-von-accounts-und-gruppen`
        : `https://docs.cloudogu.com/${locale}/usermanual/usermanagement/#synchronization-of-accounts-and-groups`;

    return (
        <a id="documentation"
            className={twMerge(
                '!flex items-center',
                classes.helpIconLink)}
            href={handbookLink}
            target="_blank"
            rel="noopener noreferrer"
            aria-label={translateToPlainString('users.steps.link')}
            title={translateToPlainString('users.steps.link')}
            {...props}
        >
            <span>{translate('users.steps.link')}</span>
            <Icon component={LaunchIcon} className={classes.helpIcon}/>
        </a>
    );
}

export default injectSheet(styles)(HelpLink);

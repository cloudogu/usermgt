import { AnchorHTMLAttributes } from "react"
import LaunchIcon from '@mui/icons-material/Launch';
import { Icon } from "@mui/material";
import injectSheet from "react-jss";
import { twMerge } from "tailwind-merge";
import {getLocale, translate} from "./helpers/i18n";

const styles = {
    helpIcon: {
        fontSize: "1.4rem",
    },
    helpIconLink: {
        maxHeight: "50px",
    }
}

export type HelpLinkProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
    classes: any
};

function HelpLink({classes, ...props}: HelpLinkProps){

    const locale = getLocale().includes("de") ? "de" : "en";
    const handbookLink = `https://docs.cloudogu.com/${locale}/usermanual/admin/documentation/`;

    return (
        <a id="documentation"
           className={twMerge(
               "!flex items-center",
               classes.helpIconLink)}
           href={handbookLink}
           target="_blank"
           rel="noopener noreferrer"
           aria-label={translate("users.steps.link")}
           title={translate("users.steps.link")}
           {...props}
        >
            <span>{translate("users.steps.link")}</span>
            <Icon component={LaunchIcon} className={classes.helpIcon}/>
        </a>
    )
}

export default injectSheet(styles)(HelpLink);

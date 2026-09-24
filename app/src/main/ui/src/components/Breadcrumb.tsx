import React, {Fragment} from 'react';
import {Link} from 'react-router-dom';
import {t} from "i18next";

export type BreadcrumpItem = readonly [text: string, path?: string];

export type BreadcrumpProps = {
    items: readonly BreadcrumpItem[];
};

export function Breadcrumb({items}: BreadcrumpProps) {
    return (
        <nav aria-label={t("components.breadcrump.aria.label")}>
            <ol className="flex flex-wrap items-center gap-1 mb-4">
                {items.map(([text, path], index) => (
                    <Fragment key={`${text}-${path ?? ''}-${index}`}>
                        {index > 0 && <li aria-hidden="true">/</li>}
                        <li aria-current={index === items.length - 1 ? "page" : undefined}>
                            {path ? (
                                <Link className="text-brand hover:underline" to={path} tabIndex={0}>
                                    {text}
                                </Link>
                            ) : text}
                        </li>
                    </Fragment>
                ))}
            </ol>
        </nav>
    );
}

export default Breadcrumb;

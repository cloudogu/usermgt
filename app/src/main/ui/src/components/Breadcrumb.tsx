import React, {Fragment} from 'react';
import {Link} from 'react-router-dom';

export type BreadcrumpItem = readonly [text: string, path?: string];

export type BreadcrumpProps = {
    items: readonly BreadcrumpItem[];
};

export function Breadcrumb({items}: BreadcrumpProps) {
    return (
        <nav aria-label="Breadcrumb">
            <ol className="flex flex-wrap items-center gap-2">
                {items.map(([text, path], index) => (
                    <Fragment key={`${text}-${path ?? ''}-${index}`}>
                        {index > 0 && <li aria-hidden="true">/</li>}
                        <li>
                            {path ? (
                                <Link className="text-brand hover:underline" to={path}>
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

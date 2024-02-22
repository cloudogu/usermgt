import {H1, H2} from "@cloudogu/deprecated-ces-theme-tailwind";
import {useRouteError, isRouteErrorResponse} from "react-router-dom";
import {t} from "../helpers/i18nHelpers";

export default function ErrorPage() {
    const error = useRouteError();
    console.error(error);

    const renderError = (error: any) => {
        let errorMessage = t("generic.errors.general");
        let errorDetail: string|undefined ;

        if (isRouteErrorResponse(error)) {
            if (error.status === 404) {
                errorMessage = t("generic.errors.notFound");
            } else {
                errorDetail = `${error.status} - ${error.statusText}`;
            }
        }
        return (
            <>
                <H2>{errorMessage}</H2>
                {errorDetail && <p>{errorDetail}</p>}
            </>
        );
    };

    return (
        <div className="text-center">
            <H1>{t("generic.errors.title")}</H1>
            {renderError(error)}
        </div>
    );
}
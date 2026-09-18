import {createUseStyles} from "react-jss";

const useDoguSelectionStyles = createUseStyles({
    fontBold600: {
        fontWeight: 600,
    },
    fontDefault400: {
        fontWeight: 400,
    },
    checkboxFocus: {
        "&:focus-visible": {
            outline: "2px solid var(--ces-color-default-focus-outer)",
            outlineOffset: "2px",
        },
        "& > button:focus-visible": {
            outline: "2px solid var(--ces-color-default-focus-outer)",
            outlineOffset: "2px",
        },
    },
    doguSelectionHover: {
        "&:hover .hover-dark-bg": {
            backgroundColor: "var(--ces-color-brand-stronger)",
        },
        "&:hover .hover-dark-border": {
            borderColor: "var(--ces-color-brand-strongery)",
        },
        "&:hover .hover-neutral-border": {
            borderWidth: "2px",
        },
    },
});

export default useDoguSelectionStyles;

import {createUseStyles} from "react-jss";

const usePATFormStyles = createUseStyles({
    boldLabel: {
        "& label > span:first-of-type": {
            fontWeight: 600,
        },
    },
    dangerLabel: {
        "& label.text-danger > span": {
            color: "#CC3333"
        },
    },
    defaultTextLabel: {
        "& label > span": {
            color: "#0D1C26",
        },
    },
});

export default usePATFormStyles;

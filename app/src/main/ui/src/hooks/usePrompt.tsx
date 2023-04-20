import * as React from "react";
import {
    useBeforeUnload,
    unstable_useBlocker as useBlocker
} from "react-router-dom";


// from: https://gist.github.com/chaance/2f3c14ec2351a175024f62fd6ba64aa6#file-use-prompt-tsx
export function usePrompt(
    message: string | null | undefined | false,
    { beforeUnload }: { beforeUnload?: boolean } = {}
) {
    const blocker = useBlocker(
        React.useCallback(
            () => (typeof message === "string" ? !window.confirm(message) : false),
            [message]
        )
    );
    const prevState = React.useRef(blocker.state);
    React.useEffect(() => {
        if (blocker.state === "blocked") {
            blocker.reset();
        }
        prevState.current = blocker.state;
    }, [blocker]);

    useBeforeUnload(
        React.useCallback(
            (event) => {
                if (beforeUnload && typeof message === "string") {
                    event.preventDefault();
                    event.returnValue = message;
                }
            },
            [message, beforeUnload]
        ),
        { capture: true }
    );
}

// You can also reimplement the v5 <Prompt> component API
export function Prompt({ when, message, ...props }: PromptProps) {
    usePrompt(when ? message : false, props);
    return null;
}

interface PromptProps {
    when: boolean;
    message: string;
    beforeUnload?: boolean;
}
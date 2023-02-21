import {useEffect, useState} from "react";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type UndeletableGroups = string[];


export function useUndeletableGroups(): UndeletableGroups {
    const [undeletableGroups, setUndeletableGroups] = useState<UndeletableGroups>([]);

    useEffect(() => {
        fetch(contextPath + `/api/groups/undeletable`)
            .then(async function (response) {
                const json: UndeletableGroups = await response.json();
                setUndeletableGroups(json);
            });
    }, []);

    return undeletableGroups;
}

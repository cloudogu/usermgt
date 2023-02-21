import {useEffect, useState} from "react";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type Group = {
    name: string;
    description: string;
    members: string[];
}

export type GroupsResponse = {
    entries: Group[];
}

export function useGroups(): Group[] {
  const [groups, setGroups] = useState<Group[]>([]);

  useEffect(() => {
    fetch(contextPath + `/api/groups`)
      .then(async function (response) {
        const json: GroupsResponse = await response.json();
        setGroups(json.entries);
      });
  }, []);

  return groups;
}

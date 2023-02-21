import {useEffect, useState} from "react";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type User = {
    username: string;
    displayName: string;
    mail: string;
}

export type UsersResponse = {
    entries: User[];
}

export function useUsers(): User[] {
  const [groups, setGroups] = useState<User[]>([]);

  useEffect(() => {
    fetch(contextPath + `/api/users`)
      .then(async function (response) {
        const json: UsersResponse = await response.json();
        setGroups(json.entries);
      });
  }, []);

  return groups;
}

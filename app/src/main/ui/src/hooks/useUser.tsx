import {useEffect, useState} from "react";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type ApiUser = {
  principal: string;
}

export function useUser(): ApiUser {
  const [user, setUser] = useState<ApiUser>({principal: ""});

  useEffect(() => {
    fetch(contextPath + `/api/subject`)
      .then(async function (response) {
        const json: ApiUser = await response.json();
        setUser(json);
      });
  }, []);

  return user;
}

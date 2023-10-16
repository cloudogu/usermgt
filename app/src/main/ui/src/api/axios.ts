import axios from "axios";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export const Axios = axios.create({
    baseURL: `${contextPath}/api`,
});

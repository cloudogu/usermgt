import axios from "axios";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export const Axios = process.env.NODE_ENV === "development" ? axios.create({
    baseURL: `${contextPath}/api`,
    auth: {
        username: "gary",
        password: "admin"
    }
}) : axios.create({
    baseURL: `${contextPath}/api`,
});

import path from "path";
import react from "@vitejs/plugin-react";
import {defineConfig, loadEnv} from "vite";

const backendURL = "http://localhost:8084";

export default defineConfig(({command, mode}) => {
    const config = {
        resolve: {
            alias: [
                {find: "@cloudogu/deprecated-ces-theme-tailwind", replacement: path.resolve(__dirname, "./node_modules/@cloudogu/deprecated/ces-theme-tailwind")},
            ],
        },
        plugins: [react()],
        assetsInclude: ["**/*.svg"],
        define: {
            "process.env": process.env
        },
        base: "/usermgt/",
        build: {
            chunkSizeWarningLimit: 2048,
        }
    };

    if (command === "serve") {
        const env = loadEnv(mode, process.cwd(), "");

        config["server"] = {
            proxy: {
                "/usermgt/api": {
                    target: backendURL,
                    changeOrigin: true,
                    secure: false,
                    auth: `${env.AUTH_USER}:${env.AUTH_PASSWORD}`
                },
                "/usermgt/downloads": {
                    target: backendURL,
                    changeOrigin: true,
                    secure: false,
                    auth: `${env.AUTH_USER}:${env.AUTH_PASSWORD}`
                },
                "/usermgt/:import": {
                    target: backendURL,
                    changeOrigin: true,
                    secure: false,
                    auth: `${env.AUTH_USER}:${env.AUTH_PASSWORD}`
                }
            },
        };
    }

    return config;
});

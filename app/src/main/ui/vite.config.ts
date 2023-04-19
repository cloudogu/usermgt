import react from "@vitejs/plugin-react";
import {defineConfig, loadEnv} from "vite";

export default defineConfig(({command, mode}) => {
    const config = {
        plugins: [react()],
        assetsInclude: ["**/*.svg"],
        define: {
            "process.env": process.env
        },
        base: "/usermgt/",
        build: {
            chunkSizeWarningLimit: 2048
        }
    };

    if (command === "serve") {
        const env = loadEnv(mode, process.cwd(), "");

        config["server"] = {
            proxy: {
                "/usermgt/api": {
                    target: "https://192.168.56.2",
                    changeOrigin: true,
                    secure: false,
                    auth: `${env.AUTH_USER}:${env.AUTH_PASSWORD}`
                },
                "/usermgt/downloads": {
                    target: "http://192.168.56.2",
                    changeOrigin: true,
                    secure: false,
                    auth: `${env.AUTH_USER}:${env.AUTH_PASSWORD}`
                }
            },
        };
    }

    return config;
});
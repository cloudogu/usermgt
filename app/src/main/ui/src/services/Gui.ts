import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";

export type GuiConfig = {
    pwdResetPreselected: boolean;
    externalLdap: boolean;
};

export const GuiService = {
    async getGuiConfig(signal?: AbortSignal): Promise<GuiConfig> {
        const response = await Axios.get<GuiConfig>("/account/gui_config", {
            signal: signal
        });

        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load gui config: " + response.status);
        }

        return response.data;
    },
};

import {GuiService} from "../services/Gui";
import {useAPI} from "./useAPI";
import type {GuiConfig} from "../services/Gui";

const defaultGuiConfig: GuiConfig = {
    pwdResetPreselected: false,
    externalLdap: false
};

export function useGuiConfig() {
    const {data, isLoading, setData: setGuiConfig, error} = useAPI<GuiConfig>(GuiService.getGuiConfig);
    const guiConfig = data ?? defaultGuiConfig;

    return {guiConfig, isLoading, setGuiConfig, error};
}

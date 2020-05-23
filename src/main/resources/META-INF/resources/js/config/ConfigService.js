import { RestClientService } from "../RestClientService.js";

export class ConfigService {
    getCustomEventName() {
        return "ConfigService";
    }

    getServicePath() {
        return "/config";
    }

    async fetchData() {
        let response = await fetch(this.getServicePath());
        if (! response.ok) {
            window.location = "/config-read-error.html";
        }
        let data = await response.json();

        const configEvent = new CustomEvent(this.getCustomEventName(), {detail: data, bubbles: true});
        dispatchEvent(configEvent);
    }
}

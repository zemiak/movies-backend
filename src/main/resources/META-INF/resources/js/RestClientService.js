import { Cache } from "./Cache.js";
import { Config } from "./Config.js";

export class RestClientService {
    constructor() {
        this.cache = new Cache();
        this.config = new Config();
    }

    getCustomEventName() {
        throw "Not Implemented";
    }

    getCacheKeyName() {
        return "_" + this.getCustomEventName();
    }

    getServicePath() {
        throw "Not Implemented";
    }

    contains(name) {
        const key = undefined === name ? name : this.getCacheKeyName();
        return (undefined !== this.cache.get(key));
    }

    getBaseUri() {
        var url = new URL(window.location.href.replace(":8000/", ":" + this.config.getPort()));
        var base = url.protocol + "//" + url.host;
        if (base.includes("#")) {
            base = base.split('#')[0];
        }

        return base;
    }

    async fetchData() {
        const name = this.getCacheKeyName();

        if (! this.cache.contains(name)) {
            const uri = this.getBaseUri() + this.getServicePath();
            console.info("Fetching '" + uri + "' for name '" + name + "' ...");

            const response = await fetch(uri);
            const payload = await response.json();

            this.cache.set(name, payload);
        }

        this.dispatchDataEvent({"key": name});
    }

    dispatchDataEvent(data) {
        const folderDataEvent = new CustomEvent(this.getCustomEventName(), {detail: data, bubbles: true});
        dispatchEvent(folderDataEvent);
    }

    getData(key) {
        if (this.cache.contains(key)) {
            return this.cache.get(key);
        }

        throw new Error("RestClientService.getData: Cache data for " + key + " does not exist!");
    }
}

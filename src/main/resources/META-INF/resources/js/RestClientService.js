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

    contains() {
        const key = this.getCacheKeyName();
        return null === key ? false : undefined !== this.cache.get(key);
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
        const isCached = this.contains();
        const key = this.getCacheKeyName();
        console.info("RestClientService.fetchData: key is " + key + ", exists? " + (isCached ? "yes" : "no"));

        if (! isCached) {
            const uri = this.getBaseUri() + this.getServicePath();
            console.info("RestClientService.fetchData: Fetching '" + uri + "' for key '" + key + "' ...");

            const response = await fetch(uri);
            const payload = await response.json();

            if (null !== key) {
                this.cache.set(key, payload);
            } else {
                this.localCache = payload;
            }
        }

        this.dispatchDataEvent({"key": name});
    }

    dispatchDataEvent(data) {
        const folderDataEvent = new CustomEvent(this.getCustomEventName(), {detail: data, bubbles: true});
        dispatchEvent(folderDataEvent);
        console.info("RestClientService.fetchData: dispatched event", folderDataEvent);
    }

    getData() {
        const key = this.getCacheKeyName();
        if (null === key) {
            return this.localCache;
        }

        if (this.cache.contains(key)) {
            return this.cache.get(key);
        }

        throw new Error("RestClientService.getData: Cache data for " + key + " does not exist!");
    }
}

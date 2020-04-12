import { Cache } from "./Cache.js";
import { Config } from "./Config.js";

export class FolderService {
    constructor() {
        this.cache = new Cache();
        this.config = new Config();
    }

    contains(name) {
        return (undefined !== this.cache.get(name));
    }

    getBaseUri() {
        var url = new URL(window.location.href.replace(":8000/", ":" + this.config.getPort() + "/"));
        var base = url.protocol + "//" + url.host;
        if (base.includes("#")) {
            base = base.split('#')[0];
        }

        return base;
    }

    async fetchFolder(name) {
        if (! this.cache.contains(name)) {
            var uri = this.getBaseUri();

            if ("root" === name) {
                uri = uri + "/ui/root"
            }

            if (name.startsWith("g")) {
                uri = uri + "/genres/browse?id=" + name.substr(1);
            }

            if (name.startsWith("s")) {
                uri = uri + "/series/browse?id=" + name.substr(1);
            }

            console.info("Fetching folder: '" + uri + "' for name '" + name + "' ...");

            const response = await fetch(uri);
            const payload = await response.json();

            this.cache.set(name, payload);
        }

        this.dispatchDataEvent(name);
    }

    dispatchDataEvent(name) {
        const folderDataEvent = new CustomEvent(FolderService.eventName(), {detail: name, bubbles: true});
        dispatchEvent(folderDataEvent);
    }

    getFolder(name) {
        if (this.cache.contains(name)) {
            return this.cache.get(name);
        }

        throw new Error("FolderService.getFolder: Cache data for " + name + " does not exist!");
    }

    static eventName() {
        return "folder-data-event";
    }
}

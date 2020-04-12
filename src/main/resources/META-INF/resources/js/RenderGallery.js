import { FolderService } from "./FolderService.js"
import { Spinner } from "./Spinner.js";
import { TemplateService } from "./TemplateService.js";

export class RenderGallery extends HTMLElement {
    constructor() {
        super();
        this.gallery = document.querySelector("#gallery");

        this.service = new FolderService();
        this.spinner = new Spinner();
        this.template = new TemplateService(this.service.getBaseUri());
        this.currentFolder = "";
    }

    connectedCallback() {
        addEventListener(FolderService.eventName(), e => this.onFolderData(e));
        addEventListener("hashchange", e => this.onHashChange(e));
        this.render();
    }

    render() {
        const folder = this.getFolder();
        if (! this.service.contains(folder)) {
            this.spinner.show();
            this.service.fetchFolder(folder);
        } else {
            this.onFolderData({detail: folder});
        }
    }

    getFolder() {
        var folder = window.location.hash;
        if ("" == folder || "#" == folder) {
            folder = "root";
        }

        if (folder.startsWith("#genre=")) {
            folder = "g" + folder.substr(7);
        }

        if (folder.startsWith("#serie=")) {
            folder = "s" + folder.substr(7);
        }

        return folder;
    }

    onFolderData(e) {
        this.spinner.hide();

        const folder = e.detail;
        const data = this.service.getFolder(folder);

        console.info("RenderGallery/onFolderData: going to render for event ", e, " data is ", data);

        this.template.renderGallery(data, this.gallery);
    }

    onHashChange(e) {
        this.render();
    }
}

customElements.define("render-gallery", RenderGallery);

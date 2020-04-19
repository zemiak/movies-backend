import { html, render } from "../lib/lit-html.js";
import { RootService } from "./RootService.js";
import { BreadCrumbs } from "../BreadCrumbs.js";

export class RootView extends HTMLElement {
    constructor() {
        super();
        this.data = [];
        this.service = new RootService();
    }

    connectedCallback() {
        console.log("RootView.connectedCallback");
        addEventListener(this.service.getCustomEventName(), e => this.update(e));
        this.service.fetchData();
    }

    update(event) {
        const e = new CustomEvent(BreadCrumbs.eventName(), [{url: "/", title: "Home"}])
        console.log("Dispatched event ", e)
        this.dispatchEvent(e);

        this.data = this.service.getData(event.key);
        this.render();
    }

    render() {
        console.log("RootView.render");
        render(this.view(), this);
    }

    view() {
        // this.data == array of GuiDTO
        return html`<h1>Root</h1>`;
    }
}

customElements.define("root-view", RootView);

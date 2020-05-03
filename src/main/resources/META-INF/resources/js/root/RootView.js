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
        console.log("RootView.update: Received event ", event);

        const e = new CustomEvent(BreadCrumbs.eventName(), [{url: "/", title: "Home"}]);
        this.dispatchEvent(e);
        console.log("RootView.update: Dispatched event ", e);

        this.data = this.service.getData(event.detail.key);
        this.render();
    }

    render() {
        console.log("RootView.render: running lit-html render");
        render(this.view(), this);
    }

    view() {
        // this.data == array of GuiDTO
        console.log("RootView.view: data is", this.data);
        const items = [];
        this.data.forEach(item => {
            console.log(item);
            items.push(html`
            <div class="column is-one-quarter">
                <a href="${item.url}">
                    <figure class="image is-128x128">
                        <img src="${item.thumbnail}" alt="${item.title}">
                    </figure>
                </a>
            </div>`);
        });

        return html`
            <div class="columns is-multiline is-mobile is-centered">
                ${items}
            </div>`;
    }
}

customElements.define("root-view", RootView);

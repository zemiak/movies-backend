import { html, render } from "../lib/lit-html.js";

export class RootView extends HTMLElement {
    connectedCallback() {
        console.log("RootView.connectedCallback");
        this.render();
    }

    render() {
        console.log("RootView.render");
        render(this.view(), this);
    }

    view() {
        return html`<h1>Root</h1>`;
    }
}

customElements.define("root-view", RootView);

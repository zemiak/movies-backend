import { html, render } from "../lib/lit-html.js";

export class SerieView extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        render(this.view(), this);
    }

    view() {
        return html`<h1>Serie</h1>`;
    }
}

customElements.define("serie-view", SerieView);

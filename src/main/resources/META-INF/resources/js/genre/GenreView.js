import { html, render } from "../lib/lit-html.js";

export class GenreView extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        render(this.view(), this);
    }

    view() {
        return html`<h1>Genre</h1>`;
    }
}

customElements.define("genre-view", GenreView);

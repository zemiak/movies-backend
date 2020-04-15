import { html, render } from "../lib/lit-html.js";

export class MovieView extends HTMLElement {
    connectedCallback() {
        this.render();
    }

    render() {
        render(this.view(), this);
    }

    view() {
        return html`<h1>Movie</h1>`;
    }
}

customElements.define("movie-view", MovieView);

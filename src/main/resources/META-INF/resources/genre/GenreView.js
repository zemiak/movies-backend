import { render, html } from "../../../lib/lit-html.js";
import { GenresRestClient } from "./GenreRestClient.js.js";

class GenreView extends HTMLElement {
    connectedCallback() {
        this._render(); // when element is in DOM
        addEventListener("GenresRestClient", e => this.dataFetched(e));
        this.data = {};
    }

    _render() {
        const template=html`
            <div @click="${e => this.elementClicked(e)}" class="genre-box">Hello</div>
        `;

        render(template, this);
    }

    _renderData() {
        console.log("rendering", this.data);
        const template=html`
            <div @click="${e => this.elementClicked(e)}" class="genre-box">Hello</div>
            <h2>World</h2>
        `;

        render(template, this);

    }

    elementClicked(event) {
        new GenresRestClient().getAll();
    }

    dataFetched(event) {
        this.data = event.detail;
        this._renderData();
    }


}

customElements.define("genre-view", GenreView);
export { GenreView };

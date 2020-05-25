import { html, render } from "/js/lib/lit-html.js";
import { GenreService } from "/js/genre/GenreService.js";
import { SerieView } from "/js/serie/SerieView.js";
import { BreadCrumbs } from "/js/BreadCrumbs.js";

export class GenreTableView extends HTMLElement {
    constructor() {
        super();
        this.data = [];
        this.service = new GenreService();
        this.serieView = new SerieView();
    }

    connectedCallback() {
        addEventListener(this.service.getCustomEventName(), e => this.update(e));
        console.log("GenreTableView.connectedCallback");
        this.render();
    }

    update(event) {
        console.log("GenreTableView.update: Received event ", event);

        const e = new CustomEvent(BreadCrumbs.eventName(), [{url: "/", title: "Home"}]);
        this.dispatchEvent(e);
        console.log("GenreTableView.update: Dispatched event ", e);

        this.data = this.service.getData(event.detail.key);

        render(this.view(), this);
    }

    render() {
        var id = this.location.params.id;
        console.log("GenreTableView.render: running fetchData(" + id + ")");
        this.service.setId(id);
        this.service.fetchData();
    }

    view() {
        const items = [];
        this.data.forEach(item => {
            items.push("serie" ===  item.type ? this.renderSerieItem(item) : this.serieView.renderMovieItem(item));
        });

        return html`
            <div class="columns is-multiline is-mobile">
                ${items}
            </div>`;
    }

    renderSerieItem(item) {
        return html`
        <div class="column is-one-quarter-tablet is-half-mobile">
            <div class="card">
                <div class="card-image">
                    <figure class="image is-256x256">
                        <a href="/${item.type}/${item.id}">
                            <img class="is-rounded" src="${item.thumbnail}" alt="${item.title}"></img>
                        </a>
                    </figure>
                </div>
                <footer class="card-footer">
                    <a class="card-footer-item" href="/${item.type}/${item.id}">
                        ${item.title}
                    </a>
                </footer>
            </div>
        </div>`;
    }
}

customElements.define("genre-table-view", GenreTableView);

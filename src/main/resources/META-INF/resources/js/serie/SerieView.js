import { html, render } from "/js/lib/lit-html.js";
import { SerieService } from "/js/serie/SerieService.js";
import { BreadCrumbs } from "/js/BreadCrumbs.js";

export class SerieView extends HTMLElement {
    constructor() {
        super();
        this.data = [];
        this.service = new SerieService();
    }

    connectedCallback() {
        addEventListener(this.service.getCustomEventName(), e => this.update(e));
        console.log("SerieView.connectedCallback");
        this.render();
    }

    update(event) {
        console.log("SerieView.update: Received event ", event);

        const e = new CustomEvent(BreadCrumbs.eventName(), [{url: "/", title: "Home"}]);
        this.dispatchEvent(e);
        console.log("SerieView.update: Dispatched event ", e);

        this.data = this.service.getData(event.detail.key);

        render(this.view(), this);
    }

    render() {
        var id = this.location.params.id;
        console.log("SerieView.render: running fetchData(" + id + ")");
        this.service.setId(id);
        this.service.fetchData();
    }

    view() {
        const items = [];
        this.data.forEach(item => {
            var imageCode = html`<img src="${item.thumbnail}" alt="${item.title}"></img>`;
            var figureSize = "is-3by4";

            if ("serie" === item.type) {
                imageCode = html`<img class="is-rounded" src="${item.thumbnail}" alt="${item.title}"></img>`;
                figureSize = "is-256x256";
            }

            items.push(html`
            <div class="column is-one-quarter-tablet is-half-mobile">
                <div class="card">
                    <div class="card-image">
                        <figure class="image ${figureSize}">
                            <a href="/${item.type}/${item.id}">
                                ${imageCode}
                            </a>
                        </figure>
                    </div>
                    <footer class="card-footer">
                        <a class="card-footer-item" href="/${item.type}/${item.id}">
                            ${item.title}
                        </a>
                    </footer>
                </div>
            </div>`);
        });

        return html`
            <div class="columns is-multiline is-mobile">
                ${items}
            </div>`;
    }
}

customElements.define("serie-view", SerieView);

import { html, render } from "/js/lib/lit-html.js";
import { GenreTableService } from "/js/genre/GenreTableService.js";

export class GenreTableView extends HTMLElement {
    constructor() {
        super();
        this.data = [];
        this.service = new GenreTableService();
    }

    connectedCallback() {
        addEventListener(this.service.getCustomEventName(), e => this.update(e));
        console.log("GenreTableView.connectedCallback");
        this.render();
    }

    update(event) {
        console.log("GenreTableView.update: Received event ", event);

        this.data = this.service.getData(event.detail.key);

        render(this.view(), this);

        let datatable = new DataTable("#genreTable", {
            columns: ['Name', 'Position', 'Department'],
            data: [
                ['Tiger Nixon', 'System Architect', 'Tech'],
                ['Garrett Winters', 'Accountant', '']
            ]
        });
    }

    render() {
        console.log("GenreTableView.render: running fetchData()");
        this.service.fetchData();
    }

    view() {
        return html`
            <div id="genreTable">
            </div>`;
    }
}

customElements.define("genre-table-view", GenreTableView);

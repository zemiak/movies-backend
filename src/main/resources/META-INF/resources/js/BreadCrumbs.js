export class BreadCrumbs extends HTMLElement {
    connectedCallback() {
        addEventListener("hashchange", e => this.onHashChange(e));
        this.render();
    }

    onHashChange(e) {
        const newUrl = e.newURL;
        var folder;

        if (!newUrl.includes("#")) {
            folder = "";
        } else {
            folder = window.location.href.split('#')[1];
        }

        this.render();
    }

    render() {
        var html = '<div class="level-item"><nav class="breadcrumb" aria-label="breadcrumbs"><ul>';
        html += this.renderItem({url: "/#", title: "Home"});
        html += '</ul></nav></div>';

        this.innerHTML = html;
    }

    renderItem(item) {
        return `<li><a href="${item.url}">${item.title}</a></li>`;
    }
}

customElements.define("bread-crumbs", BreadCrumbs);

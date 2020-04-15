export class Router {
    constructor(outlet) {
        window.addEventListener("hashchange", e => this.onHashChange(e));
        this.outlet = outlet;
        this.oldChild = null;
        this.routes = [];
    }

    onHashChange(e) {
        var location = window.location.hash;
        location = null === location || undefined === location || "" === location ? "" : location.substr(1);
        console.log("Loading " + location);
        this.loadContent(location);
    }

    loadContent(hash) {
        var tagName = findTagName(hash);
        if (nul === tagName) {
            this.outlet.innerHTML = "<h1>NOT FOUND: " + hash + "</h1>";
            return;
        }

        var newChild = document.createElement(tagName);

        if (this.oldChild) {
            this.outlet.replaceChild(newChild, this.oldChild);
        } else {
            this.outlet.appendChild(newChild);
        }

        this.oldChild = newChild;
    }

    setRoutes(routes) {
        this.routes = routes;
    }

    findTagName(hash) {
        for (route in this.routes) {
            if (hash.startsWith(route.path)) {
                return route.component;
            }
        }

        return null;
    }
}

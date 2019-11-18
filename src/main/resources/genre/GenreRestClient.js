class GenresRestClient {
    constructor() {
        this.uri = "http://localhost:8080/genres/all"
    }

    async getAll() {
        const response = await fetch(this.uri);
        const data = await response.json();
        const event = new CustomEvent(this.constructor.name, {detail: data, bubbles: true});
        window.dispatchEvent(event);
    }
}

export { GenresRestClient };

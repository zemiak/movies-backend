export class Config {
    constructor() {
        this.config = JSON.parse(window._com_zemiak_movies_config);
    }

    getPort() {
        return this.config["port"];
    }
}

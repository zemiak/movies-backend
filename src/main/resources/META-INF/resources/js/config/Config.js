export class Config {
    constructor() {
        this.config = window._com_zemiak_movies_config;
    }

    getPort() {
        return this.config["port"];
    }
}

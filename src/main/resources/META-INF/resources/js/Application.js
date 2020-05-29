import { ConfigService } from "/js/config/ConfigService.js";

import { BreadCrumbs } from "/js/BreadCrumbs.js";
import { RootView } from "/js/root/RootView.js";
import { GenreView } from "/js/genre/GenreView.js";
import { GenreTableView } from "/js/genre/GenreTableView.js";
// import { GenreDetailView } from "/js/genre/GenreDetailView.js";
import { SerieView } from "/js/serie/SerieView.js";
import { SerieTableView } from "/js/serie/SerieTableView.js";
// import { SerieDetailView } from "/js/serie/SerieDetailView.js";
import { MovieView } from "/js/movie/MovieView.js";
import { MovieTableView } from "/js/movie/MovieTableView.js";
// import { MovieDetailView } from "/js/movie/MovieDetailView.js";
import { LanguageTableView } from "/js/language/LanguageTableView.js";
// import { LanguageDetailView } from "/js/language/LanguageDetailView.js";
import { SearchView } from "/js/search/SearchView.js";
import { UnknownView } from "/js/unknown/UnknownView.js";
import { AboutView } from "/js/about/AboutView.js";

import { Cache } from "/js/Cache.js";
import { Router } from '/js/lib/@vaadin/router.js';
import "/js/search/SearchFieldService.js";

export class Application {
    constructor() {
        this.service = new ConfigService();
        addEventListener(this.service.getCustomEventName(), e => this.boot(e));
        this.service.fetchData();
    }

    boot(event) {
        window._com_zemiak_movies_config = event.detail;

        new Cache().clear();

        const outlet = document.querySelector('#outlet');
        const router = new Router(outlet);
        router.setRoutes([
            {path: '/',     component: 'root-view'},
            {path: '/genre/:id',  component: 'genre-view'},
            {path: '/admin/genres',  component: 'genre-table-view'},
            {path: '/admin/genre/:id',  component: 'genre-detail-view'},
            {path: '/serie/:id',  component: 'serie-view'},
            {path: '/admin/series',  component: 'serie-table-view'},
            {path: '/admin/serie/:id',  component: 'serie-detail-view'},
            {path: '/movie/:id',  component: 'movie-view'},
            {path: '/admin/movies',  component: 'movie-table-view'},
            {path: '/admin/movie/:id',  component: 'movie-detail-view'},
            {path: '/search/:query',  component: 'search-view'},
            {path: '/about',  component: 'about-view'},
            {path: '/admin/languages',  component: 'language-table-view'},
            {path: '/admin/language/:id',  component: 'language-detail-view'},
            {path: '/(.*)',  component: 'unknown-view'}
        ]);
    }
}

import { BreadCrumbs } from "./js/BreadCrumbs.js";
import { RootView } from "./js/root/RootView.js";
import { GenreView } from "./js/genre/GenreView.js";
import { SerieView } from "./js/serie/SerieView.js";
import { MovieView } from "./js/movie/MovieView.js";
import { SearchView } from "./js/search/SearchView.js";
import { UnknownView } from "./js/unknown/UnknownView.js";
import { AboutView } from "./js/about/AboutView.js";
import { Cache } from "./js/Cache.js";
import { Router } from './js/lib/@vaadin/Router.js';
import "./js/search/SearchFieldService.js";

new Cache().clear();

const outlet = document.querySelector('#outlet');
const router = new Router(outlet);
router.setRoutes([
  {path: '/',     component: 'root-view'},
  {path: '/genre/:id',  component: 'genre-view'},
  {path: '/serie/:id',  component: 'serie-view'},
  {path: '/movie/:id',  component: 'movie-view'},
  {path: '/search/:query',  component: 'search-view'},
  {path: '/about',  component: 'about-view'},
  {path: '/(.*)',  component: 'unknown-view'}
]);

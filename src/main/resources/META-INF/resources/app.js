import { RenderItems } from "./js/RenderItems.js";
import { BreadCrumbs } from "./js/BreadCrumbs.js";
import { RootView } from "./js/root/RootView.js";
import { GenreView } from "./js/genre/GenreView.js";
import { SerieView } from "./js/serie/SerieView.js";
import { MovieView } from "./js/movie/MovieView.js";
import { UnknownView } from "./js/unknown/UnknownView.js";
import { AboutView } from "./js/about/AboutView.js";
import { Router } from './js/lib/@vaadin/Router.js';

const outlet = document.querySelector('#outlet');
const router = new Router(outlet);
router.setRoutes([
  {path: '/',     component: 'root-view'},
  {path: '/genre',  component: 'genre-view'},
  {path: '/serie',  component: 'serie-view'},
  {path: '/movie',  component: 'movie-view'},
  {path: '/about',  component: 'about-view'},
  {path: '/(.*)',  component: 'unknown-view'}
]);

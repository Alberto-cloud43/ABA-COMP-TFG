import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Rankings } from './pages/rankings/rankings';
import { Login } from './pages/login/login';
import { Perfil } from './pages/perfil/perfil';
import { ClanesHome } from './pages/clanes-home/clanes-home';
import { ClanPerfil } from './pages/clan-perfil/clan-perfil';
import { RegistroClan } from './pages/registro-clan/registro-clan';

export const routes: Routes = [
  { path: '',                  component: Home },
  { path: 'perfil/:username', component: Perfil },
  { path: 'rankings',          component: Rankings },
  { path: 'registro-clan',     component: RegistroClan },
  { path: 'login',             component: Login },
  { path: 'perfil',            component: Perfil },
  { path: 'clanes-home',       component: ClanesHome },
  { path: 'clan/:clanName',    component: ClanPerfil },
  { path: '**',                redirectTo: '' },
];
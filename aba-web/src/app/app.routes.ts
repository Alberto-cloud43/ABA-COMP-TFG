import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Profile } from './pages/profile/profile';
import { Rankings } from './pages/rankings/rankings';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'profile/:username', component: Profile },
  { path: 'rankings', component: Rankings }, 
];
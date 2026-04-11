import { Component } from '@angular/core';
import { SearchBar } from '../../components/search-bar/search-bar';
import { TopCard } from '../../components/top-card/top-card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SearchBar, TopCard],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

}

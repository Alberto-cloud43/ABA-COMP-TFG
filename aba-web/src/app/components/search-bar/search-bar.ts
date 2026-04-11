import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css'
})
export class SearchBar {

  searchName: string = '';

  constructor(private router: Router) {}

  search() {
    if (this.searchName.trim()) {
      this.router.navigate(['/profile', this.searchName]);
    }
  }
}

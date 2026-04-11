import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common'; // 👈 IMPORTANTE

@Component({
  selector: 'app-top-card',
  standalone: true,
  imports: [CommonModule], // 👈 AÑADE ESTO
  templateUrl: './top-card.html',
  styleUrl: './top-card.css'
})
export class TopCard {

  @Input() title: string = '';

  players = [
    { name: 'Player1', value: 120 },
    { name: 'Player2', value: 110 },
    { name: 'Player3', value: 95 },
    { name: 'Player4', value: 80 },
    { name: 'Player5', value: 70 }
  ];
}

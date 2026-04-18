import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PlayerStats } from '../../models/player-stats.model';

type Metric = 'kills' | 'wins' | 'time_played';

@Component({
  selector: 'app-top-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-card.html',
  styleUrl: './top-card.css'
})
export class TopCard implements OnChanges {

  @Input() title: string = '';
  @Input() metric: Metric = 'kills';
  @Input() data: PlayerStats[] = [];

  players: { name: string; value: string }[] = [];

  constructor(private router: Router) {}

  ngOnChanges(): void {
    this.players = this.data.map(p => ({
      name: p.username,
      value: this.formatValue(p[this.metric])
    }));
  }

  goToRankings(): void {
    this.router.navigate(['/rankings']);
  }

  private formatValue(val: number): string {
    if (this.metric === 'time_played') {
      const h = Math.floor(val / 3600);
      const m = Math.floor((val % 3600) / 60);
      const s = val % 60;
      if (h > 0) return `${h}h ${m}m`;
      if (m > 0) return `${m}m ${s}s`;
      return `${s}s`;
    }
    return val.toString();
  }
}
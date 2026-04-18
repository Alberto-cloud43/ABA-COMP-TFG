import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlayerStatsService } from '../../services/player-stats.service';
import { PlayerStats } from '../../models/player-stats.model';

type Metric = 'kills' | 'deaths' | 'wins' | 'losses' | 'damage_done' | 'damage_taken' | 'time_played';

@Component({
  selector: 'app-rankings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rankings.html',
  styleUrl: './rankings.css'
})
export class Rankings implements OnInit {

  players: PlayerStats[] = [];
  selectedMetric: Metric = 'kills';

  get selectedMetricIndex(): number {
  return this.metrics.findIndex(m => m.key === this.selectedMetric);}

  metrics: { key: Metric; label: string }[] = [
    { key: 'kills',        label: 'Kills' },
    { key: 'deaths',       label: 'Muertes' },
    { key: 'wins',         label: 'Victorias' },
    { key: 'losses',       label: 'Derrotas' },
    { key: 'damage_done',  label: 'Daño hecho' },
    { key: 'damage_taken', label: 'Daño recibido' },
    { key: 'time_played',  label: 'Tiempo jugado' },
  ];

  constructor(
    private playerStatsService: PlayerStatsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.playerStatsService.getAllStats().subscribe({
      next: (data: PlayerStats[]) => {
        this.players = data;
        this.sortPlayers();
        this.cdr.detectChanges();
      }
    });
  }

  selectMetric(metric: Metric): void {
    this.selectedMetric = metric;
    this.sortPlayers();
  }

  private sortPlayers(): void {
    this.players = [...this.players].sort((a, b) => b[this.selectedMetric] - a[this.selectedMetric]);
  }

  formatValue(player: PlayerStats): string {
    const val = player[this.selectedMetric];
    if (this.selectedMetric === 'time_played') {
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
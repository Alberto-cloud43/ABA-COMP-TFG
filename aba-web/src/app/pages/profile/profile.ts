import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerStatsService } from '../../services/player-stats.service';
import { PlayerStats } from '../../models/player-stats.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  player?: PlayerStats;
  username: string = '';

  constructor(
    private statsService: PlayerStatsService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.username = this.route.snapshot.paramMap.get('username') ?? '';
  }

  ngOnInit() {
    this.statsService.getStatsByUsername(this.username).subscribe(player => {
      this.player = player;
      this.cdr.detectChanges();
    });
  }

  formatPlayTime(seconds: number | undefined): string {
    if (!seconds) return '0s';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;

    if (h > 0) return `${h}h ${m}m`;
    if (m > 0) return `${m}m ${s}s`;
    return `${s}s`;
  }
}
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
}
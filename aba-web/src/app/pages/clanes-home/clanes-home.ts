import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PlayerStatsService } from '../../services/player-stats.service';
import { BtnPerfilComponent } from '../../components/btn-perfil/btn-perfil';

interface ClanStats {
  nombre_clan: string;
  wins: number;
}

@Component({
  selector: 'app-clanes-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, BtnPerfilComponent],
  templateUrl: './clanes-home.html',
  styleUrl: './clanes-home.css'
})
export class ClanesHome implements OnInit {

  clanSearch = '';
  clanes: ClanStats[] = [];

  constructor(
    private router: Router,
    private playerStatsService: PlayerStatsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.playerStatsService.getAllClanes().subscribe({
      next: (nombres: string[]) => {
        const peticiones = nombres.map(nombre =>
          this.playerStatsService.getClanStats(nombre).toPromise()
        );

        Promise.all(peticiones).then(resultados => {
          this.clanes = resultados
            .filter(r => r != null)
            .map(r => ({ nombre_clan: r!.username, wins: r!.wins }))
            .sort((a, b) => b.wins - a.wins)
            .slice(0, 5);
          this.cdr.detectChanges();
        });
      }
    });
  }

  search(): void {
    if (this.clanSearch.trim()) {
      this.router.navigate(['/clan', this.clanSearch.trim()]);
      this.clanSearch = '';
    }
  }

  irAClan(nombre: string): void {
    this.router.navigate(['/clan', nombre]);
  }
}
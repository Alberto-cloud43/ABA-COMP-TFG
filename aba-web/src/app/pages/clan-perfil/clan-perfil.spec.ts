import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClanPerfil } from './clan-perfil';

describe('ClanPerfil', () => {
  let component: ClanPerfil;
  let fixture: ComponentFixture<ClanPerfil>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClanPerfil],
    }).compileComponents();

    fixture = TestBed.createComponent(ClanPerfil);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

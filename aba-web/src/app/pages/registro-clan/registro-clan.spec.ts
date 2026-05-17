import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistroClan } from './registro-clan';

describe('RegistroClan', () => {
  let component: RegistroClan;
  let fixture: ComponentFixture<RegistroClan>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistroClan],
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroClan);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

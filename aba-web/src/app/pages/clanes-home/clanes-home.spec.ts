import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClanesHome } from './clanes-home';

describe('ClanesHome', () => {
  let component: ClanesHome;
  let fixture: ComponentFixture<ClanesHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClanesHome],
    }).compileComponents();

    fixture = TestBed.createComponent(ClanesHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

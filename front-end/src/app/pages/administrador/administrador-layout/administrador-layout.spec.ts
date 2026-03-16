import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministradorLayout } from './administrador-layout';

describe('AdministradorLayout', () => {
  let component: AdministradorLayout;
  let fixture: ComponentFixture<AdministradorLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministradorLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(AdministradorLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

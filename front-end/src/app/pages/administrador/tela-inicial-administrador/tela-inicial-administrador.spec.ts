import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaInicialAdministrador } from './tela-inicial-administrador';

describe('TelaInicialAdministrador', () => {
  let component: TelaInicialAdministrador;
  let fixture: ComponentFixture<TelaInicialAdministrador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TelaInicialAdministrador],
    }).compileComponents();

    fixture = TestBed.createComponent(TelaInicialAdministrador);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaInicialGerente } from './tela-inicial-gerente';

describe('TelaInicialGerente', () => {
  let component: TelaInicialGerente;
  let fixture: ComponentFixture<TelaInicialGerente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TelaInicialGerente],
    }).compileComponents();

    fixture = TestBed.createComponent(TelaInicialGerente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

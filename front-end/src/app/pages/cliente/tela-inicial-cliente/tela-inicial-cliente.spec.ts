import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TelaInicialCliente } from './tela-inicial-cliente';

describe('TelaInicialCliente', () => {
  let component: TelaInicialCliente;
  let fixture: ComponentFixture<TelaInicialCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TelaInicialCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(TelaInicialCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

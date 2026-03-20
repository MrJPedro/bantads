import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarClienteIndividual } from './consultar-cliente-individual';

describe('ConsultarClienteIndividual', () => {
  let component: ConsultarClienteIndividual;
  let fixture: ComponentFixture<ConsultarClienteIndividual>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultarClienteIndividual],
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultarClienteIndividual);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExtratoCliente } from './extrato-cliente';

describe('ExtratoCliente', () => {
  let component: ExtratoCliente;
  let fixture: ComponentFixture<ExtratoCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExtratoCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(ExtratoCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

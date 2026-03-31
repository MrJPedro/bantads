import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepositarCliente } from './depositar-cliente';

describe('DepositarCliente', () => {
  let component: DepositarCliente;
  let fixture: ComponentFixture<DepositarCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepositarCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(DepositarCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

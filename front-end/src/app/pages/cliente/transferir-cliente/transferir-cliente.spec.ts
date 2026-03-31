import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferirCliente } from './transferir-cliente';

describe('TransferirCliente', () => {
  let component: TransferirCliente;
  let fixture: ComponentFixture<TransferirCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferirCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferirCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

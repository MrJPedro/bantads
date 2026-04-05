import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';
import { DepositarCliente } from './depositar-cliente';

describe('DepositarCliente', () => {
  let component: DepositarCliente;
  let fixture: ComponentFixture<DepositarCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepositarCliente],
      providers: [
        {
          provide: AuthService,
          useValue: {
            getContaNumero: () => '1234'
          }
        },
        {
          provide: Cliente,
          useValue: {
            depositar: () => of({ conta: '1234', data: new Date().toISOString(), saldo: 100 })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DepositarCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

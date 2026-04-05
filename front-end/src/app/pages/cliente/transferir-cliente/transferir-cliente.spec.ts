import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';
import { TransferirCliente } from './transferir-cliente';

describe('TransferirCliente', () => {
  let component: TransferirCliente;
  let fixture: ComponentFixture<TransferirCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferirCliente],
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
            transferir: () => of({
              conta: '1234',
              data: new Date().toISOString(),
              destino: '5678',
              saldo: 90,
              valor: 10
            })
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TransferirCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

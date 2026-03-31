import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SacarCliente } from './sacar-cliente';

describe('SacarCliente', () => {
  let component: SacarCliente;
  let fixture: ComponentFixture<SacarCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SacarCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(SacarCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

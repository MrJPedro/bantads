import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultarClientesMelhores } from './consultar-clientes-melhores';

describe('ConsultarClientesMelhores', () => {
  let component: ConsultarClientesMelhores;
  let fixture: ComponentFixture<ConsultarClientesMelhores>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultarClientesMelhores],
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultarClientesMelhores);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AjustesAvanzadosComponent } from './ajustes-avanzados.component';

describe('AjustesAvanzadosComponent', () => {
  let component: AjustesAvanzadosComponent;
  let fixture: ComponentFixture<AjustesAvanzadosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AjustesAvanzadosComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(AjustesAvanzadosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

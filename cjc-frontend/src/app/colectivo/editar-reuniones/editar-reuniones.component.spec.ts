import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarReunionesComponent } from './editar-reuniones.component';

describe('EditarReunionesComponent', () => {
  let component: EditarReunionesComponent;
  let fixture: ComponentFixture<EditarReunionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [EditarReunionesComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(EditarReunionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

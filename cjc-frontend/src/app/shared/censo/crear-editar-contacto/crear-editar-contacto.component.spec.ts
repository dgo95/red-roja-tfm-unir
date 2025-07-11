import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearEditarContactoComponent } from './crear-editar-contacto.component';

describe('CrearEditarContactoComponent', () => {
  let component: CrearEditarContactoComponent;
  let fixture: ComponentFixture<CrearEditarContactoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [CrearEditarContactoComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(CrearEditarContactoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

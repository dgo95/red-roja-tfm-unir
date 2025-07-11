import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from 'chai'; // Import the 'expect' function from the 'chai' library

import { CrearEditarMilitanteComponent } from './crear-editar-militante.component';

describe('CrearEditarMilitanteComponent', () => {
  let component: CrearEditarMilitanteComponent;
  let fixture: ComponentFixture<CrearEditarMilitanteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [CrearEditarMilitanteComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(CrearEditarMilitanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).to.be.ok; // Use the 'expect' function to check if the component is truthy
  });
});

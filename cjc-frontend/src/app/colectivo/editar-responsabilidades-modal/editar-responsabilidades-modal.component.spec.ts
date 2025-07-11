import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarResponsabilidadesModalComponent } from './editar-responsabilidades-modal.component';

describe('EditarResponsabilidadesModalComponent', () => {
  let component: EditarResponsabilidadesModalComponent;
  let fixture: ComponentFixture<EditarResponsabilidadesModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [EditarResponsabilidadesModalComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(EditarResponsabilidadesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

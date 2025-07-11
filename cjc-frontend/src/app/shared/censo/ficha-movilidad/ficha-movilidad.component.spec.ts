import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FichaMovilidadComponent } from './ficha-movilidad.component';

describe('FichaMovilidadComponent', () => {
  let component: FichaMovilidadComponent;
  let fixture: ComponentFixture<FichaMovilidadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [FichaMovilidadComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(FichaMovilidadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

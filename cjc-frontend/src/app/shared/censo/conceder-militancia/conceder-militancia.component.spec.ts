import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConcederMilitanciaComponent } from './conceder-militancia.component';

describe('ConcederMilitanciaComponent', () => {
  let component: ConcederMilitanciaComponent;
  let fixture: ComponentFixture<ConcederMilitanciaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [ConcederMilitanciaComponent]
})
    .compileComponents();

    fixture = TestBed.createComponent(ConcederMilitanciaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

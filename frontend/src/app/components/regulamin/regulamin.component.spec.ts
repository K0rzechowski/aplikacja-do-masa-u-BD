import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegulaminComponent } from './regulamin.component';

describe('RegulaminComponent', () => {
  let component: RegulaminComponent;
  let fixture: ComponentFixture<RegulaminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegulaminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegulaminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

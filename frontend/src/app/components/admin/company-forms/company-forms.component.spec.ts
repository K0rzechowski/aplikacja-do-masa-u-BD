import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyFormsComponent } from './company-forms.component';

describe('CompanyFormsComponent', () => {
  let component: CompanyFormsComponent;
  let fixture: ComponentFixture<CompanyFormsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyFormsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyFormsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMassagesComponent } from './manage-massages.component';

describe('ManageMassagesComponent', () => {
  let component: ManageMassagesComponent;
  let fixture: ComponentFixture<ManageMassagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMassagesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageMassagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MassageBoxComponent } from './massage-box.component';

describe('MassageBoxComponent', () => {
  let component: MassageBoxComponent;
  let fixture: ComponentFixture<MassageBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MassageBoxComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MassageBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

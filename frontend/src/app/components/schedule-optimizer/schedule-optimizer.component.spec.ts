import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleOptimizerComponent } from './schedule-optimizer.component';

describe('ScheduleOptimizerComponent', () => {
  let component: ScheduleOptimizerComponent;
  let fixture: ComponentFixture<ScheduleOptimizerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleOptimizerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduleOptimizerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

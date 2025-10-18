import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { Alert, AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-alert',
  imports: [CommonModule],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlertComponent {
  alerts$;

  constructor(private alertService: AlertService) {
    this.alerts$ = this.alertService.alerts$;
  }

  close(id: string): void {
    this.alertService.remove(id);
  }

  getAlertClass(type: string): string {
    const classes = {
      'success' : 'text-green-800 border-green-300 bg-green-50',
      'error' : 'text-rose-800 border-rose-300 bg-rose-50',
      'warning' : 'text-yellow-800 border-yellow-300 bg-yellow-50',
      'info' : 'text-blue-800 border-blue-300 bg-blue-50',
    }

    return classes[type as keyof typeof classes] || 'bg-gray-500';
  }
}

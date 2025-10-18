import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Alert {
  id: string,
  message: string,
  type: 'success' | 'error' | 'warning' | 'info',
  duration?: number
}

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  private alertsSubject = new BehaviorSubject<Alert[]>([]);
  public alerts$: Observable<Alert[]> = this.alertsSubject.asObservable();

  show(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info', duration: number = 5000): void {
    const id = Math.random().toString(36).substring(7);
    const alert: Alert = {id, message, type, duration };

    const currentAlerts = this.alertsSubject.value;
    this.alertsSubject.next([...currentAlerts, alert]);

    if(duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, duration);
    }
  }

  remove(id: string): void {
    const currentAlerts = this.alertsSubject.value;
    this.alertsSubject.next(currentAlerts.filter(alert => alert.id !== id));
  }

  success(message: string, duration?: number): void {
    this.show(message, 'success', duration);
  } 

  error(message: string, duration?: number): void {
    this.show(message, 'error', duration);
  } 

  warning(message: string, duration?: number): void {
    this.show(message, 'warning', duration);
  } 

  info(message: string, duration?: number): void {
    this.show(message, 'info', duration);
  } 
}
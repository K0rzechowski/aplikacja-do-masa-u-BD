import { Directive, ElementRef, Input, OnChanges, Renderer2, SimpleChanges } from '@angular/core';
import { ReservationStatus } from '../models/reservation';

@Directive({
  selector: '[appStatusColor]',
  standalone: true
})
export class StatusColorDirective implements OnChanges {

  @Input() appStatusColor!: string | ReservationStatus;

  private currentClasses: string[] = [];

  constructor(private el: ElementRef, private renderer: Renderer2) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['appStatusColor']) {
      this.setColor();
    }
  }

  private setColor(): void {
    this.currentClasses.forEach(className => {
      this.renderer.removeClass(this.el.nativeElement, className);
    });
    this.currentClasses = [];

    let newClasses: string[] = [];

    switch (this.appStatusColor) {
      case ReservationStatus.RESERVED:
      case 'RESERVED':
        newClasses = [
          'bg-green-400/10',
          'text-green-600',
          'ring-green-400/20'
        ];
        break;
      case ReservationStatus.CANCELLED:
      case 'CANCELLED':
        newClasses = [
          'bg-rose-400/10',
          'text-rose-500',
          'ring-rose-400/20'
        ];
        break;
      case ReservationStatus.COMPLETED:
      case 'COMPLETED':
        newClasses = [
          'bg-blue-400/10',
          'text-blue-400',
          'ring-blue-400/20'
        ];
        break;
      default:
        newClasses = [
          'bg-slate-400/10',
          'text-slate-400',
          'ring-slate-400/20'
        ];
        break;
    }

    newClasses.forEach(className => {
      this.renderer.addClass(this.el.nativeElement, className);
    });
    this.currentClasses = newClasses;
  }
}

import { NgStyle } from '@angular/common';
import { Component, Input, ViewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartEvent, Chart, registerables, ChartType } from 'chart.js';
import { ReservationService } from '../../../services/reservation.service';
import { Reservation, ReservationStatus, ReservationStatusLabels } from '../../../models/reservation';
import { connectable, count, Observable } from 'rxjs';
import { Company } from '../../../models/company';

@Component({
  selector: 'app-finance-chart',
  imports: [MatIconModule, NgStyle, BaseChartDirective],
  templateUrl: './finance-chart.component.html',
  styleUrl: './finance-chart.component.css'
})
export class FinanceChartComponent {
  current = new Date();
  @Input() currentCompanyId: string | undefined;
  currentYearReservations: Reservation[] = [];

  lineChartData: ChartConfiguration['data'] = {
    datasets: [],
    labels: ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'],
  };
  lineChartOptions: ChartConfiguration['options'] = {};
  lineChartType: ChartType = 'line';
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  constructor(
    private reservationService: ReservationService,
  ) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    this.loadReservations();
  }

  loadReservations() {
    if (this.currentCompanyId) {
      this.reservationService.getReservationsByCompany(this.currentCompanyId).subscribe(reservations => {
        this.currentYearReservations = reservations.filter(reservation => {
          const reservationDate = new Date(reservation.shift.startTime);
          return reservationDate.getFullYear() === this.current.getFullYear() && reservation.status !== "CANCELLED";
        });

        const monthlyRevenue = new Array(12).fill(0);
        this.currentYearReservations.forEach(reservation => {
          const reservationDate = new Date(reservation.shift.startTime);
          const month = reservationDate.getMonth();
          monthlyRevenue[month] += reservation.massageForCompany.price;
        });

        this.lineChartData = {
          datasets: [
            {
              data: monthlyRevenue,
              label: 'Przychody (zł)',
              backgroundColor: '#00808133',
              borderColor: '#008081',
              pointBackgroundColor: '#008081',
              pointBorderColor: '#fff',
              pointHoverBackgroundColor: '#fff',
              pointHoverBorderColor: '#008081',
              fill: 'origin',
            }
          ]
        };

        this.chart?.update();
      });
    }
  }

  formatYear(date: Date): string {
    return date.getFullYear().toString();
  }

  prevYear() {
    this.current = new Date(this.current.getFullYear() - 1, this.current.getMonth(), 1);
    this.loadReservations();
  }

  nextYear() {
    this.current = new Date(this.current.getFullYear() + 1, this.current.getMonth(), 1);
    this.loadReservations();
  }
}

import { NgStyle } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartEvent, Chart, registerables, ChartType } from 'chart.js';
import { ReservationService } from '../../services/reservation.service';
import { Reservation, ReservationStatus, ReservationStatusLabels } from '../../models/reservation';
import { connectable, count, Observable } from 'rxjs';
import { Company } from '../../models/company';
import { CompanyService } from '../../services/company.service';

@Component({
  selector: 'app-statistics',
  imports: [MatIconModule, NgStyle, MatButtonModule, BaseChartDirective],
  templateUrl: './statistics.component.html',
  styleUrl: './statistics.component.css'
})
export class StatisticsComponent {

  @ViewChild(BaseChartDirective) barChartEmployee: BaseChartDirective<'bar'> | undefined;
  @ViewChild(BaseChartDirective) pieChartEmployee: BaseChartDirective | undefined

  status = Object.values(ReservationStatus);
  ReservationStatusLabels = ReservationStatusLabels;

  current = new Date();
  currentMonthReservations: Reservation[] = [];
  currentMonthEmployeeReservations: Reservation[] = [];
  selectedEmployee: string | null = null;
  showPieChartEmployee = false;

  barChartType = 'bar' as const;
  pieChartEmployeeType: ChartType = 'pie';
  barChartEmployeeData: ChartData<'bar'> = {
    datasets: []
  };

  pieChartEmployeeData: ChartData<'pie', number[], string | string[]> = {
    datasets: [],
  };

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    scales: {
      x: {},
      y: {},
    },
    plugins: {
      legend: {
        display: true,
      }
    },
  };

  pieChartOptions: ChartConfiguration['options'] = {
    plugins: {
      legend: {
        display: true,
        position: 'top',
      }
    }
  };

  get currentCompany$(): Observable<Company | null> {
    return this.companyService.currentCompany;
  }

  get currentCompanyValue(): Company | null {
    let currentCompany: Company | null = null;
    this.currentCompany$.subscribe(c => currentCompany = c);
    return currentCompany;
  }

  constructor(
    private reservationService: ReservationService,
    private companyService: CompanyService
  ) {
    Chart.register(...registerables);
    this.loadReservations();
  }

  getEmployeeReservations(employeeName: string): Reservation[] {
    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(this.currentMonthReservations));

    return currentMonthReservationsJSON.filter((r: any) => {
      const fullName = r.shift.assignedEmployee.name + " " + r.shift.assignedEmployee.surname;
      return fullName === employeeName;
    })
  }

  setBarChartEmployeeData(currentMonthReservations: Reservation[]) {
    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(currentMonthReservations));
    console.log(this.currentMonthReservations);

    const employeeReservationCounts: { [employeeName: string]: number } = {};

    currentMonthReservationsJSON.forEach((r: any) => {
      const employeeName = r.shift.assignedEmployee.name + " " + r.shift.assignedEmployee.surname;
      employeeReservationCounts[employeeName] = this.getEmployeeReservations(employeeName).length;
    });

    this.barChartEmployeeData = {
      labels: Object.keys(employeeReservationCounts),
      datasets: [
        { data: Object.values(employeeReservationCounts), label: 'Liczba wszystkich rezerwacji' }
      ]
    };

    this.barChartEmployee?.update();
  }

  loadReservations() {
    if (this.currentCompanyValue?.id) {
      this.reservationService.getReservationsByCompany(this.currentCompanyValue?.id).subscribe(data => {
        this.currentMonthReservations = data.filter(r => {
          const reservationDate = new Date(r.shift.startTime);
          return reservationDate.getFullYear() === this.current.getFullYear() && reservationDate.getMonth() === this.current.getMonth();
        });

        this.setBarChartEmployeeData(this.currentMonthReservations);
      });
    }
  }

  setPieChartEmployeeData(employeeName: string) {
    const employeeReservations = this.getEmployeeReservations(employeeName);

    const statusCounts: { [status: string]: number } = {};
    if (employeeReservations) {
      employeeReservations.forEach((r: Reservation) => {
        const status = ReservationStatusLabels[r.status];
        statusCounts[status] = (statusCounts[status] || 0) + 1;
      });

      this.pieChartEmployeeData = {
        labels: Object.keys(statusCounts),
        datasets: [{
          data: Object.values(statusCounts),
          backgroundColor: ['#fb7185', '#60a5fa', '#4ade80']
        }]
      };

      this.pieChartEmployee?.update();
    }

  }

  chartEmployeeClicked({
    event,
    active,
  }: {
    event?: ChartEvent;
    active?: object[];
  }) {
    if (active && active.length > 0) {
      const clickedIndex = (active[0] as any).index;
      if (clickedIndex) {
        const employeeName = this.barChartEmployeeData.labels?.[clickedIndex] as string;
        this.selectedEmployee = employeeName;
        this.setPieChartEmployeeData(employeeName);
        this.showPieChartEmployee = true;
      }
    }
  }

  prev() {
    this.current = new Date(this.current.getFullYear(), this.current.getMonth() - 1, 1);
    console.log(this.current);
    this.loadReservations();
  }

  next() {
    this.current = new Date(this.current.getFullYear(), this.current.getMonth() + 1, 1);
    this.loadReservations();
  }

  formatDate(date: Date): string {
    return date.toLocaleString('pl-PL', { month: 'long', year: 'numeric' });
  }

}

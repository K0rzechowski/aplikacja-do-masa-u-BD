import { NgStyle } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartEvent, Chart, registerables, ChartType } from 'chart.js';
import { ReservationService } from '../../../services/reservation.service';
import { Reservation, ReservationStatus, ReservationStatusLabels } from '../../../models/reservation';
import { connectable, count, Observable } from 'rxjs';
import { Company } from '../../../models/company';
import { CompanyService } from '../../../services/company.service';
import { User } from '../../../models/auth/user';
import { AuthenticationService } from '../../../services/authentication.service';
import { NoteFormDialogComponent } from "../../note-form-dialog/note-form-dialog.component";
import { FinanceChartComponent } from "../finance-chart/finance-chart.component";

@Component({
  selector: 'app-statistics',
  imports: [MatIconModule, NgStyle, MatButtonModule, BaseChartDirective, NoteFormDialogComponent, FinanceChartComponent],
  templateUrl: './statistics.component.html',
  styleUrl: './statistics.component.css'
})
export class StatisticsComponent {

  @ViewChild(BaseChartDirective) barChartEmployee: BaseChartDirective<'bar'> | undefined;
  @ViewChild(BaseChartDirective) pieChartEmployee: BaseChartDirective | undefined;
  @ViewChild(BaseChartDirective) barChartMassage: BaseChartDirective<'bar'> | undefined;
  @ViewChild(BaseChartDirective) pieChartMassage: BaseChartDirective | undefined;

  status = Object.values(ReservationStatus);
  ReservationStatusLabels = ReservationStatusLabels;

  current = new Date();
  currentMonthReservations: Reservation[] = [];
  currentMonthEmployeeReservations: Reservation[] = [];
  currentMonthMassageReservations: Reservation[] = [];
  selectedEmployee: string | null = null;
  showPieChartEmployee = false;
  selectedMassage: string | null = null;
  showPieChartMassage = false;

  barChartType = 'bar' as const;
  pieChartType: ChartType = 'pie';
  barChartEmployeeData: ChartData<'bar'> = {
    datasets: []
  };

  pieChartEmployeeData: ChartData<'pie', number[], string | string[]> = {
    datasets: [],
  };

  barChartMassageData: ChartData<'bar'> = {
    datasets: []
  };

  pieChartMassageData: ChartData<'pie', number[], string | string[]> = {
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

  get currentUser$(): Observable<User | null> {
    return this.authService.currentUser;
  }

  get currentUserValue(): User | null {
    let currentUser: User | null = null;
    this.currentUser$.subscribe(user => currentUser = user);
    return currentUser;
  }

  constructor(
    private reservationService: ReservationService,
    private authService: AuthenticationService
  ) {
    Chart.register(...registerables);
    this.loadReservations();
  }

  prev() {
    this.current = new Date(this.current.getFullYear(), this.current.getMonth() - 1, 1);
    this.loadReservations();
    this.showPieChartEmployee = false;
    this.showPieChartMassage = false;
  }

  next() {
    this.current = new Date(this.current.getFullYear(), this.current.getMonth() + 1, 1);
    this.loadReservations();
    this.showPieChartEmployee = false;
    this.showPieChartMassage = false;
  }

  formatDate(date: Date): string {
    return date.toLocaleString('pl-PL', { month: 'long', year: 'numeric' });
  }

  loadReservations() {
    if (this.currentUserValue?.companyId) {
      this.reservationService.getReservationsByCompany(this.currentUserValue?.companyId).subscribe(data => {
        this.currentMonthReservations = data.filter(r => {
          const reservationDate = new Date(r.shift.startTime);
          return reservationDate.getFullYear() === this.current.getFullYear() && reservationDate.getMonth() === this.current.getMonth();
        });

        this.setBarChartEmployeeData(this.currentMonthReservations);
        this.setBarChartMassageData(this.currentMonthReservations);
      });
    }
  }

  setBarChartEmployeeData(currentMonthReservations: Reservation[]) {
    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(currentMonthReservations));

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
          backgroundColor: ['#4ade80', '#fb7185','#60a5fa',]
        }]
      };

      this.pieChartEmployee?.update();
    }
  }

  getEmployeeReservations(employeeName: string): Reservation[] {
    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(this.currentMonthReservations));

    return currentMonthReservationsJSON.filter((r: any) => {
      const fullName = r.shift.assignedEmployee.name + " " + r.shift.assignedEmployee.surname;
      return fullName === employeeName;
    })
  }

  setBarChartMassageData(currentMonthReservations: Reservation[]) {
    const massageReservationsCounts: { [massageName: string]: number } = {};

    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(this.currentMonthReservations));

    currentMonthReservationsJSON.forEach((r: any) => {
      const massageName = r.massageForCompany.massage.name;
      massageReservationsCounts[massageName] = this.getMassageReservations(massageName, currentMonthReservationsJSON).length;
    });

    this.barChartMassageData = {
      labels: Object.keys(massageReservationsCounts),
      datasets: [
        { data: Object.values(massageReservationsCounts), label: 'Liczba wszystkich rezerwacji' }
      ]
    };

    this.barChartMassage?.update();
  }

  setPieChartMassageData(massageName: string) {
    const currentMonthReservationsJSON = JSON.parse(JSON.stringify(this.currentMonthReservations));
    const massageReservations = this.getMassageReservations(massageName, currentMonthReservationsJSON);

    const statusCounts: { [status: string]: number } = {};
    if (massageReservations) {
      massageReservations.forEach((r: Reservation) => {
        const status = ReservationStatusLabels[r.status];
        statusCounts[status] = (statusCounts[status] || 0) + 1;
      });

      this.pieChartMassageData = {
        labels: Object.keys(statusCounts),
        datasets: [{
          data: Object.values(statusCounts),
          backgroundColor: ['#4ade80', '#fb7185','#60a5fa',]
        }]
      };

      this.pieChartMassage?.update();
    }
  }

  getMassageReservations(massageName: string, JSON: any): Reservation[] {
    return JSON.filter((r: any) => {
      return r.massageForCompany.massage.name === massageName;
    });
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
      if (clickedIndex  >= 0) {
        const employeeName = this.barChartEmployeeData.labels?.[clickedIndex] as string;
        this.selectedEmployee = employeeName;
        this.setPieChartEmployeeData(employeeName);
        this.showPieChartEmployee = true;
      }
    }
  }

  chartMassageClicked({
    event,
    active,
  }: {
    event?: ChartEvent;
    active?: object[];
  }) {
    if (active && active.length > 0) {
      const clickedIndex = (active[0] as any).index;
      if (clickedIndex >= 0) {
        const massage = this.barChartMassageData.labels?.[clickedIndex] as string;
        this.selectedMassage = massage;
        this.setPieChartMassageData(massage);
        this.showPieChartMassage = true;
      }
    }
  }
}

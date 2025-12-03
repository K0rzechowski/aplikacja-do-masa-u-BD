import { Component } from '@angular/core';
import { Company } from '../../models/company';
import { Employee } from '../../models/employee';
import { ActivatedRoute } from '@angular/router';
import { CompanyService } from '../../services/company.service';
import { EmployeeService } from '../../services/employee.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { Massage } from '../../models/massage';
import { MassageService } from '../../services/massage.service';

@Component({
  selector: 'app-employees',
  imports: [MatFormFieldModule, MatSelectModule],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent {
  company: Company | undefined;
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  massages: Massage[] = [];
  selectedMassageId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private employeeService: EmployeeService,
    private massageService: MassageService
  ) {
    this.route.paramMap.subscribe(params => {
      const city = params.get('city') || '';
      const name = params.get('name') || '';
      
      this.companyService.getCompanyByNameAndCity(name, city).subscribe((company) => {
        this.company = company;
        if (this.company?.id) {
          this.employeeService.getEmployeesByCompany(this.company.id).subscribe((data: Employee[]) => {
            this.employees = data
            this.filteredEmployees = data;

            this.extractUniqueMassages();
          });
        }
      });
    }) 
  }

  extractUniqueMassages() {
    const massageMap = new Map<string, Massage>();
    this.employees.forEach(employee => {
      employee.massages?.forEach(massage => {
        if (massage.id && !massageMap.has(massage.id)) {
          massageMap.set(massage.id, massage);
        }
      });
    });
    this.massages = Array.from(massageMap.values());
  }

  onMassageChange(massageId: string | null) {
    this.selectedMassageId = massageId;
    this.filterEmployees();
  }

  filterEmployees() {
    if (this.selectedMassageId) {
      this.filteredEmployees = this.employees.filter(employee =>
        employee.massages?.some(massage => massage.id === this.selectedMassageId)
      );
    } else {
      this.filteredEmployees = this.employees;
    }
  }
}

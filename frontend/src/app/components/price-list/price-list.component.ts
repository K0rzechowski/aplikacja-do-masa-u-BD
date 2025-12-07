import { Component } from '@angular/core';
import { Company } from '../../models/company';
import { Massage } from '../../models/massage';
import { ActivatedRoute } from '@angular/router';
import { CompanyService } from '../../services/company.service';
import { MassageService } from '../../services/massage.service';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-price-list',
  imports: [ CommonModule],
  templateUrl: './price-list.component.html',
  styleUrl: './price-list.component.css'
})
export class PriceListComponent {
  company: Company | undefined;
  massages: Massage[] = [];

  constructor(
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private massageService: MassageService
  ) {
    this.route.paramMap.subscribe(params => {
      const city = params.get('city') || '';
      const name = params.get('name') || '';
      
      this.companyService.getCompanyByNameAndCity(name, city).subscribe((company) => {
        this.company = company;
        if (this.company?.id) {
          this.massageService.getMassagesByCompany(this.company.id).subscribe((data: Massage[]) =>
            this.massages = data
        )}
      });
    }) 
  }
}

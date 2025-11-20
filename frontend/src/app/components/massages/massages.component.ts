import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CompanyService } from '../../services/company.service';
import { MassageService } from '../../services/massage.service';
import { Company } from '../../models/company';
import { Massage } from '../../models/massage';
import { NgOptimizedImage } from "@angular/common";

@Component({
  selector: 'app-massages',
  imports: [NgOptimizedImage],
  templateUrl: './massages.component.html',
  styleUrl: './massages.component.css'
})
export class MassagesComponent {
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

import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatChipSet, MatChip, MatChipGrid, MatChipRow, MatChipsModule } from "@angular/material/chips";
import { MatFormField, MatInputModule, MatLabel } from "@angular/material/input";
import { MatSelect, MatOption, MatSelectModule } from "@angular/material/select";
import { Symptom } from '../../models/symptom';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MassageService } from '../../services/massage.service';
import { AlertService } from '../../services/alert.service';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatAutocomplete, MatAutocompleteModule, MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatFormFieldModule } from '@angular/material/form-field';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/company';
import { Massage } from '../../models/massage';
import { catchError, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { SearchComponent } from "../search/search.component";

@Component({
  selector: 'app-recommendations',
  imports: [MatIconModule, MatOption, ReactiveFormsModule, MatAutocompleteModule, MatLabel, MatFormField, FormsModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule, MatChipsModule, MatSelectModule, SearchComponent],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.css'
})
export class RecommendationsComponent {

  selectedSymptoms: Symptom[] = [];
  dataType: string = 'symptom';
  companyCities: string[] = [];
  selectedCity: string = '';
  recommendedMassages: Massage[] = [];

  constructor(
    private msgService: MassageService,
    private companyService: CompanyService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {
    this.companyService.getAll().subscribe((data: Company[]) => {
      this.companyCities = Array.from(new Set(data.map(company => company.city)));
    });
  }
  
  getSelectedSymptoms(selected: Symptom[]) {
    this.selectedSymptoms = selected;
  }

  // // getMassages(symptomIds: string[], city: string): Observable<Massage[]> {
    // //   const massageRequests = symptomIds.map(id =>
  // //     this.msgService.getMassagesBySymptom(id)
  // //   )
  // //   console.log("req: ", massageRequests)
  // //   return forkJoin(massageRequests).pipe(
  // //     map(results => {
  // //       const allMassages = results.flat();
  // //       const uniqueMassages = Array.from (
  // //         new Map(allMassages.map(m => [m.id, m])).values()
  // //       );
  // //       console.log("Unique:", uniqueMassages)
  // //       return uniqueMassages;
  // //     }),
  // //     switchMap(massagesWithSymptoms =>
  // //       this.companyService.getCompaniesByCity(city).pipe(
  // //         map(companiesInCity =>
  // //           massagesWithSymptoms.filter(m => companiesInCity.some(c => c.id === m.id ))
  // //         )
  // //       )
  // //     ),
  // //     catchError(error => {
  // //       this.alertService.error("Błąd podczas pobierania masaży", error);
  // //       return of([]);
  // //     })
  // //   )
  // // }

  // seachForMassages() {
  //   console.log("Final: ",this.getMassages(this.getSelectedSymptomsIds(), this.selectedCity));
  //   // this.getMassages(this.getSelectedSymptomsIds(), this.selectedCity).subscribe(massages => {
  //   //   this.recommendedMassages = massages;
  //   //   console.log("Final: ", massages)
  //   // });
  // }
}

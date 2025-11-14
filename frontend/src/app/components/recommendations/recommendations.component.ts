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

@Component({
  selector: 'app-recommendations',
  imports: [MatIconModule, MatOption, ReactiveFormsModule, MatAutocompleteModule, MatLabel, MatFormField, FormsModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule, MatChipsModule, MatSelectModule],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.css'
})
export class RecommendationsComponent {

  searchControl = new FormControl('');
  allSymptoms: Symptom[] = [];
  selectedSymptoms = signal<Symptom[]>([]);
  filteredSymptoms: Symptom[] = [];
  companyCities: string[] = [];
  selectedCity: string = '';
  recommendedMassages: Massage[] = [];

  announcer = inject(LiveAnnouncer);

  constructor(
    private msgService: MassageService,
    private companyService: CompanyService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {
    this.loadSymptoms();
    this.setupSearch();

    this.companyService.getAll().subscribe((data: Company[]) => {
      this.companyCities = Array.from(new Set(data.map(company => company.city)));
    });
  }

  setupSearch() {
    this.searchControl.valueChanges.subscribe(value => {
      const searchTerm = typeof value === 'string' ? value : '';
      this.filterByName(searchTerm);
    });
  }

  filterByName(searchTerm: string) {
    const term = searchTerm.toLowerCase();
    this.filteredSymptoms = this.getAvaiableSymptoms().filter(s => 
      s.name.toLowerCase().includes(term)
    );
  }

  loadSymptoms() {
    this.msgService.getAllSymptoms().subscribe((data: Symptom[]) => {
      this.allSymptoms = data;
      this.filteredSymptoms = this.getAvaiableSymptoms();
      this.cdr.detectChanges(); 
    });
  }

  onSymptomSelected(event: MatAutocompleteSelectedEvent) {
    const symptom = event.option.value as Symptom;
    this.addSymptom(symptom);
    this.searchControl.setValue('');
    this.filteredSymptoms = this.getAvaiableSymptoms();
  }

  addSymptom(symptom: Symptom) {
    const isAlreadySelected = this.selectedSymptoms().some(s => s.id === symptom.id);
    if (!isAlreadySelected) {
      this.selectedSymptoms.update(symptoms => [...symptoms, symptom]);
      this.announcer.announce(`Dodano symptom: ${symptom.name}`);
    }    
  }

  removeSymptom(symptom: Symptom) {
    this.selectedSymptoms.update(symptoms => {
      const filteredSymptoms = symptoms.filter(m => m.id !== symptom.id);
      this.announcer.announce(`Usunięto symptom: ${symptom.name}`);
      return filteredSymptoms;
    });
    this.filteredSymptoms = this.getAvaiableSymptoms();
  }

  getSelectedSymptomsIds(): string[] {
    return this.selectedSymptoms().map(symptom => symptom.id!);
  }

  getAvaiableSymptoms(): Symptom[] {
    const selectedIds = this.getSelectedSymptomsIds();
    return this.allSymptoms.filter(symptom => !selectedIds.includes(symptom.id!));
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

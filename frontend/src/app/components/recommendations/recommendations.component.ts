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
import { AsyncPipe, NgOptimizedImage } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CompanyService } from '../../services/company.service';
import { Company } from '../../models/company';
import { Massage, MassageSymptom } from '../../models/massage';
import { catchError, forkJoin, map, Observable, of, switchMap } from 'rxjs';
import { SearchComponent } from "../search/search.component";
import { MassageBoxComponent } from '../massage-box/massage-box.component';

@Component({
  selector: 'app-recommendations',
  imports: [MatIconModule, MatOption, ReactiveFormsModule, MatAutocompleteModule, MatLabel, MatFormField, FormsModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule, MatChipsModule, MatSelectModule, SearchComponent, MassageBoxComponent, NgOptimizedImage],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.css'
})
export class RecommendationsComponent {

  selectedSymptoms: Symptom[] = [];
  dataType: string = 'symptom';
  companyCities: string[] = [];
  selectedCity: string = '';
  recommendedMassages: MassageSymptom[] = [];
  randomMassage: Massage | undefined;
  massageEffectivenessMap: Map<string, { symptoms: string[], effectivenesses: string[]}> = new Map();

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
    console.log(selected)
    this.selectedSymptoms = selected;
  }

  getRandomMassage(city: string) {
    this.msgService.getMassagesByCity(city).subscribe(m => {
      if (m && m.length > 0) {
        const randomIndex = Math.floor(Math.random() * m.length);
        this.randomMassage = m[randomIndex];
      }
    })
  }

  categoriseEffectiveness(score: number): string {
    let text = '';
    if (score < 2.5) {
      text = "małym";
    } else if (score >= 2.5 && score < 4 ) {
      text = "średnim";
    } else if (score >= 4) {
      text = "dużym";
    }
    return text + " stopniu";
  }

  getMassages(symptoms: Symptom[], city: string) {
    this.recommendedMassages = [];
    this.massageEffectivenessMap.clear();

    for(let symptom of symptoms) {
      if (symptom.id) {
        this.msgService.getMassagesBySymptom(symptom.id, city).subscribe(m => {
          for(let massage of m) {
            const exists = this.recommendedMassages.some(
              existing => existing.massageId === massage.massageId && existing.companyName === massage.companyName
            );

            if (!exists) {
              this.recommendedMassages.push(massage);
            }

            if (massage.massageId) {
              if (!this.massageEffectivenessMap.has(massage.massageId)) {
                this.massageEffectivenessMap.set(massage.massageId, {
                  symptoms: [],
                  effectivenesses: []
                });
              }

              const mapEntry = this.massageEffectivenessMap.get(massage.massageId)!;

              if (!mapEntry.symptoms.includes(massage.symptomName)) {
                mapEntry.symptoms.push(massage.symptomName);
                mapEntry.effectivenesses.push(this.categoriseEffectiveness(massage.effectivenessScore))
              }
            }
          }
        });
      }
    }
  }

  findMassages() {
    this.getMassages(this.selectedSymptoms, this.selectedCity);
    this.getRandomMassage(this.selectedCity);
  }
}

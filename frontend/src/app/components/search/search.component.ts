import { ChangeDetectorRef, Component, EventEmitter, inject, Input, Output, signal, SimpleChange, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Symptom } from '../../models/symptom';
import { Massage } from '../../models/massage';
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { MassageService } from '../../services/massage.service';
import { CompanyService } from '../../services/company.service';
import { AlertService } from '../../services/alert.service';
import { Company } from '../../models/company';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent, MatOption } from '@angular/material/autocomplete';
import { MAT_INPUT_CONFIG, MatFormField, MatInput, MatInputModule, MatLabel } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-search',
  imports: [MatIconModule, MatOption, ReactiveFormsModule, MatAutocompleteModule, MatFormField, FormsModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule, MatChipsModule, MatSelectModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent<T extends Symptom | Massage> {
  allData: T[] = [];
  @Input() dataType: string = '';
  @Input() companyId: string = '';
  @Input() alreadySelectedData: T[] = [];
  searchControl = new FormControl('');
  selectedData = signal<T[]>([]);
  @Output() selected: EventEmitter<T[]> = new EventEmitter<T[]>();
  filteredData: T[] = [];
  @ViewChild(MatInput) input!: MatInput;

  announcer = inject(LiveAnnouncer);

  constructor(
    private msgService: MassageService,
    private companyService: CompanyService,
    private alertService: AlertService,
    private cdr: ChangeDetectorRef
  ) {
  }
  
  ngOnInit() {
    this.loadData();
    this.setupSearch();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['alreadySelectedData'] && !changes['alreadySelectedData'].firstChange) {
      this.selectedData.set(this.alreadySelectedData as any);
      this.filteredData = this.getAvaiableData();
    }
  }

  setupSearch() {
    this.searchControl.valueChanges.subscribe(value => {
      const searchTerm = typeof value === 'string' ? value : '';
      this.filterByName(searchTerm);
    });
  }

  filterByName(searchTerm: string) {
    const term = searchTerm.toLowerCase();
    this.filteredData = this.getAvaiableData().filter(s => 
      s.name.toLowerCase().includes(term)
    );
  }

  loadData() {
    if (this.dataType === 'symptom') {
      this.msgService.getAllSymptoms().subscribe((data: Symptom[]) => {
        this.allData = data as T[];
        this.filteredData = this.getAvaiableData();
        this.cdr.detectChanges(); 
      });
      if (this.alreadySelectedData.length > 0) {
        this.selectedData.set(this.alreadySelectedData);
      }
    } else if (this.dataType === 'massage' && this.companyId !== '') {
      this.msgService.getMassagesByCompany(this.companyId).subscribe((data: Massage[]) => {
        this.allData = data as T[];
        this.filteredData = this.getAvaiableData();
        this.cdr.detectChanges(); 
      });
      if (this.alreadySelectedData.length > 0) {
        this.selectedData.set(this.alreadySelectedData );
      }
    }
  }

  onDataSelected(event: MatAutocompleteSelectedEvent) {
    let chip: T;
    if (this.dataType === 'symptom') {
      chip = event.option.value as T;
      this.addData(chip);
    } else if (this.dataType === 'massage') {
      chip = event.option.value as T;
      this.addData(chip);
    }
    this.searchControl.setValue('');
    this.filteredData = this.getAvaiableData();
    if (this.input) {
      this.input.value='';
    }
  }

  addData(chip: T) {
    const isAlreadySelected = this.selectedData().some(c => c.id === chip.id);
    if (!isAlreadySelected) {
      this.selectedData.update(chips => [...chips, chip]);
      this.selected.emit(this.selectedData());
      this.announcer.announce(`Dodano: ${chip.name}`);
    }    
  }

  removeData(chip: T) {
    this.selectedData.update(chips => {
      const filteredData = chips.filter(c => c.id !== chip.id);
      this.announcer.announce(`Usunięto: ${chip.name}`);
      return filteredData;
    });
    this.selected.emit(this.selectedData());
    this.filteredData = this.getAvaiableData();
  }

  getSelectedDataIds(): string[] {
    return this.selectedData().map(chip => chip.id!);
  }

  getAvaiableData(): T[] {
    const selectedIds = this.getSelectedDataIds();
    return this.allData.filter(chip => !selectedIds.includes(chip.id!));
  }
}

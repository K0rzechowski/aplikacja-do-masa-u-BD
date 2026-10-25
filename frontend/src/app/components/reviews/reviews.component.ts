import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { Company } from '../../models/company';
import { BranchOffice } from '../../models/branch-office';
import { Review } from '../../models/review';
import { CompanyService } from '../../services/company.service';
import { BranchOfficeService } from '../../services/branch-office.service';
import { ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-reviews',
  imports: [MatFormFieldModule, MatSelectModule, MatIconModule, DatePipe],
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css'
})
export class ReviewsComponent {
  company: Company | undefined;
  branchOffices: BranchOffice[] = [];
  selectedBranchId: string | null = null;
  reviews: Review[] = [];
  stars = [1, 2, 3, 4, 5];

  constructor(
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private branchOfficeService: BranchOfficeService,
    private reviewService: ReviewService
  ) {
    this.route.paramMap.subscribe(params => {
      const city = params.get('city') || '';
      const name = params.get('name') || '';

      this.companyService.getCompanyByNameAndCity(name, city).subscribe((company) => {
        this.company = company;
        if (this.company?.id) {
          this.branchOfficeService.getBranchOfficesByCompany(this.company.id).subscribe(data => {
            this.branchOffices = data;
            if (data.length > 0) {
              this.onBranchChange(data[0].id!);
            }
          });
        }
      });
    });
  }

  onBranchChange(branchId: string) {
    this.selectedBranchId = branchId;
    this.reviews = [];
    this.reviewService.getReviewsByBranchOffice(branchId).subscribe(data => {
      this.reviews = data;
    });
  }

  get averageRating(): number {
    if (this.reviews.length === 0) {
      return 0;
    }
    return this.reviews.reduce((sum, review) => sum + review.rating, 0) / this.reviews.length;
  }
}

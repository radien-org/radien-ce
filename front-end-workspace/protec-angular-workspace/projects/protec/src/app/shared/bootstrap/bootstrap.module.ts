import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  NgbAccordionModule,
  NgbAlertModule,
  NgbDatepickerModule,
  NgbDropdownModule,
  NgbModalModule,
  NgbNavModule,
  NgbPaginationModule,
  NgbProgressbarModule,
  NgbTabsetModule,
  NgbToastModule,
} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    NgbAlertModule,
    NgbModalModule,
    NgbTabsetModule,
    NgbProgressbarModule,
    NgbToastModule,
    NgbDatepickerModule,
    NgbAccordionModule,
    NgbPaginationModule,
    NgbDatepickerModule,
    NgbNavModule,
    NgbDropdownModule,
  ],
  exports: [
    NgbAlertModule,
    NgbModalModule,
    NgbTabsetModule,
    NgbProgressbarModule,
    NgbToastModule,
    NgbDatepickerModule,
    NgbAccordionModule,
    NgbPaginationModule,
    NgbDatepickerModule,
    NgbNavModule,
    NgbDropdownModule,
  ],
})
export class BootstrapModule {}

import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DataAcquisitionService } from '../../../shared/services/data-acquisition/data-acquisition.service';

@Component({
  selector: 'app-work-accident',
  templateUrl: './work-accident.component.html',
  styleUrls: ['./work-accident.component.scss']
})
export class WorkAccidentComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/details-intro'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/full-body'
        }
      ]
    }
  }

  options: any[];

  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService
  ) {
    this.options = [];
  }

  ngOnInit(): void {
    this.getEmploymentResponsibleOptions();
  }

  getEmploymentResponsibleOptions() {
    this.dataService.getEmploymentResponsibleOptions().then((data:any) => {
      if(data) {
        data.forEach((item:any) => {
          this.options.push(
              { name: item, code: item }
          );
        });
      }
    });
  }

}
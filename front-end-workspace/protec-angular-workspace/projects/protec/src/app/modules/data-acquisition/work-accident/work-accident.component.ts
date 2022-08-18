import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DataAcquisitionService } from '../../../shared/services/data-acquisition/data-acquisition.service';
import {LOCAL} from "../../../shared/services/storage/local.enum";

@Component({
  selector: 'app-work-accident',
  templateUrl: './work-accident.component.html',
  styleUrls: ['./work-accident.component.scss']
})
export class WorkAccidentComponent implements OnInit {

  initNavigation = [{
    label: this.translationService.instant('back'),
    link: '/data-acquisition/full-body'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [...this.initNavigation]
    }
  }

  options: any[];

  postCode: {
    value: String,
    error: String,
  }
  occupation: {
    value: String,
    error: String,
  }

  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService
  ) {
    this.options = [];
    this.postCode = {
      value: localStorage.getItem(LOCAL.OCCUPATION_POSTCODE) || '', error : ''
    }
    this.occupation = {
      value: localStorage.getItem(LOCAL.OCCUPATION) || '', error: ''
    }
  }

  verifyInput(): void {
    if (this.postCode.value && this.occupation.value) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('next'),
        link: '/data-acquisition/full-body'
      }]
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  saveInputs(): void {
    localStorage.setItem(LOCAL.OCCUPATION_POSTCODE, `${this.postCode.value}`)
    localStorage.setItem(LOCAL.OCCUPATION, `${this.occupation.value}`)
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
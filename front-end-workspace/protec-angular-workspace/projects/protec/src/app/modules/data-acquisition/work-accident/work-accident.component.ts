import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DataAcquisitionService } from '../../../shared/services/data-acquisition/data-acquisition.service';
import {LOCAL} from "../../../shared/services/storage/local.enum";
import {StorageService} from "../../../shared/services/storage/storage.service";

@Component({
  selector: 'app-work-accident',
  templateUrl: './work-accident.component.html',
  styleUrls: ['./work-accident.component.scss']
})
export class WorkAccidentComponent implements OnInit {

  initNavigation = [{
    label: this.translationService.instant('ZURÜCK'),
    link: '/data-acquisition/details-intro'
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
  employer: {
    value: String,
    error: String,
  }
  fileNumber: {
    value: String,
    error: String,
  }
  responsible: {
    value: String,
    error: String,
  }

  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService,
      private readonly storageService: StorageService,
  ) {
    this.options = [];
    try {
      this.employer = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).employer || '', error: ''
      }
      this.postCode = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).postCode || '', error : ''
      }
      this.occupation = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).occupation || '', error: ''
      }
      this.fileNumber = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).fileNumber || '', error: ''
      }
      this.responsible = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).responsible || '', error: ''
      }
    } catch (err) {
      this.employer = {value: '', error: ''};
      this.postCode = {value: '', error: ''};
      this.occupation = {value: '', error: ''};
      this.fileNumber = {value: '', error: ''};
      this.responsible = {value: '', error: ''};
    }

    this.verifyInput()
  }

  verifyInput(): void {
    console.log('saving...');
    if (this.postCode.value && this.occupation.value) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('WEITER'),
        link: '/data-acquisition/full-body'
      }]
      this.saveInputs();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  saveInputs(): void {
    this.storageService.setItem(LOCAL.WORK_ACCIDENT_FORM, {
      employer: `${this.employer.value}`,
      postCode: `${this.postCode.value}`, 
      occupation: `${this.occupation.value}`,
      fileNumber: `${this.fileNumber.value}`,
      responsible: `${this.responsible.value}`
    })
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
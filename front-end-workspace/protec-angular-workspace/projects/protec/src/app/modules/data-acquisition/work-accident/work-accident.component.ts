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
      private readonly dataService : DataAcquisitionService,
      private readonly storageService: StorageService,
  ) {
    this.options = [];
    try {
      this.postCode = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).postCode || '', error : ''
      }
      this.occupation = {
        value: this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM).occupation || '', error: ''
      }
    } catch (err) {
      this.postCode = {value: '', error: ''}
      this.occupation = {value: '', error: ''}
    }

    this.verifyInput()
  }

  verifyInput(): void {
    if (this.postCode.value && this.occupation.value) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('next'),
        link: '/data-acquisition/details-intro'
      }]
      this.saveInputs();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  saveInputs(): void {
    this.storageService.setItem(LOCAL.WORK_ACCIDENT_FORM, {postCode: `${this.postCode.value}`, occupation: `${this.occupation.value}`})
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
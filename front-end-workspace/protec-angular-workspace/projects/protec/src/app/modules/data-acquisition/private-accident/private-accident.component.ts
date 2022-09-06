import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-private-accident',
  templateUrl: './private-accident.component.html',
  styleUrls: ['./private-accident.component.scss']
})
export class PrivateAccidentComponent implements OnInit {

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

  accidentType: string = '';

  howHappen: {
    value: string,
    error: string
  } = { value : '', error: ''};
  professionalActivity: {
    value: string,
    error: string,
  } = { value : '', error: ''};
  employer: {
    value: string,
    error: string
  } = { value : '', error: ''};
  longerThanTwelveMonths: {
    value: boolean,
    error: string
  } = { value : false, error: ''};
  youDoBefore: {
    value: string,
    error: string
  } = { value : '', error: ''};
  previousEmployer: {
    value: string,
    error: string
  } = { value : '', error: ''};
  longerThanTwelveMonthsSecond: {
    value: boolean,
    error: string
  } = { value : false, error: ''};

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) {}

  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    const savedData = this.storageService.getItem(LOCAL.PRIVATE_ACCIDENT_FORM)?.data;
    console.log('debug (savedData):', savedData);
    if(savedData) {
      this.howHappen = savedData.howHappen;
      this.professionalActivity = savedData.professionalActivity;
      this.employer = savedData.employer;
      this.longerThanTwelveMonths = savedData.longerThanTwelveMonths;
      this.youDoBefore = savedData.youDoBefore;
      this.previousEmployer = savedData.previousEmployer;
      this.longerThanTwelveMonthsSecond = savedData.longerThanTwelveMonthsSecond;
    }
    this.verifyInput();
  }

  public showOptions(){
    this.longerThanTwelveMonths.value = false;
    this.verifyInput();
  }
  
  public hideOptions(){
    this.longerThanTwelveMonths.value = true;
    this.longerThanTwelveMonthsSecond.value = false;
    this.youDoBefore.value = '';
    this.previousEmployer.value = '';
    this.verifyInput();
  }

  addNextButton (): void {
    this.pageNav.navegation.navegations = [...this.initNavigation, {
      label: this.translationService.instant('WEITER'),
      link: '/data-acquisition/full-body'
    }]

    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('WEITER'),
        link: '/data-acquisition/full-body'
      }]
    } else if(this.accidentType === 'disease') {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('WEITER'),
        link: '/data-acquisition/illness-diagnostic'
      }]
    }
  }

  verifyInput(): void {
    if (this.howHappen.value && this.professionalActivity.value) {
      if (!this.longerThanTwelveMonths.value) {
        if (this.longerThanTwelveMonthsSecond.value) {
          this.pageNav.navegation.navegations = [...this.initNavigation]
          if (this.youDoBefore.value) {
            console.log('verifications ooooo 11111')
            this.addNextButton();
          }
        } else {
          this.addNextButton();
        }
      } else {
        this.addNextButton();
      }
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
    this.saveInputs();
  }

  saveInputs() {
    const data = {data: {
      howHappen: this.howHappen,
      professionalActivity: this.professionalActivity,
      employer: this.employer,
      longerThanTwelveMonths: this.longerThanTwelveMonths,
      youDoBefore: this.youDoBefore,
      previousEmployer: this.previousEmployer,
      longerThanTwelveMonthsSecond: this.longerThanTwelveMonthsSecond
    }};
    this.storageService.setItem(LOCAL.PRIVATE_ACCIDENT_FORM, data);
  }
}

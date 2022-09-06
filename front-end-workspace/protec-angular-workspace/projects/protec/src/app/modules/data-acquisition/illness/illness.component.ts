import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-illness',
  templateUrl: './illness.component.html',
  styleUrls: ['./illness.component.scss']
})
export class IllnessComponent implements OnInit {

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

  accidentUccur: any;
  professionalActivity: any;
  employer: any;
  professionalActivity12Months = false;
  previousJobOccupation: any;
  previousEmployer: any;
  moreThan12Months = false;

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService) { }

  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
    const savedData = this.storageService.getItem(LOCAL.ILLNESS_DETAIL_FORM)?.data;
    console.log('debug (savedData):', savedData);
    if(savedData) {
      this.accidentUccur = savedData.accidentUccur;
      this.professionalActivity = savedData.professionalActivity;
      this.employer = savedData.employer;
      this.professionalActivity12Months = savedData.professionalActivity12Months;
      this.previousJobOccupation = savedData.previousJobOccupation;
      this.previousEmployer = savedData.previousEmployer;
      this.moreThan12Months = savedData.moreThan12Months;
    }
    this.verifyInput();
  }

  verifyAccidentType() {
    this.pageNav.navegation.navegations.splice(1, 1);
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/illness-diagnostic'
        }
      );
    } else if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/illness-diagnostic'
        }
      );
    }
  }

  verifyInput(): void {
    console.log('saving...');
    if (this.accidentUccur && this.professionalActivity) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('WEITER'),
        link: '/data-acquisition/illness-diagnostic'
      }]
      this.save();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  public showOptions(){
    this.professionalActivity12Months = true;
    this.save();
  }

  public hideOptions(){
    this.professionalActivity12Months = false;
    this.save();
  }

  choiceRadioOption(value:boolean) {
    this.moreThan12Months = value;
    this.save();
  }

  save() {
    const data = {data: {
      accidentUccur: this.accidentUccur,
      professionalActivity: this.professionalActivity,
      employer: this.employer,
      professionalActivity12Months: this.professionalActivity12Months,
      previousJobOccupation: this.previousJobOccupation,
      previousEmployer: this.previousEmployer,
      moreThan12Months: this.moreThan12Months
    }};
    console.log('debug (save):', data);
    this.storageService.setItem(LOCAL.ILLNESS_DETAIL_FORM, data);
  }
}

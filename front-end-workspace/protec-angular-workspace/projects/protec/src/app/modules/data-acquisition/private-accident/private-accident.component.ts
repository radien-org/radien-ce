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

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/details-intro'
        }
      ]
    }
  }

  accidentType: string = '';

  howHappen: any;
  professionalActivity: any;
  employer: any;
  longerThanTwelveMonths: any;
  youDoBefore: any;
  previousEmployer: any;
  longerThanTwelveMonthsSecond: any;

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
    this.verifyAccidentType();
  }

  public showOptions(){
    this.longerThanTwelveMonths = false;
    this.save();
  }
  
  public hideOptions(){
    this.longerThanTwelveMonths = true;
    this.save();
  }

  verifyAccidentType() {
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/full-body'
        }
      );
    } else if(this.accidentType === 'disease') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/illness-diagnostic'
        }
      );
    }
  }

  save() {
    const data = {data: {
      howHappen: this.howHappen,
      professionalActivity: this.professionalActivity,
      employer: this.employer,
      longerThanTwelveMonths: this.longerThanTwelveMonths,
      youDoBefore: this.youDoBefore,
      previousEmployer: this.previousEmployer,
      longerThanTwelveMonthsSecond: this.longerThanTwelveMonthsSecond
    }};
    console.log('debug (save):', data);
    this.storageService.setItem(LOCAL.PRIVATE_ACCIDENT_FORM, data);
  }
}

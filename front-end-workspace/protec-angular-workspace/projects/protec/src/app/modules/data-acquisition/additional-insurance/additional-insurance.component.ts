import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PrimeNGConfig } from "primeng/api";
import { CookieService } from '../../../shared/services/cookie/cookie.service';
import { DataAcquisitionService } from '../../../shared/services/data-acquisition/data-acquisition.service';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-additional-insurance',
  templateUrl: './additional-insurance.component.html',
  styleUrls: ['./additional-insurance.component.scss']
})
export class AdditionalInsuranceComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('Zurück'),
          link: '/data-acquisition/accident-type'
        }
      ]
    }
  }

  noButton = {
    label: this.translationService.instant('NEIN'),
    type: 'outline',
    link: 'function',
    funcParams: {
      var: 'firstStatus',
      value: 'no'
    },
    selected: true
  }

  secondOptionButtons = [
    {
      label: this.translationService.instant('JA'),
      type: 'outline',
      link: 'function',
      funcParams: {
        var: 'secondStatus',
        value: 'yes'
      },
      selected: false
    },
    {
      label: this.translationService.instant('NEIN'),
      type: 'outline',
      link: 'function',
      funcParams: {
        var: 'secondStatus',
        value: 'no'
      },
      selected: true
    }
  ]

  items = [];
  item: string = '';
  selectedOptions: string[] = [];
  options: any[];

  buttonsStatus = {
    firstStatus: 'no',
    secondStatus: 'no'
  }

  accidentType: string = '';

  secondButtons: boolean = false;

  constructor(
    private readonly translationService: TranslateService, 
    private primengConfig: PrimeNGConfig, 
    private readonly dataService : DataAcquisitionService,
    private readonly storageService: StorageService
  ) {
    this.options = [];
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;
    this.getAdditionalInsuranceOptions();
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
  }

  verifyAccidentType() {
    this.pageNav.navegation.navegations.splice(1, 1);
    if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition' + (this.buttonsStatus.firstStatus == 'no' ? '/we-are-sorry' : '/accident-date')
        }
      );
    } else if(this.accidentType === 'disease') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition' + (this.buttonsStatus.firstStatus == 'no' ? '/we-are-sorry' : '/accident-date')
        }
      );
    } else {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/accident-date'
        }
      );
    }
  }

  selectNoBtn(data: any, selectedItems: any = '') {
    console.log(data);
    console.log(selectedItems);
    if(data){
      if(data.var === 'firstStatus'){
        this.buttonsStatus.firstStatus = data.value;
        if(selectedItems.length > 0){
          this.noButton.selected = true;
          this.secondButtons = true;
        } else {
          this.noButton.selected = false;
          this.secondButtons = false;
        }
      }
      if(data.var === 'secondStatus'){
        this.buttonsStatus.secondStatus = data.value;
        this.secondOptionButtons.forEach(item => {
          item.selected = item.funcParams.value === data.value ? true : false;
        });
      }
      this.verifyAccidentType();
    }
  }

  getAdditionalInsuranceOptions() {
    this.dataService.getAdditionalInsuranceOptions().then((data:any) => {
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

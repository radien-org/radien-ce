import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PrimeNGConfig } from "primeng/api";
import { CookieService } from '../../../shared/services/cookie/cookie.service';
import { DataAcquisitionService } from '../../../shared/services/data-acquisition/data-acquisition.service';

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
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-type'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/accident-date'
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

  constructor(private readonly translationService: TranslateService, private primengConfig: PrimeNGConfig, private readonly dataService : DataAcquisitionService) {
    this.options = [];
  }

  ngOnInit(): void {
    this.primengConfig.ripple = true;
    this.getAdditionalInsuranceOptions();
  }

  selectNoBtn(data: any) {
    if(data){
      if(data.var === 'firstStatus'){
        this.buttonsStatus.firstStatus = data.value;
        this.noButton.selected = data.value === 'yes' ? false : true;
      }
      if(data.var === 'secondStatus'){
        this.buttonsStatus.secondStatus = data.value;
        this.secondOptionButtons.forEach(item => {
          item.selected = item.funcParams.value === data.value ? true : false;
        });
      }
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

import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Component({
  selector: 'app-more-injuries',
  templateUrl: './more-injuries.component.html',
  styleUrls: ['./more-injuries.component.scss']
})
export class MoreInjuriesComponent implements OnInit {

  buttons = [{
    label: this.translationService.instant('JA'),
    type: 'outline',
    link: 'function',
    funcParams: {
      choice: 'yes'
    }
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'outline',
    link: 'function',
    funcParams: {
      choice: 'no'
    }
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/part-body'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService, private readonly router: Router) { }

  ngOnInit(): void {
    this.save();
  }
  
  getRandomArbitrary(min:number, max:number) {
    return Math.random() * (max - min) + min;
  }

  buildTitle(type:string) {
    return type;
  }

  save() {
    let CLAIMS_LIST = this.storageService.getItem(LOCAL.CLAIMS_LIST);
    const ACCIDENT_TYPE = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    const ADDITIONAL_INSURANCE_FORM = this.storageService.getItem(LOCAL.ADDITIONAL_INSURANCE_FORM);
    const ADDITIONAL_INSURANCE_FORM_SECOND = this.storageService.getItem(LOCAL.ADDITIONAL_INSURANCE_FORM_SECOND);
    const PRIVATE_ACCIDENT_FORM = this.storageService.getItem(LOCAL.PRIVATE_ACCIDENT_FORM);
    const ILLNESS_DIAGNOSTIC_FORM = this.storageService.getItem(LOCAL.ILLNESS_DIAGNOSTIC_FORM);
    const WORK_ACCIDENT_FORM = this.storageService.getItem(LOCAL.WORK_ACCIDENT_FORM);
    const ACCIDENT_DATE_FORM = this.storageService.getItem(LOCAL.ACCIDENT_DATE_FORM);
    const PERSONAL_DATA_PERSON = this.storageService.getItem(LOCAL.PERSONAL_DATA_PERSON);
    const PERSONAL_DATA_CONTACT = this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT);

    console.log('debug (CLAIMS_LIST):', CLAIMS_LIST);
    console.log('debug (ADDITIONAL_INSURANCE_FORM):', ADDITIONAL_INSURANCE_FORM);
    console.log('debug (ADDITIONAL_INSURANCE_FORM_SECOND):', ADDITIONAL_INSURANCE_FORM_SECOND);
    console.log('debug (PRIVATE_ACCIDENT_FORM):', PRIVATE_ACCIDENT_FORM);
    console.log('debug (ILLNESS_DIAGNOSTIC_FORM):', ILLNESS_DIAGNOSTIC_FORM);
    console.log('debug (WORK_ACCIDENT_FORM):', WORK_ACCIDENT_FORM);
    console.log('debug (ACCIDENT_DATE_FORM):', ACCIDENT_DATE_FORM);
    console.log('debug (PERSONAL_DATA_PERSON):', PERSONAL_DATA_PERSON);
    console.log('debug (PERSONAL_DATA_CONTACT):', PERSONAL_DATA_CONTACT);

    const obj = {
      id: this.getRandomArbitrary(100000, 1000000),
      title: this.buildTitle(ACCIDENT_TYPE),
      mainData: [
        {
          id: 'ADDITIONAL_INSURANCE_FORM',
          label: 'Additional Insurance',
          value: ADDITIONAL_INSURANCE_FORM
        },
        {
          id: 'ADDITIONAL_INSURANCE_FORM_SECOND',
          label: 'Additional Insurance Second',
          value: ADDITIONAL_INSURANCE_FORM_SECOND
        },
        {
          id: 'PRIVATE_ACCIDENT_FORM',
          label: 'Private Accident',
          value: PRIVATE_ACCIDENT_FORM
        },
        {
          id: 'ILLNESS_DIAGNOSTIC_FORM',
          label: 'Illness Diagnostic',
          value: ILLNESS_DIAGNOSTIC_FORM
        },
        {
          id: 'WORK_ACCIDENT_FORM',
          label: 'Work Accident',
          value: WORK_ACCIDENT_FORM
        },
        {
          id: 'ACCIDENT_DATE_FORM',
          label: 'Accident Date',
          value: ACCIDENT_DATE_FORM
        },
        {
          id: 'PERSONAL_DATA_PERSON',
          label: 'Personal Data Person',
          value: PERSONAL_DATA_PERSON
        },
        {
          id: 'PERSONAL_DATA_CONTACT',
          label: 'Personal Data Contact',
          value: PERSONAL_DATA_CONTACT
        }
      ]
    }

    console.log('debug (obj):', obj);

    CLAIMS_LIST.push(obj);

    this.storageService.setItem(LOCAL.CLAIMS_LIST, CLAIMS_LIST);

  }

  selectButton(data: any) {
    console.log('debug (selectButton):', data);
    if(data.choice == 'yes'){
      this.router.navigate(['/data-acquisition/full-body']);
    } else {
      this.router.navigate(['/data-acquisition/summary']);
    }
  }

}

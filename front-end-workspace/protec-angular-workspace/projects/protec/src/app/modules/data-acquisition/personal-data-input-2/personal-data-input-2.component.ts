import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {DataAcquisitionService} from "../../../shared/services/data-acquisition/data-acquisition.service";
import {StorageService} from "../../../shared/services/storage/storage.service";
import {LOCAL} from "../../../shared/services/storage/local.enum";

@Component({
  selector: 'app-illness',
  templateUrl: './personal-data-input-2.component.html',//TODO change component name to personal-data-contact
  styleUrls: ['./personal-data-input-2.component.scss']
})
export class PersonalDataInputComponentTwo implements OnInit {

  initNavigation = [{
    label: this.translationService.instant('back'),
    link: '/data-acquisition/personal-data-person'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [...this.initNavigation]
    }
  }


  street: {
    value: String,
    error: String,
  }
  postCode: {
    value: String,
    error: String,
  }
  mobileNumber: {
    value: String,
    error: String
  }
  addressSupplement: {
    value: String,
    error: String
  }
  city: {
    value: String,
    error: String
  }
  country: {
    value: String,
    error: String
  }
  email: {
    value: String,
    error: String
  }

  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService,
      private readonly storageService: StorageService,
  ) {
    try {
      this.postCode = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).postCode || '', error : ''
      }
      this.street = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).street || '', error: ''
      }
      this.mobileNumber = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).mobileNumber || '', error: ''
      }
      this.addressSupplement = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).addressSupplement || '', error: ''
      }
      this.city = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).city || '', error: ''
      }
      this.country = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).country || '', error: ''
      }
      this.email = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_CONTACT).email || '', error: ''
      }
    } catch (err) {
      this.postCode = {value: '', error: ''};
      this.street = {value: '', error: ''};
      this.mobileNumber = {value: '', error: ''};
      this.addressSupplement = {value: '', error: ''};
      this.city = {value: '', error: ''};
      this.country = {value: '', error: ''};
      this.email = {value: '', error: ''};
    }

    this.verifyInput()
  }

  ngOnInit(): void {}

  verifyInput(): void {
    if (this.postCode.value && this.street.value && this.mobileNumber.value) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('next'),
        link: '/data-acquisition/appreciation'
      }]
      this.saveInputs();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  saveInputs(): void {
    this.storageService.setItem(LOCAL.PERSONAL_DATA_CONTACT, {
      postCode: `${this.postCode.value}`, 
      street: `${this.street.value}`, 
      mobileNumber: `${this.mobileNumber.value}`,
      addressSupplement: `${this.addressSupplement.value}`,
      city: `${this.city.value}`,
      country: `${this.city.value}`,
      email: `${this.city.value}`
    })
  }
}

import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {DataAcquisitionService} from "../../../shared/services/data-acquisition/data-acquisition.service";
import {StorageService} from "../../../shared/services/storage/storage.service";
import {LOCAL} from "../../../shared/services/storage/local.enum";

@Component({
  selector: 'app-illness',
  templateUrl: './personal-data-input.component.html',//TODO change component name to personal-data-person
  styleUrls: ['./personal-data-input.component.scss']
})
export class PersonalDataInputComponent implements OnInit {
  initNavigation = [{
    label: this.translationService.instant('back'),
    link: '/data-acquisition/personal-data-information'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [...this.initNavigation]
    }
  }

  firstName : {
    value: String,
    error: String
  }

  lastName: {
    value: String,
    error: String
  }

  birthDate: {
    value: String,
    error: String
  }


  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService,
      private readonly storageService: StorageService,
  ) {
    try {
      this.firstName = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_PERSON).firstName || '', error: ''
      }
      this.lastName = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_PERSON).lastName || '', error: ''
      }
      this.birthDate = {
        value: this.storageService.getItem(LOCAL.PERSONAL_DATA_PERSON).birthDate || '', error: ''
      }
    } catch (err) {
      this.firstName = {value: '', error: ''}
      this.lastName = {value: '', error: ''}
      this.birthDate = {value: '', error: ''}
    }


    this.verifyInput()
  }

  verifyInput(): void {
    if (this.lastName.value && this.firstName.value && this.birthDate.value) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('next'),
        link: '/data-acquisition/personal-data-contact'
      }]
      this.saveInputs();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }
  saveInputs(): void {
    this.storageService.setItem(LOCAL.PERSONAL_DATA_PERSON, {birthDate: `${this.birthDate.value}`, firstName:`${this.firstName.value}`, lastName: `${this.lastName.value}` })
  }


  setDate (value: String): void {
    this.birthDate.value = value;
    this.verifyInput()
  }


  ngOnInit(): void {
  }

  option_class = 'hide';
  public showOptions(){
    this.option_class = 'show-flex'
  }
  public hideOptions(){
    this.option_class = 'hide'
  }
}

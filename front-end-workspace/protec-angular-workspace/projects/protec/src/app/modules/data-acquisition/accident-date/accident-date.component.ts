import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {DataAcquisitionService} from "../../../shared/services/data-acquisition/data-acquisition.service";
import {StorageService} from "../../../shared/services/storage/storage.service";
import {LOCAL} from "../../../shared/services/storage/local.enum";


interface IChoice {
  label: String,
  type: String,
  link: String,
  value: String,
  active: Boolean
}

@Component({
  selector: 'app-accident-date',
  templateUrl: './accident-date.component.html',
  styleUrls: ['./accident-date.component.scss']
})
export class AccidentDateComponent implements OnInit {

  initNavigation = [{
    label: this.translationService.instant('back'),
    link: '/data-acquisition/additional-insurance'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [...this.initNavigation]
    }
  }

  buttons = [{
    label: this.translationService.instant('JA'),
    type: 'item-selectable-yes',
    link: 'function',
    value: 'yes',
    active: false
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'item-selectable-no',
    link: 'function',
    value: 'no',
    active: false
  }]


  accidentDate: {
    value: String,
    error: String,
  }
  accidentIsOnSickLeave: {
    value: String,
    error: String,
  }

  setAccidentIsOnSickLeave(choice: IChoice): void {
    this.buttons.forEach(btn => {
      if (btn.active && btn !== choice) {
        btn.active = false
      }
    })
    choice.active = !choice.active;
    if (choice.active) {
      this.accidentIsOnSickLeave.value = choice.value
    } else {
      this.accidentIsOnSickLeave.value = ''
    }
    this.verifyInput()
  }

  constructor(
      private readonly translationService: TranslateService,
      private readonly dataService : DataAcquisitionService,
      private readonly storageService: StorageService,
  ) {
    try {
      this.accidentDate = {
        value: this.storageService.getItem(LOCAL.ACCIDENT_DATE_FORM).date || '', error : ''
      }
      this.accidentIsOnSickLeave = {
        value: this.storageService.getItem(LOCAL.ACCIDENT_DATE_FORM).isOnSickLeave || '', error: ''
      }
    } catch (err) {
      this.accidentDate = {value: '', error: ''}
      this.accidentIsOnSickLeave = {value: '', error: ''}
    }

    if (this.accidentIsOnSickLeave.value == "yes") {
      this.buttons[0].active = true
    }
    if (this.accidentIsOnSickLeave.value == 'no')  {
      this.buttons[1].active = true
    }
    this.verifyInput()
  }

  verifyInput(): void {
    if (this.accidentDate.value && this.accidentIsOnSickLeave.value) {
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
    this.storageService.setItem(LOCAL.ACCIDENT_DATE_FORM, {date: `${this.accidentDate.value}`, isOnSickLeave:`${this.accidentIsOnSickLeave.value}` })
  }

  setDate (value: String): void {
    this.accidentDate.value = value;
    this.verifyInput()
  }


  ngOnInit(): void {}

}

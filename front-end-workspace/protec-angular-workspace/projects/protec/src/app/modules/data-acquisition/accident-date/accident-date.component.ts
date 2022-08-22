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
    value: 'false',
    active: false
  }]

  months = 0
  days = 0
  monthdays = 0
  today = new Date();
  currentYear = this.today.getFullYear();
  currentMonth = this.today.getMonth();
  currentDay = this.today.getDate();
  monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
  selectedYear =1;
  selectedMonth=1;
  selectedDay=1;
  daysInMonth = new Date(this.selectedYear, this.selectedMonth, 0).getDate();
  year_selector_class = "show";
  month_selector_class = "hide";
  day_selector_class = "hide";
  year_head_class = "";
  month_head_class = "";
  day_head_class = "";
  year_head_label = this.translationService.instant('accident-date.JAHR *');
  month_head_label= this.translationService.instant('accident-date.MONATE');
  day_head_label= this.translationService.instant('accident-date.TAG');

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
    this.accidentDate = {
      value: this.storageService.getItem(LOCAL.ACCIDENT_DATE) || '', error : ''
    }
    this.accidentIsOnSickLeave = {
      value: this.storageService.getItem(LOCAL.ACCIDENT_ON_SICK_LEAVE) || '', error: ''
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
    this.storageService.setItem(LOCAL.ACCIDENT_DATE, `${this.accidentDate.value}`)
    this.storageService.setItem(LOCAL.ACCIDENT_ON_SICK_LEAVE, `${this.accidentIsOnSickLeave.value}`)
  }

  counter(i: number) {
    return new Array(i);
  }

  selectYear(i: number){
    this.selectedYear = i;
    this.year_selector_class = "hide";
    if(i==this.currentYear){
      this.months=this.currentMonth;
    }else{
      this.months=11;
    }
    this.year_head_class="toggle-button-completed"
    this.month_selector_class = "show";
    this.year_head_label= i.toString();
    this.setAccidentDate()
  }
  selectMonth(i: number){
    this.selectedMonth = i;
    this.month_selector_class = "hide";
    this.day_selector_class = "show";
    this.month_head_class = "toggle-button-completed"
    this.month_head_label= this.monthNames[i];
    this.setAccidentDate()
  }

  getDaylist(){
    switch (this.selectedMonth){
      case 0:this.monthdays = 31;break;
      case 1:if(this.selectedYear%4==0){this.monthdays = 29} else {this.monthdays = 28}break;
      case 2:this.monthdays = 31;break;
      case 3:this.monthdays = 30;break;
      case 4:this.monthdays = 31;break;
      case 5:this.monthdays = 30;break;
      case 6:this.monthdays = 31;break;
      case 7:this.monthdays = 31;break;
      case 8:this.monthdays = 30;break;
      case 9:this.monthdays = 31;break;
      case 10:this.monthdays = 30;break;
      case 11:this.monthdays = 31;break;
    }
    if(this.selectedYear == this.currentYear){
      if(this.selectedMonth == this.currentMonth) {this.monthdays = this.currentDay;}
    }
    return this.monthdays;
  }

  selectDay(i:number){
    this.selectedDay=i;
    this.day_selector_class = "hide";
    this.day_head_class = "toggle-button-completed";
    this.day_head_label= i.toString();
    this.setAccidentDate()
  }

  setAccidentDate(): void {
    if (this.day_head_class == "toggle-button-completed" && this.month_head_class == "toggle-button-completed" && this.year_head_class == "toggle-button-completed") {
      this.accidentDate.value = `${this.selectedYear}.${this.selectedMonth}.${this.selectedDay}`
      this.verifyInput()
    } else {
      this.accidentDate.value = ''
    }
  }

  enableSelectYear()
  {
    if(this.year_selector_class == 'hide') {
      this.year_selector_class = 'show';
      this.year_head_class = "";
      this.month_head_class = "";
      this.day_head_class = "";
      this.year_head_label = "JAHR *";
      this.month_head_label="MONATE";
      this.day_head_label="TAG";
    }
  }

  enableSelectMonth(){
    if(this.month_selector_class == 'hide') {
      this.month_selector_class = 'show';
      this.month_head_class = "";
      this.day_head_class = "";
      this.month_head_label="MONATE";
      this.day_head_label="TAG";
    }
  }

  enableSelectDay(){
    if(this.day_selector_class == 'hide') {
      this.day_selector_class = 'show';
      this.day_head_class = "";
      this.day_head_label="TAG";
    }
  }

  ngOnInit(): void {}

}

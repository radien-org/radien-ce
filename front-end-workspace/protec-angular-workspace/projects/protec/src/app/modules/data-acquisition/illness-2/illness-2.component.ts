import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';
import { Router } from '@angular/router';

@Component({
  selector: 'app-illness-2',//TODO: change this name component probably to illness-diagnostic
  templateUrl: './illness-2.component.html',
  styleUrls: ['./illness-2.component.scss']
})
export class Illness2Component implements OnInit {

  initNavigation = [{
    label: this.translationService.instant('ZURÜCK'),
    link: '/data-acquisition/illness-details',
    funcParams: {}
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [...this.initNavigation]
    }
  }
  
  buttons = [{
    label: this.translationService.instant('JA'),
    type: 'item',
    link: '/data-acquisition/accident-date'
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'item',
    link: '/data-acquisition/accident-type'
  }]
  
  months = 0
  days = 0
  monthdays = 0
  today = new Date();
  currentYear = this.today.getFullYear();
  currentMonth = this.today.getMonth();
  currentDay = this.today.getDate();
  monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
  selectedBeginningYear =0;
  selectedBeginningMonth=0;
  selectedBeginningDay=0;
  selectedEndYear =0;
  selectedEndMonth=0;
  selectedEndDay=0;
  daysInMonth = new Date(this.selectedBeginningYear, this.selectedBeginningMonth, 0).getDate();
  year_selector_class_Beginning = "show"
  month_selector_class_Beginning = "hide"
  day_selector_class_Beginning = "hide"
  year_head_class_Beginning = ""
  month_head_class_Beginning = ""
  day_head_class_Beginning = ""
  year_selector_class_end = "hide"
  month_selector_class_end = "hide"
  day_selector_class_end = "hide"
  year_head_class_end = ""
  month_head_class_end = ""
  day_head_class_end = ""
  year_head_label_Beginning = "JAHR *"
  month_head_label_Beginning="MONATE"
  day_head_label_Beginning="TAG"
  year_head_label_end = "JAHR *"
  month_head_label_end="MONATE"
  day_head_label_end="TAG"
  current_selector = "Beginning"
  start_end_month=0
  start_end_day=0
  end_date="show"
  
  
  counter(i: number) {
    return new Array(i);
  }

  whatWasDiagnosed: any;
  currentlySickLeave: any;

  accidentType: string = '';

  constructor(private readonly translationService: TranslateService, private readonly storageService: StorageService, private readonly router: Router) { }
  
  ngOnInit(): void {
    this.accidentType = this.storageService.getItem(LOCAL.ACCIDENT_TYPE);
    this.verifyAccidentType();
    const savedData = this.storageService.getItem(LOCAL.ILLNESS_DIAGNOSTIC_FORM)?.data;
    console.log('debug (savedData):', savedData);
    if(savedData) {
      this.whatWasDiagnosed = savedData.whatWasDiagnosed;
      this.currentlySickLeave = savedData.currentlySickLeave;
    }
    this.verifyInput();
  }

  verifyAccidentType() {
    if(this.accidentType === 'work-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/full-body',
          funcParams: {}
        }
      );
    } else if(this.accidentType === 'recreational-accident') {
      this.pageNav.navegation.navegations.push(
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/full-body',
          funcParams: {}
        }
      );
    }
  }

  verifyInput(): void {
    console.log('saving...');
    if (this.whatWasDiagnosed) {
      this.pageNav.navegation.navegations = [...this.initNavigation, {
        label: this.translationService.instant('WEITER'),
        link: '/data-acquisition/summary',
        funcParams: {}
      }]
      this.save();
    } else {
      this.pageNav.navegation.navegations = [...this.initNavigation]
    }
  }

  public selectYear(i: number){
    if(this.current_selector == "Beginning"){
      this.selectedBeginningYear = i;
      this.year_selector_class_Beginning = "hide";
      if(i==this.currentYear){
        this.months=this.currentMonth;
      }else{
        this.months=11;
      }
      this.year_head_class_Beginning="toggle-button-completed"
      this.month_selector_class_Beginning = "show";
      this.year_head_label_Beginning= i.toString();
    }
    else if(this.current_selector == "End"){
      this.selectedEndYear = i;
      this.year_selector_class_end = "hide";
      if(this.selectedEndYear==this.currentYear){
        this.months=this.currentMonth;
      }
      else{
        this.months=11;
      }
      if(this.selectedEndYear == this.selectedBeginningYear)
      {
        this.start_end_month = this.selectedBeginningMonth;
      }
      this.year_head_class_end="toggle-button-completed"
      this.month_selector_class_end = "show";
      this.year_head_label_end= i.toString();
    }
  }
  
  public selectMonth(i: number){
    if(this.current_selector == "Beginning"){
      this.selectedBeginningMonth = i;
      this.month_selector_class_Beginning = "hide";
      this.day_selector_class_Beginning = "show";
      this.month_head_class_Beginning = "toggle-button-completed"
      this.month_head_label_Beginning= this.monthNames[i];
    }
    else if(this.current_selector == "End"){
      this.selectedEndMonth = i;
      this.month_selector_class_end = "hide";
      this.day_selector_class_end = "show";
      this.month_head_class_end = "toggle-button-completed"
      this.month_head_label_end= this.monthNames[i];
    }
  }

  public getDaylist(){
    switch (this.selectedBeginningMonth){
      case 0: this.monthdays = 31; break;
      case 1: if(this.selectedBeginningYear%4==0){this.monthdays = 29} else {this.monthdays = 28} break;
      case 2: this.monthdays = 31; break;
      case 3: this.monthdays = 30; break;
      case 4: this.monthdays = 31; break;
      case 5: this.monthdays = 30; break;
      case 6: this.monthdays = 31; break;
      case 7: this.monthdays = 31; break;
      case 8: this.monthdays = 30; break;
      case 9: this.monthdays = 31; break;
      case 10: this.monthdays = 30; break;
      case 11: this.monthdays = 31; break;
    }
    if(this.selectedBeginningYear == this.currentYear){
      if(this.selectedBeginningMonth == this.currentMonth)
      {
        this.monthdays = this.currentDay;
      }
    }
    if(this.current_selector == "End"){
      if(this.selectedEndYear == this.selectedBeginningYear)
      {
        if(this.selectedEndMonth == this.selectedBeginningMonth)
        {
          this.start_end_day = this.selectedBeginningDay;
        }
      }
    }
    
    return this.monthdays;
  }
  
  public selectDay(i:number){
    
    if(this.current_selector == "Beginning"){
      this.selectedBeginningDay=i;
      this.day_selector_class_Beginning = "hide";
      this.day_head_class_Beginning = "toggle-button-completed";
      this.day_head_label_Beginning= i.toString();
      this.current_selector = "End";
      this.year_selector_class_end = "show";
    }
    else if(this.current_selector == "End"){
      this.selectedBeginningDay=i;
      this.day_selector_class_end = "hide";
      this.day_head_class_end = "toggle-button-completed";
      this.day_head_label_end= i.toString();
      this.current_selector = "End";
      this.year_selector_class_end = "hide";
    }
    
  }
  
  public showOptions(){
    this.end_date = 'show-flex';
    this.save();
  }

  public hideOptions(){
    this.currentlySickLeave = !this.currentlySickLeave;
    this.save();
  }

  save() {
    const data = {data: {
      whatWasDiagnosed: this.whatWasDiagnosed,
      currentlySickLeave: this.currentlySickLeave
    }};
    console.log('debug (save):', data);
    this.storageService.setItem(LOCAL.ILLNESS_DIAGNOSTIC_FORM, data);
  }

  saveAll(data:any) {
    this.storageService.saveDataAcquisition();
    this.router.navigate(['/data-acquisition/summary']);
  }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Router } from '@angular/router';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {

  @Output() dateSet: EventEmitter<String> = new EventEmitter<String>();
  @Input() public initialDate : String | undefined

  date: {
    value: String,
    error: String
  }

  setDate(): void {
    if (this.day_head_class == "toggle-button-completed" && this.month_head_class == "toggle-button-completed" && this.year_head_class == "toggle-button-completed") {
      this.date.value = `${this.selectedYear}.${this.selectedMonth}.${this.selectedDay}`
    } else {
      this.date.value = ''
    }
    this.dateSet.emit(this.date.value);
  }

  constructor(private translate: TranslateService) {
    this.translate.get('accident-date').subscribe((data:any) => {
      console.log(data)
    })
    this.date = {
      value: '', error: ""
    }
  }


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
  year_selector_class = "show"
  month_selector_class = "hide"
  day_selector_class = "hide"
  year_head_class = ""
  month_head_class = ""
  day_head_class = ""
  year_head_label = "JAHR *"
  month_head_label= "MONAT"
  day_head_label= "TAG"




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
    this.setDate()
  }
  selectMonth(i: number){
    this.selectedMonth = i;
    this.month_selector_class = "hide";
    this.day_selector_class = "show";
    this.month_head_class = "toggle-button-completed"
    this.month_head_label= this.monthNames[i];
    this.setDate()
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
    this.setDate()
  }

  enableSelectYear()
  {
    if(this.year_selector_class == 'hide') {
      this.year_selector_class = 'show';
      this.year_head_class = "";
      this.month_head_class = "";
      this.day_head_class = "";
      this.year_head_label = "JAHR *"
      this.month_head_label="MONAT"
      this.day_head_label="TAG"
      this.selectedYear = 1;
      this.setDate()
    }
  }

  enableSelectMonth(){
    if(this.month_selector_class == 'hide') {
      this.month_selector_class = 'show';
      this.month_head_class = "";
      this.day_head_class = "";
      this.month_head_label="MONAT"
      this.day_head_label="TAG"
      this.setDate()
    }
  }

  enableSelectDay(){
    if(this.day_selector_class == 'hide') {
      this.day_selector_class = 'show';
      this.day_head_class = "";
      this.day_head_label="TAG"
      this.setDate()
    }
  }
  // Required info is stored in var:
  // selectedYear
  // selectedMonth
  // selectedDay

  public ngOnInit(): void {
    if (this.initialDate) {
      let dateList: String [ ] = this.initialDate.split('.')
      this.selectYear(+dateList[0]);
      this.selectMonth(+dateList[1]);
      this.selectDay(+dateList[2])
    }
  }

}

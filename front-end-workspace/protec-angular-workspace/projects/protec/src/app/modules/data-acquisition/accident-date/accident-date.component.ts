import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-accident-date',
  templateUrl: './accident-date.component.html',
  styleUrls: ['./accident-date.component.scss']
})
export class AccidentDateComponent implements OnInit {

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
          link: '/data-acquisition/die-details'
        }
      ]
    }
  }

  buttons = [{
    label: this.translationService.instant('JA'),
    type: 'outline',
    link: 'disabled'
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'outline',
    link: 'disabled'
  }]

  dtYear: Date | undefined;

  dtMonth: Date | undefined;

  dtDay: Date | undefined;

  dates: Date[] | undefined;

  rangeDates: Date[] | undefined;

  minDate: any;

  maxDate: any;

  invalidDates: Array<Date> | undefined;

  constructor(private readonly translationService: TranslateService) {}

  ngOnInit(): void {
    let today = new Date();
    let month = today.getMonth();
    let year = today.getFullYear();
    let prevMonth = (month === 0) ? 11 : month -1;
    let prevYear = (prevMonth === 11) ? year - 1 : year;
    let nextMonth = (month === 11) ? 0 : month + 1;
    let nextYear = (nextMonth === 0) ? year + 1 : year;
    this.minDate = new Date();
    this.minDate.setMonth(prevMonth);
    this.minDate.setFullYear(prevYear);
    this.maxDate = new Date();
    this.maxDate.setMonth(nextMonth);
    this.maxDate.setFullYear(nextYear);

    let invalidDate = new Date();
    invalidDate.setDate(today.getDate() - 1);
    this.invalidDates = [today,invalidDate];
  }

}

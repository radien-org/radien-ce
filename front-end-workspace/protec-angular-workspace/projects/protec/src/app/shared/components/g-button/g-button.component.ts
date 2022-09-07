import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import {global} from "@angular/compiler/src/util";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-g-button',
  templateUrl: './g-button.component.html',
  styleUrls: ['./g-button.component.scss']
})
export class GButtonComponent implements OnInit {

  @Input() public data: any = {
    label: this.translationService.instant('no-data' ),
    type: 'default',
    link: '/',
    active: false,
    navegations: [],
    status: null,
    step: -1,
  };
  @Output() public linkFunction = new EventEmitter();

  constructor(private readonly translationService: TranslateService, private readonly router: Router) { }

  ngOnInit(): void {
    let stepsMap  = {
      '/data-acquisition': 1,
      '/data-acquisition/accident-intro': 1,
      '/data-acquisition/accident-type': 1,
      '/data-acquisition/additional-insurance': 1,
      '/data-acquisition/accident-date': 1,
      '/data-acquisition/details-intro': 2,
      '/data-acquisition/illness-details': 2,
      '/data-acquisition/full-body': 2,
      '/data-acquisition/work-accident': 2,
      '/data-acquisition/part-body': 2,
      '/data-acquisition/more-injuries': 2,
      '/data-acquisition/summary': 2,
      '/data-acquisition/prospects-of-success': 3,
      '/data-acquisition/chance-of-success': 3,
      '/data-acquisition/request-quote': 3,
      '/data-acquisition/your-data': 4,
      '/data-acquisition/personal-data-person': 4,
      '/data-acquisition/personal-data-contact': 4,
      '/data-acquisition/appreciation': 5,
      '/data-acquisition/pre-summary': 5,
      '/data-acquisition/illness-diagnostic': 2
    }
    if (this.data.step > 0) {
      // @ts-ignore
      const routeStep = stepsMap[`${this.router.url}`]
      if (routeStep === this.data.step) {
        this.data.status = 'in-progress'
      } else if (routeStep > this.data.step) {
        this.data.status = 'completed'
      } else {
        this.data.status = 'untouched'
      }
    }
  }

  goToLink(link:string, funcParams:object={}) {
    // @ts-ignore
    const {runPreNavHook} = funcParams;

    if(link !== 'disabled'){
      if(link === 'function'){
        this.linkFunction.emit(funcParams);
      } else if (runPreNavHook) {
        this.linkFunction.emit(funcParams);
        link ? this.router.navigate([link]) : this.router.navigate(['not-found']);
      } else {
        link ? this.router.navigate([link]) : this.router.navigate(['not-found']);
      }
    }
  }

  btn_arrow = "btn-arrow"
  btnOnMouseEnter() {
    this.btn_arrow = "btn-arrow-hover"
  }
  btnOnMouseLeave() {
    this.btn_arrow = "btn-arrow"
  }

}


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
    navegations: []
  };
  @Output() public linkFunction = new EventEmitter();

  constructor(private readonly translationService: TranslateService, private readonly router: Router) { }

  ngOnInit(): void {}

  goToLink(link:string, funcParams:object={}) {
    if(link !== 'disabled'){
      if(link === 'function'){
        this.linkFunction.emit(funcParams);
      } else {
        link ? this.router.navigate([link]) : this.router.navigate(['not-found']);
      }
    }
  }

  checkCurrentRoute(link:string) {
    const currentRouter  = this.router.url;
    return link === currentRouter ? true : false;
  }

  class_yes="not-selected"
  class_no="not-selected"
  btn_arrow = "btn-arrow"
  btnOnMouseEnter() {
    this.btn_arrow = "btn-arrow-hover"
  }
  btnOnMouseLeave() {
    this.btn_arrow = "btn-arrow"
  }


  selectYes(){
    localStorage.setItem('krankgeschrieben','yes');
  }
  selectNo(){
    localStorage.setItem('krankgeschrieben','no');
  }

}


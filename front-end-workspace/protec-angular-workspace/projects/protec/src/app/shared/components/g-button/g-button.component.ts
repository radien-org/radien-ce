import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-g-button',
  templateUrl: './g-button.component.html',
  styleUrls: ['./g-button.component.scss']
})
export class GButtonComponent implements OnInit {

  @Input() public data: any = {
    label: 'no-data',
    type: 'default',
    link: '/',
    active: false,
    navegations: []
  };
  @Output() public linkFunction = new EventEmitter();

  constructor(private readonly router: Router) { }

  ngOnInit(): void {}

  goToLink(link:string) {
    if(link !== 'disabled'){
      if(link === 'function'){
        this.linkFunction.emit();
      } else {
        link ? this.router.navigate([link]) : this.router.navigate(['not-found']);
      }
    }
  }

  checkCurrentRoute(link:string) {
    const currentRouter  = this.router.url;
    return link === currentRouter ? true : false;
  }

  btn_arrow = "btn-arrow"
  btnOnMouseEnter() {
    this.btn_arrow = "btn-arrow-hover"
  }
  btnOnMouseLeave() {
    this.btn_arrow = "btn-arrow"
  }

}

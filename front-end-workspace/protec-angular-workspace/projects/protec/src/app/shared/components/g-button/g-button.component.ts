import { Component, OnInit, Input } from '@angular/core';
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

  constructor(private readonly router: Router) { }

  ngOnInit(): void {}

  goToLink(link:string) {
    link ? this.router.navigate([link]) : this.router.navigate(['not-found']);
  }

  checkCurrentRoute(link:string) {
    const currentRouter  = this.router.url;
    return link === currentRouter ? true : false;
  }

}

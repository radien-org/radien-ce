import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-wizard',
  templateUrl: './request-quote.component.html',
  styleUrls: ['./request-quote.component.scss']
})
export class RequestQuoteComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('ANGEBOT ANFORDERN'),
    type: 'outline',
    link: '/data-acquisition/wizard'
  }

  pageNav = {
    home: {
      label: this.translationService.instant('HOME'),
      type: 'anchor-button-home'
    },
    back: {
      label: this.translationService.instant('BACK'),
      type: 'footer-nav'
    }
  }
  constructor(private readonly translationService: TranslateService) { }
  ngOnInit(): void {}
}

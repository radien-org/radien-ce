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
    type: 'outline-lg',
    link: '/data-acquisition/wizard'
  }

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-intro'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/illness-2'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }
  ngOnInit(): void {}
}

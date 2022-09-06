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
    link: '/data-acquisition/your-data'
  }

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/chance-of-success'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }
  ngOnInit(): void {}
}

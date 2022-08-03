import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-pre-summary',
  templateUrl: './pre-summary.component.html',
  styleUrls: ['./pre-summary.component.scss']
})
export class PreSummaryComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/appreciation'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}


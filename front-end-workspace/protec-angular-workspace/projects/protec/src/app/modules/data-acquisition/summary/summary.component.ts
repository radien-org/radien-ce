import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.scss']
})
export class SummaryComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/part-body'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/chance-of-success'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

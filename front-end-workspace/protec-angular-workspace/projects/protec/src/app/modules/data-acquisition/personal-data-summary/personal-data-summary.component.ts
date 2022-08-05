import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-personal-data-summary',
  templateUrl: './personal-data-summary.component.html',
  styleUrls: ['./personal-data-summary.component.scss']
})
export class PersonalDataSummaryComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/personal-data-person'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/appreciation'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

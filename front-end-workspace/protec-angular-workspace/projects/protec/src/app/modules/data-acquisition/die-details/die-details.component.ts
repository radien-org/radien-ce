import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-die-details',
  templateUrl: './die-details.component.html',
  styleUrls: ['./die-details.component.scss']
})
export class DieDetailsComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/accident-date'
        },
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/accident-date'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

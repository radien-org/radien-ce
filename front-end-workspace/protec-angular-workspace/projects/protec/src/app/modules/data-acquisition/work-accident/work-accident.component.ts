import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-work-accident',
  templateUrl: './work-accident.component.html',
  styleUrls: ['./work-accident.component.scss']
})
export class WorkAccidentComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/details-intro'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/full-body'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

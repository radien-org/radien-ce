import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-part-body-figure',
  templateUrl: './part-body-figure.component.html',
  styleUrls: ['./part-body-figure.component.scss']
})
export class PartBodyFigureComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/full-body'//TODO we need put variation for work-accident
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/summary'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

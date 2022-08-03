import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-details-figure',//TODO change this component name to full-body-figure
  templateUrl: './details-figure.component.html',
  styleUrls: ['./details-figure.component.scss']
})
export class DetailsFigureComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/illness-diagnostic'//TODO we need put variation for work-accident
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/part-body'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-details-intro',
  templateUrl: './details-intro.component.html',
  styleUrls: ['./details-intro.component.scss']
})
export class DetailsIntroComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-date'
        },
        {
          label: this.translationService.instant('weiter'),
          link: ''
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) {}

  ngOnInit(): void {
  }

}

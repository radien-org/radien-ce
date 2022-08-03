import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-illness',
  templateUrl: './personal-data-input-2.component.html',
  styleUrls: ['./personal-data-input-2.component.scss']
})
export class PersonalDataInputComponentTwo implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/personal-data-input'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/illness-2'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }
}

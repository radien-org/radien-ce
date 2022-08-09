import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-illness',
  templateUrl: './personal-data-input-2.component.html',//TODO change component name to personal-data-contact
  styleUrls: ['./personal-data-input-2.component.scss']
})
export class PersonalDataInputComponentTwo implements OnInit {

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

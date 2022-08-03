import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-personal-data-information',
  templateUrl: './personal-data-information.component.html',
  styleUrls: ['./personal-data-information.component.scss']
})
export class PersonalDataInformationComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/chance-of-success'
        },
        {
          label: this.translationService.instant('weiter'),
          link: '/data-acquisition/personal-data-contact'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

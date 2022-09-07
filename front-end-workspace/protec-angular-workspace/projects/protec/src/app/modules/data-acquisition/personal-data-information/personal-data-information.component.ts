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
          label: this.translationService.instant('ZURÜCK'),
          link: '/data-acquisition/request-quote'
        },
        {
          label: this.translationService.instant('WEITER'),
          link: '/data-acquisition/personal-data-person'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

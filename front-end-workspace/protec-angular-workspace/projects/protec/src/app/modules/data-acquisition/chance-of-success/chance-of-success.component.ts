import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-chance-of-success',
  templateUrl: './chance-of-success.component.html',
  styleUrls: ['./chance-of-success.component.scss']
})
export class ChanceOfSuccessComponent implements OnInit {

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
          link: '/data-acquisition/personal-data-information'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

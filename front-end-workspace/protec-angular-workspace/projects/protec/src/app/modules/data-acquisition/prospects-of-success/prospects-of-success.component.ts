import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-prospects-of-success',
  templateUrl: './prospects-of-success.component.html',
  styleUrls: ['./prospects-of-success.component.scss']
})
export class ProspectsOfSuccess implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('Zurück'),
          link: '/data-acquisition/'
        },
        {
          label: this.translationService.instant('Weiter'),
          link: '/data-acquisition/'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

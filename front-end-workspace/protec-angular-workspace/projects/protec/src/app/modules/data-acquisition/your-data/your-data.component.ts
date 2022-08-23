import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-your-data',
  templateUrl: './your-data.component.html',
  styleUrls: ['./your-data.component.scss']
})
export class YourDataComponent implements OnInit {

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('Zurück'),
          link: '/data-acquisition/request-quote'
        },
        {
          label: this.translationService.instant('Weiter'),
          link: '/data-acquisition/personal-data-person'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-more-injuries',
  templateUrl: './more-injuries.component.html',
  styleUrls: ['./more-injuries.component.scss']
})
export class MoreInjuriesComponent implements OnInit {

  buttons = [{
    label: this.translationService.instant('JA'),
    type: 'outline',
    link: '/data-acquisition/more-injuries'
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'outline',
    link: '/data-acquisition/more-injuries'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('zurück'),
          link: '/data-acquisition/accident-intro'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

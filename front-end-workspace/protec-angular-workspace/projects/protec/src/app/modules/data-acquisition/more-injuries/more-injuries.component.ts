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
    link: '/data-acquisition/full-body'
  },
  {
    label: this.translationService.instant('NEIN'),
    type: 'outline',
    link: '/data-acquisition/summary'
  }]

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('back'),
          link: '/data-acquisition/part-body'
        }
      ]
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

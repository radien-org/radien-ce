import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-cookie-guide-line',
  templateUrl: './cookie-guide-line.component.html',
  styleUrls: ['./cookie-guide-line.component.scss']
})
export class CookieGuideLineComponent implements OnInit {
  pageNav = {
    home: {
      label: this.translationService.instant('HOME'),
      type: 'anchor-button-home'
    },
    back: {
      label: this.translationService.instant('BACK'),
      type: 'footer-nav'
    }
  }

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

}

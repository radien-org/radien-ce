import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from '../../../shared/services/cookie/cookie.service';

@Component({
  selector: 'app-we-are-sorry',
  templateUrl: './we-are-sorry.component.html',
  styleUrls: ['./we-are-sorry.component.scss']
})
export class WeAreSorryComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('WEITEREN FALL PRÜFEN'),
    type: 'outline',
    link: '/data-acquisition/accident-intro'
  }

  checks = {
    color: 'primary'
  }

  pageNav = {
    navegation: {
      type: 'navegation-buttons',
      navegations: [
        {
          label: this.translationService.instant('Zurück'),
          link: '/data-acquisition'
        }
      ]
    }
  }


  showCookieWindow = this.cookieService.getInLocal();

  cookieGuideLink = 'cookie-guide-line';

  constructor(private readonly translationService: TranslateService, private readonly cookieService : CookieService) {
    console.log('cookie status:', this.cookieService.getInLocal())
  }

  ngOnInit(): void {}

  changeVarCookieWindow(flag=false) {
    this.showCookieWindow = flag;
  }

}

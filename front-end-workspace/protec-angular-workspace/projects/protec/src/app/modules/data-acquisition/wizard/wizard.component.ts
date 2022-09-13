import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from '../../../shared/services/cookie/cookie.service';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('start_button'),
    type: 'outline',
    link: '/data-acquisition/accident-intro'
  }

  checks = {
    color: 'primary'
  }

  cookieWindowButton = {
    dataButtonOptionOne: {
      label: this.translationService.instant('cookies_all'),
      type: 'outline'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('cookies_only_necessary'),
      type: 'outline'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('MEINE AUSWAHL BESTÄTIGEN'),
      type: 'outline'
    }

  }

  pageNav = {
    home: {
      label: this.translationService.instant('home'),
      type: 'anchor-button-home'
    },
    back: {
      label: this.translationService.instant('ZURÜCK'),
      type: 'footer-nav'
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

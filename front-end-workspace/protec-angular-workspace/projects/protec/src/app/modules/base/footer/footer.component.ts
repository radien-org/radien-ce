import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
//import { TranslationService } from '../../../shared/services/translation/translation.service';
import { CookieService } from '../../../shared/services/cookie/cookie.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  footerNav = {
    dataButtonOptionOne: {
      label: this.translationService.instant('EINSTELLUNGEN'),
      type: 'footer-nav',
      link: 'disabled'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('COOKIES'),
      type: 'footer-nav',
      link: '/data-acquisition/cookie-guide-line'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('DATENSCHUTZ'),
      type: 'footer-nav',
      link: '/data-acquisition/legal-data-policy'
    },
    dataButtonOptionFour: {
      label: this.translationService.instant('IMPRESSUM'),
      type: 'footer-nav',
      link: '/data-acquisition/impressum'
    },
    dataButtonOptionFive: {
      label: this.translationService.instant('AGB'),
      type: 'footer-nav',
      link: '/data-acquisition/agb'
    }
  }

  constructor(private readonly translationService: TranslateService, private readonly router: Router, private readonly cookieService : CookieService) { }

  ngOnInit(): void {}

  cleanCookieStorage() {
    this.cookieService.cleanInLocal();
    window.location.reload();
  }

}

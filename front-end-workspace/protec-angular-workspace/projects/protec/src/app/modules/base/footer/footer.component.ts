import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { TranslationService } from '../../../shared/services/translation/translation.service';
import { Router } from '@angular/router';

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
      link: null
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
    }
  }

  constructor(private readonly translationService: TranslateService, private readonly router: Router) { }

  ngOnInit(): void {}

}

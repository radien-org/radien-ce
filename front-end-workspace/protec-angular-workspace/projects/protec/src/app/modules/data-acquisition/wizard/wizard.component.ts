import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss']
})
export class WizardComponent implements OnInit {

  dataButton = {
    label: this.translationService.instant('JETZT STARTEN'),
    type: 'outline'
  }

  checks = {
    color: 'primary'
  }

  cookieWindowButton = {
    dataButtonOptionOne: {
      label: this.translationService.instant('ALLE COOKIES AKZEPTIEREN'),
      type: 'outline'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('OPTIONALE COOKIES ABLEHNEN'),
      type: 'outline'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('MEINE AUSWAHL BESTÄTIGEN'),
      type: 'outline'
    }

  }

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

  footerNav = {
    dataButtonOptionOne: {
      label: this.translationService.instant('EINSTELLUNGEN'),
      type: 'footer-nav'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('COOKIES'),
      type: 'footer-nav'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('DATENSCHUTZ'),
      type: 'footer-nav'
    },
    dataButtonOptionFour: {
      label: this.translationService.instant('IMPRESSUM'),
      type: 'footer-nav'
    }
  }

  cookieGuideLink = 'cookie-guide-line';

  constructor(private readonly translationService: TranslateService) { }

  ngOnInit(): void {
  }

  test(mode:any){
    return typeof mode
  }

}

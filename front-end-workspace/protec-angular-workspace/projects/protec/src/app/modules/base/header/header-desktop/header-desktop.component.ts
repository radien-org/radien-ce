import { Component, OnInit } from '@angular/core';
import { TranslationService } from '../../../../shared/services/translation/translation.service';

@Component({
  selector: 'app-header-desktop',
  templateUrl: './header-desktop.component.html',
  styleUrls: ['./header-desktop.component.scss']
})
export class HeaderDesktopComponent implements OnInit {

  constructor(private readonly translationService: TranslationService) { }

  ngOnInit(): void {
  }

  backToBegin() {
    window.location.href='/';
  }

  public get availableTranslationLocales(): { key: string; locale: string }[] {
    return this.translationService.availableTranslationLocales;
  }

  public onClickTranslationLocale(locale: { key: string; locale: string }): void {
    //this.translationService.getTranslates(locale.locale);
    this.translationService.setLocale(locale.locale);
  }

  public getTranslationLocale() : string {
    return this.translationService.currentTranslationLocale
  }

  login() {
    window.location.href='https://idp-test.protec.help/auth/realms/protec/account';
    //window.open('https://idp-test.protec.help/auth/admin');
    //window.open('https://idp-test.protec.help/auth/admin', '_blank');
  }

}

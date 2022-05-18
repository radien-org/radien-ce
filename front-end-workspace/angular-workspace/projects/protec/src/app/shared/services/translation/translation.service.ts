import { Injectable, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LOCAL } from '../storage/local.enum';
import { StorageService } from '../storage/storage.service';
import { SUPPORTED_LANGUAGES } from './supported-languages';

@Injectable({
  providedIn: 'root',
})
export class TranslationService implements OnDestroy {
  public get availableTranslationLocales(): { key: string; locale: string }[] {
    return Object.values(SUPPORTED_LANGUAGES);
  }
  public get currentTranslationLocale(): string {
    return this.translate.currentLang || this.translate.getDefaultLang();
  }

  private readonly complete$ = new Subject();

  constructor(private readonly translate: TranslateService, private readonly storageService: StorageService) {
    this.translate.addLangs(Object.values(SUPPORTED_LANGUAGES).map(lang => lang.locale));

    this.translate.setDefaultLang(SUPPORTED_LANGUAGES.EN.locale);

    const storedLocale = this.storageService.getItem(LOCAL.LOCALE);

    if (storedLocale) {
      translate.use(storedLocale);
    } else {
      this.storageService.setItem(LOCAL.LOCALE, this.currentTranslationLocale);
    }

    this.setFileButtonLabel();

    this.subscribeToLanguageChange();
  }

  private setFileButtonLabel(): void {
    this.translate.get('fileChoose').subscribe((value: string) => {
      document.documentElement.style.setProperty('--upload-text', `"${value}"`);
    });
  }

  private subscribeToLanguageChange(): void {
    this.translate.onLangChange.pipe(takeUntil(this.complete$)).subscribe(langEvent => {
      /**
       * Change moment locale, date-format pipe uses it to display the date
       */
      moment.locale(langEvent.lang);

      /**
       * :after ... {content: }
       */
      this.setFileButtonLabel();
    });
  }

  public ngOnDestroy() {
    this.complete$.next();

    this.complete$.complete();
  }

  public setLocale(locale: string): void {
    this.translate.use(locale);

    this.storageService.setItem(LOCAL.LOCALE, locale);
  }
}

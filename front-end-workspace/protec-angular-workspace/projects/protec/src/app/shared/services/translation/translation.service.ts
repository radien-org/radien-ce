import { Injectable, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LOCAL } from '../storage/local.enum';
import { StorageService } from '../storage/storage.service';
import { SUPPORTED_LANGUAGES } from './supported-languages';
import { Observable } from 'rxjs';
import { pluck } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

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

  constructor(private readonly translate: TranslateService, private readonly storageService: StorageService, private readonly http: HttpClient) {
    this.translate.addLangs(Object.values(SUPPORTED_LANGUAGES).map(lang => lang.locale));

    this.translate.setDefaultLang(SUPPORTED_LANGUAGES.PT.locale);

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
    this.complete$.next(true);

    this.complete$.complete();
  }

  public setLocale(locale: string): void {
    this.translate.use(locale);

    this.storageService.setItem(LOCAL.LOCALE, locale);
  }

  public getTranslates(lang: string) {
    this.getApiTranslatefile(lang).subscribe(data => {
      this.storageService.setItem(LOCAL.TRANSLATE_FILE, data);
    });
  }

  private getApiTranslatefile(lang: string): Observable<any> {
    const url = `/cms/i18n/properties/${lang}`;

    return this.http.get<any>(url).pipe(pluck('responseData'));
  }
}

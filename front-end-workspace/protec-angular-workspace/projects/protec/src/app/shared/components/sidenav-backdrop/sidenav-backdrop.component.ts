import { Component, EventEmitter, OnInit, Input, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CookieService } from '../../services/cookie/cookie.service';
import {LOCAL} from "../../services/storage/local.enum";

@Component({
  selector: 'app-sidenav-backdrop',
  templateUrl: './sidenav-backdrop.component.html',
  styleUrls: ['./sidenav-backdrop.component.scss']
})
export class SidenavBackdropComponent implements OnInit {

  @Input() public dataComponent: any;
  @Output() public closeModalFunc = new EventEmitter();
  @Output() public changeVarCookieWindow = new EventEmitter();

  checks = {
    color: 'primary'
  }

  cookieWindowButton = {
    dataButtonOptionOne: {
      label: this.translationService.instant('ALLE COOKIES AKZEPTIEREN'),
      type: 'cookie-window',
      link: 'disabled'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('OPTIONALE COOKIES ABLEHNEN'),
      type: 'cookie-window',
      link: 'disabled'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('MEINE AUSWAHL BESTÄTIGEN'),
      type: 'cookie-window',
      link: 'disabled'
    }
  }

  cookieWindowButtonDefault = {
    dataButtonOptionOne: {
      label: this.translationService.instant('ALLE COOKIES AKZEPTIEREN'),
      type: 'cookie-window',
      link: 'disabled'
    },
    dataButtonOptionTwo: {
      label: this.translationService.instant('OPTIONALE COOKIES ABLEHNEN'),
      type: 'cookie-window',
      link: 'disabled'
    },
    dataButtonOptionThree: {
      label: this.translationService.instant('EINSTELLUNGEN'),
      type: 'cookie-window',
      link: 'disabled'
    }
  }



  cookieGuideLink = '/data-acquisition/cookie-guide-line';

  constructor(private readonly translationService: TranslateService, private readonly router: Router, private readonly cookieService : CookieService) { }

  ngOnInit(): void {}

  goTo(url:string) {
    this.router.navigate([url]);
  }

  getAcceptedCookie(cookies: string) {
    this.cookieService.getAcceptedCookie(cookies).then((x:any) => {
      console.log('save cookie request response:', cookies)
      this.cookieService.saveInLocal(cookies);
      this.closeModal();
    }).catch((error:any) => {
      console.error('erro in save cookie request:', error)
      this.cookieService.saveInLocal(cookies);
      this.closeModal();
    });

  }

  public closeModal(): void {
    this.closeModalFunc.emit();
    this.changeVarCookieWindow.emit();
  }

  windowDefault = 'hide';
  windowEssenziell = 'show'
  public changeCookieWindow(){
    this.windowDefault = 'show'
    this.windowEssenziell = 'hide'
  }

}

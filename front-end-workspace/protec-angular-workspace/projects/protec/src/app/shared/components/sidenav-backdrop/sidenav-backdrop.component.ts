import { Component, OnInit, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CookieService } from '../../services/cookie/cookie.service';

@Component({
  selector: 'app-sidenav-backdrop',
  templateUrl: './sidenav-backdrop.component.html',
  styleUrls: ['./sidenav-backdrop.component.scss']
})
export class SidenavBackdropComponent implements OnInit {

  @Input() public dataComponent: any;

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

  cookieGuideLink = '/data-acquisition/cookie-guide-line';

  constructor(private readonly translationService: TranslateService, private readonly router: Router, private readonly cookieService : CookieService) { }

  ngOnInit(): void {
  }

  goTo(url:string) {
    this.router.navigate([url]);
  }

  getAcceptedCookie(cookies: string) {
    let x = this.cookieService.getAcceptedCookie(cookies);
    console.log(x);
    return x;
  }

}
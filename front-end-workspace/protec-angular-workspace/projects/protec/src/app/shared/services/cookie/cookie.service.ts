import { Injectable } from '@angular/core';
import { LOCAL } from '../storage/local.enum';
import { StorageService } from '../storage/storage.service';

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  private serviceUrl: string = "/nwprotecservice/cookie/permittedCookies";

  constructor(private readonly storageService: StorageService) { }

  public getAcceptedCookie(search: string = "all") : any {

    let url: string = `${this.serviceUrl}?cookieType=${search}`;

    return fetch(url).then(res => {
      if(res.ok) {
        return res.json();
      } else {
        throw new ReferenceError(JSON.stringify(res));
      }
    }).then(body => {
      return body;
    });
  }

  public saveInLocal(search: string) {
    this.storageService.setItem(LOCAL.COOKIE_DECISION, search);
  }

  public getInLocal(): boolean {
    console.log('debug:', this.storageService.getItem(LOCAL.COOKIE_DECISION))
    return this.storageService.getItem(LOCAL.COOKIE_DECISION) !== null ? false : true;
  }
}

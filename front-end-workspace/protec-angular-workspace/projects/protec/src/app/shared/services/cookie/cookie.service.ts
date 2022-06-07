import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CookieService {

  private serviceUrl: string = "/api/cookie/permittedCookies";

  constructor() { }

  public getAcceptedCookie(search: string = "all") : any {

    let url: string = `${this.serviceUrl}?cookieType=${search}`;

    return fetch(url).then(res => {
      if(res.ok) {
        return res.json();
      } else {
        throw new ReferenceError(JSON.stringify(res));
      }
    }).then(body => {
      console.log(body)
      return body;
    });
  }
}

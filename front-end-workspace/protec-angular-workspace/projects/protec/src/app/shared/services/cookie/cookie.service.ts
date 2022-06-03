/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

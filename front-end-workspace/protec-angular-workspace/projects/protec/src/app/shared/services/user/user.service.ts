import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import User from '../../models/user/User';
import Page from '../../models/page/Page';
import UserFactory from '../../models/user/UserFactory';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private serviceURL : string = "/api/user";

  constructor(private keycloakService: KeycloakService,
             private userFactory: UserFactory) { }

  async getAllUsers(search: string | undefined,
             pageNumber: number = 1,
             pageSize: number = 10) : Promise<Page<User>> {
    let url: string = `${this.serviceURL}?pageNo=${pageNumber}&pageSize=${pageSize}`;
    let bearer: string = "";
    await this.keycloakService.getToken().then(token => { bearer = token });
    return fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${bearer}`,
      }
    }).then(res => {
      if(res.ok) {
        return res.json();
      } else {
        throw new ReferenceError(String(res.headers));
      }
    }).then(body => {
      let resultsArray = body['results'] as any[];
      return new Page<User> (
        body['currentPage'],
        body['totalResults'],
        body['totalPages'],
        resultsArray.map(obj => { return this.userFactory.convert(obj) }),
      );
    }).catch(e => {
      console.log(e);
      return new Page<User>(0,0,0, []);
    });
  }

  async deleteUser(userId: number): Promise<boolean> {
    let url = `${this.serviceURL}/${userId}`;
    let bearer = "";
    await this.keycloakService.getToken().then(token => { bearer = token; });

    return fetch(url, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${bearer}`,
      }
    }).then(response => {
      return response.ok;
    });
  }

  async updateUser(user: User): Promise<boolean> {
    let url = `${this.serviceURL}/${user.id}`;
    let bearer = "";
    await this.keycloakService.getToken().then(token => { bearer = token; });

    console.log({...user, lastUpdate: new Date().toISOString(), terminationDate: user.terminationDate?.toISOString()})
    return fetch(url, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${bearer}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({...user, lastUpdate: new Date().toISOString()}),
    }).then(response => {
      return response.ok;
    });
  }
}

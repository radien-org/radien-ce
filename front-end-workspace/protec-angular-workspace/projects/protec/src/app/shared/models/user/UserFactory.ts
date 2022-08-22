import { Injectable } from "@angular/core";
import User from "./User";

@Injectable({
  providedIn: 'root'
})

export default class UserFactory {
  convert(jsonObject: any): User {
    return new User(
      jsonObject['id'],
      jsonObject['logon'],
      jsonObject['userEmail'],
      jsonObject['terminationDate'],
      jsonObject['enabled'],
      jsonObject['sub'],
      jsonObject['firstname'],
      jsonObject['lastname'],
      jsonObject['delegatedCreation'],
      jsonObject['createDate'],
      jsonObject['lastUpdate']
    );
  }
}

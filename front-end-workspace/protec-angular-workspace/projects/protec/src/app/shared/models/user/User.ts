import { AbstractEntity } from "../AbstractEntity";

interface IUser extends AbstractEntity {
  id: number,
  logon: string,
  userEmail: string,
  terminationDate?: Date,
  enabled: boolean,
  sub: string,
  firstname: string,
  lastname: string,
  delegatedCreation: boolean
}

export default class User implements IUser {
  id: number = -1;
  logon: string = "";
  userEmail: string = "";
  terminationDate?: Date = new Date();
  enabled: boolean = false;;
  sub: string = "";
  firstname: string = "";
  lastname: string = "";
  delegatedCreation: boolean = false;
  createDate: Date = new Date();
  lastUpdate: Date = new Date();

  constructor(id: number,
             logon: string,
             userEmail: string,
             terminationDate: Date,
             enabled: boolean,
             sub: string,
             firstName: string,
             lastName: string,
             delegatedCreation: boolean,
             createDate: Date,
             lastUpdate: Date) {
               this.id = id;
               this.logon = logon;
               this.userEmail = userEmail;
               this.terminationDate = terminationDate ? new Date(terminationDate) : undefined;
               this.enabled = enabled;
               this.sub = sub;
               this.firstname = firstName;
               this.lastname = lastName;
               this.delegatedCreation = delegatedCreation;
               this.createDate = createDate;
               this.lastUpdate = lastUpdate;
             }

  getFullname(): string {
    return `${this.firstname} ${this.lastname}`;
  }
}

import { Injectable } from '@angular/core';
import { LOCAL } from '../../../shared/services/storage/local.enum';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  
  public getItem(key: string): any {
    try {
      const str = localStorage.getItem(key) ? localStorage.getItem(key) : 'de';
      return JSON.parse(String(str));
    } catch {
      this.removeItem(key);

      return null;
    }
  }

  public getTableState(key: string): { columns: any; filter: any; page: any; sort: any } {
    return this.getItem(key);
  }

  public removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  public setItem(key: string, value: any): boolean {
    try {
      localStorage.setItem(key, JSON.stringify(value));

      return true;
    } catch {
      return false;
    }
  }

  public setTableState(key: string, filter: any, sort: any, page: any, columns: any): void {
    const tableState = { filter, sort, page, columns };
    this.setItem(key, tableState);
  }

  private getRandomArbitrary(min:number, max:number) {
    return Math.random() * (max - min) + min;
  }

  private buildTitle(type:string) {
    return type;
  }
  
  public saveDataAcquisition() {
    let CLAIMS_LIST = this.getItem(LOCAL.CLAIMS_LIST);
    const ACCIDENT_TYPE = this.getItem(LOCAL.ACCIDENT_TYPE);
    const ADDITIONAL_INSURANCE_FORM = this.getItem(LOCAL.ADDITIONAL_INSURANCE_FORM);
    const ADDITIONAL_INSURANCE_FORM_SECOND = this.getItem(LOCAL.ADDITIONAL_INSURANCE_FORM_SECOND);
    const PRIVATE_ACCIDENT_FORM = this.getItem(LOCAL.PRIVATE_ACCIDENT_FORM);
    const ILLNESS_DIAGNOSTIC_FORM = this.getItem(LOCAL.ILLNESS_DIAGNOSTIC_FORM);
    const WORK_ACCIDENT_FORM = this.getItem(LOCAL.WORK_ACCIDENT_FORM);
    const ACCIDENT_DATE_FORM = this.getItem(LOCAL.ACCIDENT_DATE_FORM);
    const PERSONAL_DATA_PERSON = this.getItem(LOCAL.PERSONAL_DATA_PERSON);
    const PERSONAL_DATA_CONTACT = this.getItem(LOCAL.PERSONAL_DATA_CONTACT);

    console.log('debug (CLAIMS_LIST):', CLAIMS_LIST);
    console.log('debug (ADDITIONAL_INSURANCE_FORM):', ADDITIONAL_INSURANCE_FORM);
    console.log('debug (ADDITIONAL_INSURANCE_FORM_SECOND):', ADDITIONAL_INSURANCE_FORM_SECOND);
    console.log('debug (PRIVATE_ACCIDENT_FORM):', PRIVATE_ACCIDENT_FORM);
    console.log('debug (ILLNESS_DIAGNOSTIC_FORM):', ILLNESS_DIAGNOSTIC_FORM);
    console.log('debug (WORK_ACCIDENT_FORM):', WORK_ACCIDENT_FORM);
    console.log('debug (ACCIDENT_DATE_FORM):', ACCIDENT_DATE_FORM);
    console.log('debug (PERSONAL_DATA_PERSON):', PERSONAL_DATA_PERSON);
    console.log('debug (PERSONAL_DATA_CONTACT):', PERSONAL_DATA_CONTACT);

    const obj = {
      id: this.getRandomArbitrary(100000, 1000000),
      title: this.buildTitle(ACCIDENT_TYPE),
      mainData: [
        {
          id: 'ADDITIONAL_INSURANCE_FORM',
          label: 'Additional Insurance',
          value: ADDITIONAL_INSURANCE_FORM
        },
        {
          id: 'ADDITIONAL_INSURANCE_FORM_SECOND',
          label: 'Additional Insurance Second',
          value: ADDITIONAL_INSURANCE_FORM_SECOND
        },
        {
          id: 'PRIVATE_ACCIDENT_FORM',
          label: 'Private Accident',
          value: PRIVATE_ACCIDENT_FORM
        },
        {
          id: 'ILLNESS_DIAGNOSTIC_FORM',
          label: 'Illness Diagnostic',
          value: ILLNESS_DIAGNOSTIC_FORM
        },
        {
          id: 'WORK_ACCIDENT_FORM',
          label: 'Work Accident',
          value: WORK_ACCIDENT_FORM
        },
        {
          id: 'ACCIDENT_DATE_FORM',
          label: 'Accident Date',
          value: ACCIDENT_DATE_FORM
        },
        {
          id: 'PERSONAL_DATA_PERSON',
          label: 'Personal Data Person',
          value: PERSONAL_DATA_PERSON
        },
        {
          id: 'PERSONAL_DATA_CONTACT',
          label: 'Personal Data Contact',
          value: PERSONAL_DATA_CONTACT
        }
      ]
    }

    console.log('debug (obj):', obj, CLAIMS_LIST);

    if(!CLAIMS_LIST){
      CLAIMS_LIST = [];
    }

    CLAIMS_LIST.push(obj);

    this.setItem(LOCAL.CLAIMS_LIST, CLAIMS_LIST);
  }
}

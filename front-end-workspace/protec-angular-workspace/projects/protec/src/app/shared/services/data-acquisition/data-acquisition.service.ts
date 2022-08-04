import { Injectable } from '@angular/core';
import { LOCAL } from '../storage/local.enum';
import { StorageService } from '../storage/storage.service';

@Injectable({
  providedIn: 'root'
})
export class DataAcquisitionService {

  private serviceUrl: string = "/nwprotecservice";

  constructor(private readonly storageService: StorageService) { }

  public getAdditionalInsuranceOptions() : any {

    let url: string = `${this.serviceUrl}/additionalInsurance/values`;

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
}

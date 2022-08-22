import { Injectable } from "@angular/core";
import { Tenant } from "./Tenant";

@Injectable({
  providedIn: 'root'
})

export default class TenantFactory {
  convert(jsonObject: any): Tenant {
    return new Tenant(
      jsonObject['id'],
      jsonObject['name'],
      jsonObject['tenantKey'],
      jsonObject['tenantType'],
      jsonObject['tenantStart'],
      jsonObject['tenantEnd']
    );
  }
}

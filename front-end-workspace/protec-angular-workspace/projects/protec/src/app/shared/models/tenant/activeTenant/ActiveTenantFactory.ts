import { Injectable } from "@angular/core";
import ActiveTenant from "./ActiveTenant";

@Injectable({
  providedIn: 'root'
})


export default class ActiveTenantFactory {
  convert(jsonObject: any) {
    return new ActiveTenant(
      jsonObject["userId"],
      jsonObject["tenantId"],
      jsonObject["id"],
    );
  }
}

interface ITenant {
  id: string,
  name: string,
  tenantKey: string,
  tenantType: string,
  tenantStart: Date,
  tenantEnd: Date,
}

export class Tenant implements ITenant {
  id: string;
  name: string;
  tenantKey: string;
  tenantType: string;
  tenantStart: Date;
  tenantEnd: Date;

  constructor(id: string,
             name: string,
             tenantKey: string,
             tenantType: string,
             tenantStart: string,
             tenantEnd: string) {
               this.id = id;
               this.name = name;
               this.tenantKey = tenantKey;
               this.tenantType = tenantType;
               this.tenantStart = new Date(tenantStart);
               this.tenantEnd = new Date(tenantEnd);
             }
}

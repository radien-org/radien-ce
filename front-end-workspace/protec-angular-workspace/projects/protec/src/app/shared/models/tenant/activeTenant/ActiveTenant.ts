interface IActiveTenant {
  userId: number,
  tenantId: number,
}

export default class ActiveTenant implements IActiveTenant {
  id?: number;
  userId: number;
  tenantId: number;

  constructor(userId: number,
             tenantId: number,
             id?: number) {
               this.id=id;
               this.userId = userId;
               this.tenantId = tenantId;
             }
}

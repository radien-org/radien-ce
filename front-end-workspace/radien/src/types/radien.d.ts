declare module "radien" {
    interface RadienModel {
        id?: number;
    }

    interface User extends RadienModel {
        sub: string;
        firstname: string;
        lastname: string;
        userEmail: string;
        logon: string;
        mobileNumber: string;
        processingLocked: boolean;
        enabled: boolean;
        delegatedCreation: boolean;
        terminationDate: Date;
        createDate: Date;
        lastUpdate: Date;
    }

    interface UserRequest extends User {
        terminationDate: string | Date | null;
    }
    interface UserPasswordChanging {
        login: string;
        oldPassword: string;
        newPassword: string;
        id: string;
    }

    interface Tenant extends RadienModel {
        name: string;
        tenantKey: string;
        tenantType: string;
        tenantStart: Date;
        tenantEnd: Date;
        clientAddress: string;
        clientZipCode: string;
        clientCity: string;
        clientCountry: string;
        clientPhoneNumber: number;
        clientEmail: string;
        parentId: number;
        clientId: number;
        parentData?: Tenant;
        clientData?: Tenant;
    }

    interface SplitEmptyColumnDefinition{
        header: string,
        body: React.ReactNode
    }

    interface ActiveTenant extends RadienModel {
        userId: number;
        tenantId: number;
    }

    interface Role extends RadienModel {
        name: string;
        description: string;
    }

    interface Action extends RadienModel {
        name: string;
    }
    interface Resource extends RadienModel {
        name: string;
    }

    interface TenantRole extends RadienModel {
        tenantId: number;
        roleId: number;
        tenant?: Tenant;
        role?: Role;
    }

    interface Permission {
        id?: number;
        name: string;
        actionId: number;
        resourceId: number;
        action?: Action;
        resource?: Resource;
    }

    interface Ticket extends RadienModel {
        userId: number;
        ticketType: number;
        expireDate: Date | string;
        token: string;
        data: string;
        createUser: number;
    }

    interface Page<T> {
        results: T[];
        currentPage: number;
        totalResults: number;
        totalPages: number;
    }
}

declare module "radien" {
    interface User {
        id?: number,
        sub: string,
        firstname: string,
        lastname: string,
        userEmail: string,
        logon: string,
        mobileNumber: string,
        enabled: boolean,
        delegatedCreation: boolean,
        terminationDate: Date,
        createDate: Date,
        lastUpdate: Date
    }

    interface Tenant {
        id?: number,
        name: string,
        tenantKey: string,
        tenantType: string,
        tenantStart: Date,
        tenantEnd: Date,
        clientAddress: string,
        clientZipCode: string,
        clientCity: string,
        clientCountry: string,
        clientPhoneNumber: number,
        clientEmail: string,
        parentId: number,
        clientId: number
    }

    interface ActiveTenant {
        id?: number,
        userId: number,
        tenantId: number,
    }

    interface Role {
        id?: number,
        name: string,
        description: string,
        terminationDate: Date
    }

    interface Permission {
        id?: number,
        name: string,
        actionId: number,
        resourceId: number
    }

    interface Page<T> {
        results: T[],
        currentPage: number,
        totalResults: number,
        totalPages: number
    }
}


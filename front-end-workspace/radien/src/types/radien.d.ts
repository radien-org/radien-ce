declare module "radien" {
    interface RadienModel {
        id?: number
    }

    interface User extends RadienModel {
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

    interface Tenant extends RadienModel {
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

    interface ActiveTenant extends RadienModel {
        userId: number,
        tenantId: number,
    }

    interface Role extends RadienModel {
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

    interface Ticket extends RadienModel {
        userId: number,
        ticketType: number,
        expireDate: Date,
        token: string,
        data: string,
        createUser: number,
    }

    interface Page<T> {
        results: T[],
        currentPage: number,
        totalResults: number,
        totalPages: number
    }
}


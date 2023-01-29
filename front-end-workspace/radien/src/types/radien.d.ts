declare module "radien" {
    interface User {
        id?: number,
        sub: string,
        firstname: string,
        lastname: string,
        userEmail: string,
        logon: string,
        enabled: boolean,
        createDate: Date,
        lastUpdate: Date
    }

    interface Tenant {
        id?: number,
        name: string,
        tenantKey: string,
        tenantType: string,
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

    interface Page<T> {
        results: T[],
        currentPage: number,
        totalResults: number,
        totalPages: number
    }
}

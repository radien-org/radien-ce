enum QueryKeys {
    ME = "userInSession",
    I18N = "i18n",
    ACTIVE_TENANT = "activeTenant",
    ASSIGNED_TENANTS = "assignedTenants",
    ROLE_MANAGEMENT = "roleManagement",
    USER_MANAGEMENT = "userManagement",
    PERMISSION_MANAGEMENT = "permissionManagement",
    TENANT_MANAGEMENT = "tenantManagement",
    AVAILABLE_TENANTS = "availableTenants"
}

enum TicketType {
    EMAIL_CHANGE = 1,
    REFERENCE_LINK = 2,
    GDPR_DATA_REQUEST = 3,
    TENANT_REQUEST = 4,
}

export { QueryKeys, TicketType };

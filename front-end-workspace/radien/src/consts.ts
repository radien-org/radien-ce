enum QueryKeys {
    ME = "userInSession",
    I18N = "i18n",
    ACTIVE_TENANT = "activeTenant",
    AVAILABLE_TENANTS = "availableTenants",
    ROLE_MANAGEMENT = "roleManagement",
    USER_MANAGEMENT = "userManagement",
    PERMISSION_MANAGEMENT = "permissionManagement",
    TENANT_MANAGEMENT = "tenantManagement",
    COMPLETE_TENANT_LIST = "completeTenantList"
}

enum TicketType {
    EMAIL_CHANGE = 1,
    REFERENCE_LINK = 2,
    GDPR_DATA_REQUEST = 3,
}

export { QueryKeys, TicketType };

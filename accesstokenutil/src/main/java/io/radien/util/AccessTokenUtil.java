package io.radien.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class AccessTokenUtil {

    private final static String host = "http://localhost";

    private final static String ROLE_HOST_PORT_KEY = "role.management.host.port";
    private final static String PERMISSION_HOST_PORT_KEY = "permission.management.host.port";
    private final static String TENANT_HOST_PORT_KEY = "tenant.management.host.port";

    private final static String DEFAULT_ROLE_MANAGEMENT_HOST_PORT = host + ":8080";
    private final static String DEFAULT_PERMISSION_MANAGEMENT_HOST_PORT = host + ":8080";
    private final static String DEFAULT_TENANT_MANAGEMENT_HOST_PORT = host + ":8080";

    public static String getRoleManagementBaseURL() {
        String hostPort = System.getenv(ROLE_HOST_PORT_KEY);
        if (hostPort == null) {
            hostPort = DEFAULT_ROLE_MANAGEMENT_HOST_PORT;
        }
        return hostPort + "/rolemanagementservice/v1";
    }

    public static String getPermissionManagementBaseURL() {
        String hostPort = System.getenv(PERMISSION_HOST_PORT_KEY);
        if (hostPort == null) {
            hostPort = DEFAULT_PERMISSION_MANAGEMENT_HOST_PORT;
        }
        return hostPort + "/permissionmanagementservice/v1";
    }

    public static String getTenantManagementBaseURL() {
        String hostPort = System.getenv(TENANT_HOST_PORT_KEY);
        if (hostPort == null) {
            hostPort = DEFAULT_TENANT_MANAGEMENT_HOST_PORT;
        }
        return hostPort + "/tenantmanagementservice/v1";
    }

    public static void main(String[] args) {
        if(args.length < 5) {
            System.out.println("Usage URL ClientId ClientSecret Username Password");
            System.exit(1);
        }
        String url = args[0];
        String clientId = args[1];
        String clientSecret = args[2];
        String username = args[3];
        String password = args[4];

        HttpResponse<HashMap> response = Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", clientId)
                .field("client_secret",clientSecret)
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .asObject(HashMap.class);
        String accessToken = (String)response.getBody().get("access_token");
        System.out.println(response.getBody().get("access_token"));

        tenantCreation(accessToken);
        actionCreation(accessToken);
        resourceCreation(accessToken);
        permissionCreation(accessToken);
        roleCreation(accessToken);
        tenantRoleCreation(accessToken);
    }

    private static void tenantRoleCreation(String accessToken) {
        String tenantRoleUrl= getRoleManagementBaseURL() + "/tenantrole";
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":1, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole2",accessToken,"{\"tenantId\":2, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":3, \"roleId\":1}");

        String assignPermissionLayout = tenantRoleUrl + "/assign/permission/%d/tenant/%d/role/%d";
        makePostRequest(String.format(assignPermissionLayout,1,1,1),"assignPermission1",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,2,1,1),"assignPermission2",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,3,1,1),"assignPermission3",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,4,1,1),"assignPermission4",accessToken,"");

        makePostRequest(String.format(assignPermissionLayout,4,2,1),"assignPermission5",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,4,3,1),"assignPermission6",accessToken,"");

        makePostRequest(String.format(assignPermissionLayout,1,1,1),"assignPermission7",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,1,2,1),"assignPermission8",accessToken,"");
        makePostRequest(String.format(assignPermissionLayout,1,3,1),"assignPermission9",accessToken,"");

        String assignUserLayout = tenantRoleUrl + "/assign/user/%d/tenant/%d/role/%d";
        makePostRequest(String.format(assignUserLayout,1,2,1),"assignUser1ToTenantRole2",accessToken,"");
        makePostRequest(String.format(assignUserLayout,1,1,1),"assignUser1ToTenantRole1",accessToken,"");
        makePostRequest(String.format(assignUserLayout,1,3,1),"assignUser1ToTenantRole3",accessToken,"");
    }

    private static void roleCreation(String accessToken) {
        System.out.println("Starting roles...");
        String roleUrl= getRoleManagementBaseURL() + "/role";
        List<String> roles = Arrays.asList(
                "{ \"name\": \"System Administrator\", \"description\": \"The BOSS!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Permission Administrator\", \"description\": \"The Role for Permission Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Role Administrator\", \"description\": \"The Role for Role Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"User Administrator\", \"description\": \"The Role for User Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Tenant Administrator\", \"description\": \"The Role for Tenant Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Client Tenant Administrator\", \"description\": \"The Role for the Client Tenant administrators!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Sub Tenant Administrator\", \"description\": \"The Role for the Sub Tenant administrators!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Guest\", \"description\": \"The Role for Guests!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Approver\", \"description\": \"The Role for users that only approve data!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Author\", \"description\": \"The Role for users that only create data!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Publisher\", \"description\": \"The Role for the publishers!\", \"terminationDate\": \"2030-12-12T00:00:00\" }"
        );
        for(int i =0;i<roles.size();i++){
            makePostRequest(roleUrl,"role"+i,accessToken,roles.get(i));
        }
    }

    private static void resourceCreation(String accessToken) {
        System.out.println("Starting resource...");
        String resourceUrl= getPermissionManagementBaseURL() + "/resource";

        /**
         * User
         * Roles
         * Permission
         * Resource
         * Action
         * Tenant
         * TenantRole
         * TenantRolePermission
         * TenantRoleUser
         */
        String user = "{ \"name\": \"User\" }";
        String roles = "{ \"name\": \"Roles\" }";
        String permission = "{ \"name\": \"Permission\" }";
        String resource = "{ \"name\": \"Resource\" }";
        String action = "{ \"name\": \"Action\" }";
        String tenant = "{ \"name\": \"Tenant\" }";
        String tenantRole = "{ \"name\": \"Tenant Role\" }";
        String tenantRolePermission = "{ \"name\": \"Tenant Role Permission\" }";
        String tenantRoleUser = "{ \"name\": \"Tenant Role User\" }";

        makePostRequest(resourceUrl,"resource user",accessToken,user);
        makePostRequest(resourceUrl,"resource roles",accessToken,roles);
        makePostRequest(resourceUrl,"resource permission",accessToken,permission);
        makePostRequest(resourceUrl,"resource resource",accessToken,resource);
        makePostRequest(resourceUrl,"resource action",accessToken,action);
        makePostRequest(resourceUrl,"resource tenant",accessToken,tenant);
        makePostRequest(resourceUrl,"resource tenant role",accessToken,tenantRole);
        makePostRequest(resourceUrl,"resource tenant role permission",accessToken,tenantRolePermission);
        makePostRequest(resourceUrl,"resource tenant role user",accessToken,tenantRoleUser);
    }

    private static void permissionCreation(String accessToken){
        System.out.println("Start permission...");
        String permissionUrl = getPermissionManagementBaseURL() + "/permission";

        // Permissions regarding User
        String permission1 = "{ \"name\": \"User Management - Create\", \"actionId\": 1, \"resourceId\": 1 }";
        String permission2 = "{ \"name\": \"User Management - Read\", \"actionId\": 2, \"resourceId\": 1 }";
        String permission3 = "{ \"name\": \"User Management - Update\", \"actionId\": 3, \"resourceId\": 1 }";
        String permission4 = "{ \"name\": \"User Management - Delete\", \"actionId\": 4, \"resourceId\": 1 }";
        String permission5 = "{ \"name\": \"User Management - All\", \"actionId\": 5, \"resourceId\": 1 }";

        makePostRequest(permissionUrl,"permission1",accessToken,permission1);
        makePostRequest(permissionUrl,"permission2",accessToken,permission2);
        makePostRequest(permissionUrl,"permission3",accessToken,permission3);
        makePostRequest(permissionUrl,"permission4",accessToken,permission4);
        makePostRequest(permissionUrl,"permission5",accessToken,permission5);

        // Permissions regarding Roles
        String permission6 = "{ \"name\": \"Roles Management - Create\", \"actionId\": 1, \"resourceId\": 2 }";
        String permission7 = "{ \"name\": \"Roles Management - Read\", \"actionId\": 2, \"resourceId\": 2 }";
        String permission8 = "{ \"name\": \"Roles Management - Update\", \"actionId\": 3, \"resourceId\": 2 }";
        String permission9 = "{ \"name\": \"Roles Management - Delete\", \"actionId\": 4, \"resourceId\": 2 }";
        String permission10 = "{ \"name\": \"Roles Management - All\", \"actionId\": 5, \"resourceId\": 2 }";

        makePostRequest(permissionUrl,"permission6",accessToken,permission6);
        makePostRequest(permissionUrl,"permission7",accessToken,permission7);
        makePostRequest(permissionUrl,"permission8",accessToken,permission8);
        makePostRequest(permissionUrl,"permission9",accessToken,permission9);
        makePostRequest(permissionUrl,"permission10",accessToken,permission10);

        // Permissions regarding Permission
        String permission11 = "{ \"name\": \"Permission Management - Create\", \"actionId\": 1, \"resourceId\": 3 }";
        String permission12 = "{ \"name\": \"Permission Management - Read\", \"actionId\": 2, \"resourceId\": 3 }";
        String permission13 = "{ \"name\": \"Permission Management - Update\", \"actionId\": 3, \"resourceId\": 3 }";
        String permission14 = "{ \"name\": \"Permission Management - Delete\", \"actionId\": 4, \"resourceId\": 3 }";
        String permission15 = "{ \"name\": \"Permission Management - All\", \"actionId\": 5, \"resourceId\": 3 }";

        makePostRequest(permissionUrl,"permission11",accessToken,permission11);
        makePostRequest(permissionUrl,"permission12",accessToken,permission12);
        makePostRequest(permissionUrl,"permission13",accessToken,permission13);
        makePostRequest(permissionUrl,"permission14",accessToken,permission14);
        makePostRequest(permissionUrl,"permission15",accessToken,permission15);

        // Permissions regarding Resource
        String permission16 = "{ \"name\": \"Resource Management - Create\", \"actionId\": 1, \"resourceId\": 4 }";
        String permission17 = "{ \"name\": \"Resource Management - Read\", \"actionId\": 2, \"resourceId\": 4 }";
        String permission18 = "{ \"name\": \"Resource Management - Update\", \"actionId\": 3, \"resourceId\": 4 }";
        String permission19 = "{ \"name\": \"Resource Management - Delete\", \"actionId\": 4, \"resourceId\": 4 }";
        String permission20 = "{ \"name\": \"Resource Management - All\", \"actionId\": 5, \"resourceId\": 4 }";

        makePostRequest(permissionUrl,"permission16",accessToken,permission16);
        makePostRequest(permissionUrl,"permission17",accessToken,permission17);
        makePostRequest(permissionUrl,"permission18",accessToken,permission18);
        makePostRequest(permissionUrl,"permission19",accessToken,permission19);
        makePostRequest(permissionUrl,"permission20",accessToken,permission20);

        // Action regarding Resource
        String permission21 = "{ \"name\": \"Action Management - Create\", \"actionId\": 1, \"resourceId\": 5 }";
        String permission22 = "{ \"name\": \"Action Management - Read\", \"actionId\": 2, \"resourceId\": 5 }";
        String permission23 = "{ \"name\": \"Action Management - Update\", \"actionId\": 3, \"resourceId\": 5 }";
        String permission24 = "{ \"name\": \"Action Management - Delete\", \"actionId\": 4, \"resourceId\": 5 }";
        String permission25 = "{ \"name\": \"Action Management - All\", \"actionId\": 5, \"resourceId\": 5 }";

        makePostRequest(permissionUrl,"permission21",accessToken,permission21);
        makePostRequest(permissionUrl,"permission22",accessToken,permission22);
        makePostRequest(permissionUrl,"permission23",accessToken,permission23);
        makePostRequest(permissionUrl,"permission24",accessToken,permission24);
        makePostRequest(permissionUrl,"permission25",accessToken,permission25);

        // Tenant regarding Resource
        String permission26 = "{ \"name\": \"Tenant Management - Create\", \"actionId\": 1, \"resourceId\": 6 }";
        String permission27 = "{ \"name\": \"Tenant Management - Read\", \"actionId\": 2, \"resourceId\": 6 }";
        String permission28 = "{ \"name\": \"Tenant Management - Update\", \"actionId\": 3, \"resourceId\": 6 }";
        String permission29 = "{ \"name\": \"Tenant Management - Delete\", \"actionId\": 4, \"resourceId\": 6 }";
        String permission30 = "{ \"name\": \"Tenant Management - All\", \"actionId\": 5, \"resourceId\": 6 }";

        makePostRequest(permissionUrl,"permission26",accessToken,permission26);
        makePostRequest(permissionUrl,"permission27",accessToken,permission27);
        makePostRequest(permissionUrl,"permission28",accessToken,permission28);
        makePostRequest(permissionUrl,"permission29",accessToken,permission29);
        makePostRequest(permissionUrl,"permission30",accessToken,permission30);

        // Tenant Role regarding Resource
        String permission31 = "{ \"name\": \"Tenant Role Management - Create\", \"actionId\": 1, \"resourceId\": 7 }";
        String permission32 = "{ \"name\": \"Tenant Role Management - Read\", \"actionId\": 2, \"resourceId\": 7 }";
        String permission33 = "{ \"name\": \"Tenant Role Management - Update\", \"actionId\": 3, \"resourceId\": 7 }";
        String permission34 = "{ \"name\": \"Tenant Role Management - Delete\", \"actionId\": 4, \"resourceId\": 7 }";
        String permission35 = "{ \"name\": \"Tenant Role Management - All\", \"actionId\": 5, \"resourceId\": 7 }";

        makePostRequest(permissionUrl,"permission31",accessToken,permission31);
        makePostRequest(permissionUrl,"permission32",accessToken,permission32);
        makePostRequest(permissionUrl,"permission33",accessToken,permission33);
        makePostRequest(permissionUrl,"permission34",accessToken,permission34);
        makePostRequest(permissionUrl,"permission35",accessToken,permission35);

        // Tenant Role Permission regarding Resource
        String permission36 = "{ \"name\": \"Tenant Role Permission Management - Create\", \"actionId\": 1, \"resourceId\": 8 }";
        String permission37 = "{ \"name\": \"Tenant Role Permission Management - Read\", \"actionId\": 2, \"resourceId\": 8 }";
        String permission38 = "{ \"name\": \"Tenant Role Permission Management - Update\", \"actionId\": 3, \"resourceId\": 8 }";
        String permission39 = "{ \"name\": \"Tenant Role Permission Management - Delete\", \"actionId\": 4, \"resourceId\": 8 }";
        String permission40 = "{ \"name\": \"Tenant Role Permission Management - All\", \"actionId\": 5, \"resourceId\": 8 }";

        makePostRequest(permissionUrl,"permission36",accessToken,permission36);
        makePostRequest(permissionUrl,"permission37",accessToken,permission37);
        makePostRequest(permissionUrl,"permission38",accessToken,permission38);
        makePostRequest(permissionUrl,"permission39",accessToken,permission39);
        makePostRequest(permissionUrl,"permission40",accessToken,permission40);

        // Tenant Role User regarding Resource
        String permission41 = "{ \"name\": \"Tenant Role User Management - Create\", \"actionId\": 1, \"resourceId\": 9 }";
        String permission42 = "{ \"name\": \"Tenant Role User Management - Read\", \"actionId\": 2, \"resourceId\": 9 }";
        String permission43 = "{ \"name\": \"Tenant Role User Management - Update\", \"actionId\": 3, \"resourceId\": 9 }";
        String permission44 = "{ \"name\": \"Tenant Role User Management - Delete\", \"actionId\": 4, \"resourceId\": 9 }";
        String permission45 = "{ \"name\": \"Tenant Role User Management - All\", \"actionId\": 5, \"resourceId\": 9 }";

        makePostRequest(permissionUrl,"permission41",accessToken,permission41);
        makePostRequest(permissionUrl,"permission42",accessToken,permission42);
        makePostRequest(permissionUrl,"permission43",accessToken,permission43);
        makePostRequest(permissionUrl,"permission44",accessToken,permission44);
        makePostRequest(permissionUrl,"permission45",accessToken,permission45);
    }

    private static void actionCreation(String accessToken) {
        /**
         * Create
         * Read
         * Update
         * Delete
         * All
         */
        System.out.println("Starting action...");
        String actionUrl= getPermissionManagementBaseURL() + "/action";
        String action1 ="{ \"name\": \"Create\" }";
        String action2 ="{ \"name\": \"Read\" }";
        String action3 ="{ \"name\": \"Update\" }";
        String action4 ="{ \"name\": \"Delete\" }";
        String action5 ="{ \"name\": \"All\" }";

        makePostRequest(actionUrl,"action1",accessToken,action1);
        makePostRequest(actionUrl,"action2",accessToken,action2);
        makePostRequest(actionUrl,"action3",accessToken,action3);
        makePostRequest(actionUrl,"action4",accessToken,action4);
        makePostRequest(actionUrl,"action5",accessToken,action5);
    }

    public static void checkResponse(HttpResponse response,String msg){
        if(!response.isSuccess()){
            System.exit(1);
            System.err.println(msg);
        }
    }

    public static void tenantCreation(String accessToken){
        String tenant1 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Root Tenant\", \"tenantType\": \"Root\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\"}";
        String tenant2 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Client Tenant\", \"tenantType\": \"Client\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"clientAddress\": \"Sophiestrasse 33\", \"clientZipCode\": \"38118\", \"clientCity\":\"Braunschweig\", \"clientCountry\":\"Germany\", \"clientPhoneNumber\":933876547, \"clientEmail\":\"email@email.com\", \"parentId\":1, \"clientId\":1}";
        String tenant3 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Sub Tenant\", \"tenantType\": \"Sub\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"parentId\":2, \"clientId\":2}";

        String tenantUrl= getTenantManagementBaseURL() + "/tenant";

        makePostRequest(tenantUrl,"tenant1",accessToken,tenant1);
        makePostRequest(tenantUrl,"tenant2",accessToken,tenant2);
        makePostRequest(tenantUrl,"tenant3",accessToken,tenant3);

    }

    public static HashMap makePostRequest(String url,String identifier, String accessToken, String body){
        HttpResponse<HashMap> response = Unirest.post(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(body).asObject(HashMap.class);

        //checkResponse(response,identifier);
        System.out.println(identifier + " " + response.getStatus());
        return response.getBody();
    }
}

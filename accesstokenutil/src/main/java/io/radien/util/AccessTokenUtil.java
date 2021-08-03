package io.radien.util;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AccessTokenUtil {

    private final static String host = "http://localhost";
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

        tentantCreation(accessToken);
        actionCreation(accessToken);
        resourceCreation(accessToken);
        permissionCreation(accessToken);
        roleCreation(accessToken);
        tenantRoleCreation(accessToken);
    }

    private static void tenantRoleCreation(String accessToken) {
        String tenantRoleUrl= host+":8083/rolemanagementservice/v1/tenantrole";
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":1, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole2",accessToken,"{\"tenantId\":2, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":3, \"roleId\":1}");

        String tenantRolePermissionUrl= host+":8083/rolemanagementservice/v1/tenantrolepermission";
        makePostRequest(tenantRolePermissionUrl,"assignPermission1",accessToken,"{\"tenantRoleId\":1, \"permissionId\":1}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission2",accessToken,"{\"tenantRoleId\":1, \"permissionId\":2}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission3",accessToken,"{\"tenantRoleId\":1, \"permissionId\":3}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission4",accessToken,"{\"tenantRoleId\":1, \"permissionId\":4}");

        makePostRequest(tenantRolePermissionUrl,"assignPermission5",accessToken,"{\"tenantRoleId\":2, \"permissionId\":4}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission6",accessToken,"{\"tenantRoleId\":3, \"permissionId\":4}");

        makePostRequest(tenantRolePermissionUrl,"assignPermission8",accessToken,"{\"tenantRoleId\":2, \"permissionId\":1}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission9",accessToken,"{\"tenantRoleId\":3, \"permissionId\":1}");
    }

    private static void roleCreation(String accessToken) {
        System.out.println("Starting roles...");
        String roleUrl= host + ":8083/rolemanagementservice/v1/role";
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
        String resourceUrl= host +":8085/permissionmanagementservice/v1/resource";
        String user = "{ \"name\": \"User Management\" }";
        String permission = "{ \"name\": \"Permission Management\" }";
        String role = "{ \"name\": \"Role Management\" }";
        String tenant = "{ \"name\": \"Tenant Management\" }";

        makePostRequest(resourceUrl,"resource user",accessToken,user);
        makePostRequest(resourceUrl,"resource permission",accessToken,permission);
        makePostRequest(resourceUrl,"resource role",accessToken,role);
        makePostRequest(resourceUrl,"resource tenant",accessToken,tenant);
    }

    private static void permissionCreation(String accessToken){
        System.out.println("Start permission...");
        String permissionUrl = host + ":8085/permissionmanagementservice/v1/permission";
        String permission1 = "{ \"name\": \"User Management - Create\", \"actionId\": 1, \"resourceId\": 1 }";
        String permission2 = "{ \"name\": \"User Management - Read\", \"actionId\": 2, \"resourceId\": 1 }";
        String permission3 = "{ \"name\": \"User Management - Update\", \"actionId\": 3, \"resourceId\": 1 }";
        String permission4 = "{ \"name\": \"User Management - Delete\", \"actionId\": 4, \"resourceId\": 1 }";
        makePostRequest(permissionUrl,"permission1",accessToken,permission1);
        makePostRequest(permissionUrl,"permission2",accessToken,permission2);
        makePostRequest(permissionUrl,"permission3",accessToken,permission3);
        makePostRequest(permissionUrl,"permission4",accessToken,permission4);
    }

    private static void actionCreation(String accessToken) {
        System.out.println("Starting action...");
        String actionUrl= host +":8085/permissionmanagementservice/v1/action";
        String action1 ="{ \"name\": \"Create\" }";
        String action2 ="{ \"name\": \"Read\" }";
        String action3 ="{ \"name\": \"Update\" }";
        String action4 ="{ \"name\": \"Delete\" }";
        makePostRequest(actionUrl,"action1",accessToken,action1);
        makePostRequest(actionUrl,"action2",accessToken,action2);
        makePostRequest(actionUrl,"action3",accessToken,action3);
        makePostRequest(actionUrl,"action4",accessToken,action4);

    }

    public static void checkResponse(HttpResponse response,String msg){
        if(!response.isSuccess()){
            System.exit(1);
            System.err.println(msg);
        }
    }

    public static void tentantCreation(String accessToken){
        String tenant1 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Root Tenant\", \"tenantType\": \"Root\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\"}";
        String tenant2 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Client Tenant\", \"tenantType\": \"Client\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"clientAddress\": \"Sophiestrasse 33\", \"clientZipCode\": \"38118\", \"clientCity\":\"Braunschweig\", \"clientCountry\":\"Germany\", \"clientPhoneNumber\":933876547, \"clientEmail\":\"email@email.com\", \"parentId\":1, \"clientId\":1}";
        String tenant3 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Sub Tenant\", \"tenantType\": \"Sub\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"parentId\":2, \"clientId\":2}";

        String tenantUrl= host +":8082/tenantmanagementservice/v1/tenant";

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

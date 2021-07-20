package io.radien.util;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.HashMap;

public class AccessTokenUtil {

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




        String tenant1 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Root Tenant\", \"tenantType\": \"Root\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\"}";
        String tenant2 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Client Tenant\", \"tenantType\": \"Client\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"clientAddress\": \"Sophiestrasse 33\", \"clientZipCode\": \"38118\", \"clientCity\":\"Braunschweig\", \"clientCountry\":\"Germany\", \"clientPhoneNumber\":933876547, \"clientEmail\":\"email@email.com\", \"parentId\":1, \"clientId\":1}";
        String tenant3 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Sub Tenant\", \"tenantType\": \"Sub\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"parentId\":2, \"clientId\":2}";

        String tenantUrl= "http://localhost:8082/tenantmanagementservice/v1/tenant";
        response = Unirest.post(tenantUrl)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(tenant1).asObject(HashMap.class);
        String msg = "tentant1";
        //checkResponse(response,msg);
        System.out.println(msg + " " + response.getStatus());

        response = Unirest.post(tenantUrl)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(tenant2).asObject(HashMap.class);
        msg = "tentant2";
        //checkResponse(response,msg);
        System.out.println(msg + " " + response.getStatus());

        response = Unirest.post(tenantUrl)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(tenant3).asObject(HashMap.class);
        msg = "tentant3";
        //checkResponse(response,msg);
        System.out.println(msg + " " + response.getStatus());

    }

    public static void checkResponse(HttpResponse response,String msg){
        if(!response.isSuccess()){
            System.exit(1);
            System.err.println(msg);
        }
    }
}

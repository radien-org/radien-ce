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
        System.out.println(response.getBody().get("access_token"));

    }
}

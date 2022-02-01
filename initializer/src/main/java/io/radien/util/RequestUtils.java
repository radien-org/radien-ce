package io.radien.util;

import kong.unirest.HttpResponse;
import kong.unirest.UnirestParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RequestUtils {
    /**
     * Method that checks response and print status
     * @param response response to be checked
     * @param identifier to be print
     */

    public static final String STATUS = "{} Status:{} Success:{}";
    private static final Logger log
            = LoggerFactory.getLogger(Initializer.class);

    public static void checkResponse(HttpResponse response, String identifier){
        if(!response.isSuccess()){
            log.error(identifier);
            if(response.getBody()!=null) {
                String msg = response.getBody().toString();
                log.error(msg);
            }
            Optional<UnirestParsingException> parsingError = response.getParsingError();
            if(parsingError.isPresent()){

                log.error("originalBody: {}" , parsingError.get().getOriginalBody());
                log.error("Message: {}" , parsingError.get().getMessage());
            }
            System.exit(1);
        }
    }

    public static void logProgress(String identifier, HttpResponse response){
        log.info(STATUS,identifier, response.getStatus(), response.isSuccess());
    }
}

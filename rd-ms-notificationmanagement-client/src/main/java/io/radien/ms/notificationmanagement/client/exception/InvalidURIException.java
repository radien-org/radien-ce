package io.radien.ms.notificationmanagement.client.exception;

public class InvalidURIException extends RuntimeException{

    public InvalidURIException(){
        super();
    }

    public InvalidURIException(String text){
        super(text);
    }
}

package io.radien.ms.notificationmanagement.client.exception;

public class JMSGenericException extends RuntimeException{

    public JMSGenericException(){
        super();
    }

    public JMSGenericException(String text){
        super(text);
    }
}

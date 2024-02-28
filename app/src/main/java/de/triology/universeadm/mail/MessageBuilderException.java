package de.triology.universeadm.mail;

public class MessageBuilderException extends Exception {
    public MessageBuilderException(String errorMsg, Throwable cause){
        super(errorMsg, cause);
    }
}

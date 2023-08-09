package de.triology.universeadm.user.imports;

/**
 * BadArgumentException is an exception thrown when invalid input values are provided.
 * It provides a public error message to avoid giving out internal processing information.
 */
public class BadArgumentException extends Exception{

    private final String publicErrMsg;

    public BadArgumentException(String errorMessage) {
        super(errorMessage);
        this.publicErrMsg = "";
    }

    public BadArgumentException(String publicErrMsg, String errorMessage) {
        super(errorMessage);
        this.publicErrMsg = publicErrMsg;
    }

    public BadArgumentException(String errorMessage, Throwable t) {
        super(errorMessage, t);
        this.publicErrMsg = "";
    }

    public BadArgumentException(String publicErrMsg, String errorMessage, Throwable t){
        super(errorMessage, t);
        this.publicErrMsg = publicErrMsg;
    }

    public String getPublicErrMsg() {
        return publicErrMsg;
    }
}



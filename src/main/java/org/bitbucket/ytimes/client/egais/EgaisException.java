package org.bitbucket.ytimes.client.egais;

public class EgaisException extends Exception {

    public EgaisException(String message) {
        super(message);
    }

    public EgaisException(String message, Throwable cause) {
        super(message, cause);
    }

    public EgaisException(Throwable cause) {
        super(cause);
    }
}

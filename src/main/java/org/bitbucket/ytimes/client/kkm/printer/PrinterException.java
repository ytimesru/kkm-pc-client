package org.bitbucket.ytimes.client.kkm.printer;

/**
 * Created by root on 27.05.17.
 */
public class PrinterException extends Exception {

    private int code;

    public PrinterException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

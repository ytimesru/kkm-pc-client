package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.PrinterType;

/**
 * Created by root on 27.05.17.
 */
public interface Printer {

    boolean isConnected() throws PrinterException;

    void reportZ() throws PrinterException;

    void reportX() throws PrinterException;

    void startShift() throws PrinterException;

    void cashIncome(Integer summ) throws PrinterException;

    void printCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printPredCheck(PrintCheckCommandRecord record) throws PrinterException;

    void destroy() throws Throwable;

}

package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.NewGuestCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;

/**
 * Created by root on 27.05.17.
 */
public interface Printer {

    void reportZ() throws PrinterException;

    void reportX() throws PrinterException;

    void printCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printPredCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printNewGuest(NewGuestCommandRecord record) throws PrinterException ;

}

package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.*;

/**
 * Created by root on 27.05.17.
 */
public interface Printer {

    boolean isConnected() throws PrinterException;

    ModelInfoRecord getInfo() throws PrinterException;

    void reportZ(AbstractCommandRecord record) throws PrinterException;

    void reportX(ReportCommandRecord record) throws PrinterException;

    void startShift(ReportCommandRecord record) throws PrinterException;

    void cashIncome(CashIncomeRecord record) throws PrinterException;

    void copyLastDoc(AbstractCommandRecord record) throws PrinterException;

    void demoReport(AbstractCommandRecord record) throws PrinterException;

    void ofdTestReport(AbstractCommandRecord record) throws PrinterException;

    void printCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException;

    void printPredCheck(PrintCheckCommandRecord record) throws PrinterException;

    void connect() throws PrinterException;

    void destroy() throws Throwable;

}

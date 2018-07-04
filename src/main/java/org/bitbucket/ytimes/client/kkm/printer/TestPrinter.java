package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrey on 28.07.17.
 */
public class TestPrinter implements Printer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public TestPrinter() {
        logger.info("init test printer");
    }

    public boolean isConnected() {
        return true;
    }

    public ModelInfoRecord getInfo() throws PrinterException {
        return new ModelInfoRecord();
    }

    public void reportX(ReportCommandRecord record) throws PrinterException {
        logger.info("report x");
    }

    public void reportZ(AbstractCommandRecord record) throws PrinterException {
        logger.info("report z");
    }

    public void copyLastDoc(AbstractCommandRecord record) throws PrinterException {
        logger.info("report copy last doc");
    }

    public void demoReport(AbstractCommandRecord record) throws PrinterException {
        logger.info("report demo");
    }

    public void ofdTestReport(AbstractCommandRecord record) throws PrinterException {
        logger.info("report odf test");
    }

    public void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        logger.info("print check");
    }

    public void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        logger.info("print return check");
    }

    public void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        logger.info("print pred check");
    }

    public void cashIncome(CashChangeRecord summ) throws PrinterException {
        logger.info("cash income: " + summ.sum);
    }

    public void cashOutcome(CashChangeRecord summ) throws PrinterException {
        logger.info("cash outcome: " + summ.sum);
    }

    public void startShift(ReportCommandRecord record) throws PrinterException {
        logger.info("start shift");
    }

    public void connect() throws PrinterException {

    }

    public void destroy() throws PrinterException {
    }
}

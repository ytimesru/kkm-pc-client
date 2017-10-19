package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.NewGuestCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrey on 28.07.17.
 */
public class TestPrinter implements Printer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public void reportZ() throws PrinterException {
        logger.info("report z");
    }

    public void reportX() throws PrinterException {
        logger.info("report x");
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

    public void printNewGuest(NewGuestCommandRecord record) throws PrinterException {
        logger.info("print new guest");
    }

    public void cashIncome(Integer summ) throws PrinterException {
        logger.info("cash income");
    }

    public void startShift() throws PrinterException {
        logger.info("start shift");
    }

}

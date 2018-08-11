package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.AbstractCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.CashChangeRecord;
import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.ReportCommandRecord;

//without permanent connection
public class AtolPrinterEx extends AtolPrinter {

    public AtolPrinterEx(String model, String port, String wifiIP, Integer wifiPort) throws PrinterException {
        super(model, port, wifiIP, wifiPort);
    }

    @Override
    public synchronized boolean isConnected() throws PrinterException {
        connect();
        try {
            return true;
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void applySettingsAndConnect() throws PrinterException {
        super.applySettingsAndConnect();
        disconnect();
    }

    @Override
    public synchronized void reportX(ReportCommandRecord record) throws PrinterException {
        connect();
        try {
            super.reportX(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void reportZ(AbstractCommandRecord record) throws PrinterException {
        connect();
        try {
            super.reportZ(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void startShift(ReportCommandRecord record) throws PrinterException {
        connect();
        try {
            super.startShift(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void cashIncome(CashChangeRecord record) throws PrinterException {
        connect();
        try {
            super.cashIncome(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void cashOutcome(CashChangeRecord record) throws PrinterException {
        connect();
        try {
            super.cashOutcome(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void copyLastDoc(AbstractCommandRecord record) throws PrinterException {
        connect();
        try {
            super.copyLastDoc(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void demoReport(AbstractCommandRecord record) throws PrinterException {
        connect();
        try {
            super.demoReport(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void ofdTestReport(AbstractCommandRecord record) throws PrinterException {
        connect();
        try {
            super.ofdTestReport(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        connect();
        try {
            super.printPredCheck(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        connect();
        try {
            super.printCheck(record);
        }
        finally {
            disconnect();
        }
    }

    @Override
    public synchronized void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        connect();
        try {
            super.printReturnCheck(record);
        }
        finally {
            disconnect();
        }
    }

}

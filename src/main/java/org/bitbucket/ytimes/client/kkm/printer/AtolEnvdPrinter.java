package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.kkm.record.*;
import org.bitbucket.ytimes.client.main.StringUtils;
import ru.atol.drivers10.fptr.IFptr;

import java.math.BigDecimal;

public class AtolEnvdPrinter extends AtolPrinter {

    public AtolEnvdPrinter(String model, String port, String wifiIP, Integer wifiPort) throws PrinterException {
        super(model, port, wifiIP, wifiPort);
    }

    @Override
    public synchronized void reportX(ReportCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void reportZ(AbstractCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void startShift(ReportCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void cashIncome(CashChangeRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void cashOutcome(CashChangeRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void copyLastDoc(AbstractCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void demoReport(AbstractCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void ofdTestReport(AbstractCommandRecord record) throws PrinterException {
        throw new PrinterException(0, "Не поддерживается в данном принтере");
    }

    @Override
    public synchronized void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        super.printPredCheck(record);
    }

    @Override
    public synchronized void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        printDoc(record, "ПРОДАЖА");
    }

    @Override
    public synchronized void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        printDoc(record, "ВОЗВРАТ");
    }

    private void printDoc(PrintCheckCommandRecord record, String title) throws PrinterException {
        checkRecord(record);
        if (fptr.beginNonfiscalDocument() < 0) {
            checkError(fptr);
        }

        printText("");
        printText(StringUtils.twoColumn(title, "#" + record.checkNum, 32));
        printText(StringUtils.twoColumn("Кассир", record.userFIO, 32));
        printText("");

        BigDecimal totalSum = new BigDecimal(0.0);
        for(int i = 0; i < record.itemList.size(); i++) {
            ItemRecord r = record.itemList.get(i);
            printText((i + 1) + ". " + r.name, IFptr.LIBFPTR_ALIGNMENT_LEFT, IFptr.LIBFPTR_TW_WORDS);

            BigDecimal total = new BigDecimal(r.price).multiply(new BigDecimal(r.quantity));
            printText(r.price + " x " + r.quantity + " = " + total, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);

            if (r.discountSum != null && r.discountSum > 0) {
                printText("Скидка: " + r.discountSum, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
            }
            if (r.discountPercent != null && r.discountPercent > 0) {
                printText("Скидка: " + r.discountPercent + "%", IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
            }

            totalSum = totalSum.add(total);
        }
        printBoldText("ИТОГО: " + totalSum, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
        printText("");

        if (fptr.endNonfiscalDocument() < 0) {
            checkError(fptr);
        }
    }

}

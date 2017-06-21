package org.bitbucket.ytimes.client.kkm.printer;

import com.atol.drivers.fptr.Fptr;
import com.atol.drivers.fptr.IFptr;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Created by root on 27.05.17.
 */
public class AtolPrinter implements Printer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private IFptr fptr;

    @Value("${printer.atol.SETTING_PORT}")
    private String port;

    @Value("${printer.atol.ip}")
    private String wifiIP;

    @Value("${printer.atol.port}")
    private Integer wifiPort;

    @Value("${printer.atol.SETTING_VID}")
    private String vid;

    @Value("${printer.atol.SETTING_PID}")
    private String pid;

    @Value("${printer.atol.SETTING_PROTOCOL}")
    private int protocol;

    @Value("${printer.atol.SETTING_MODEL}")
    private int model;

    @Value("${printer.atol.SETTING_ACCESSPASSWORD}")
    private int accessPassword;

    @Value("${printer.atol.SETTING_USERPASSWORD}")
    private int userPassword;

    @Value("${printer.atol.SETTING_BAUDRATE}")
    private int baudrate;

    @PostConstruct
    public void init() throws Exception {
        logger.info("START ATOL PRINTER");
        logger.info("PORT: " + port);

        fptr = new Fptr();

        fptr.create();
        fptr.put_DeviceSingleSetting(fptr.SETTING_SEARCHDIR, System.getProperty("java.library.path"));
        fptr.ApplySingleSettings();

        try {
            int i = Integer.parseInt(port);
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PORT, i) < 0)
                checkError(fptr);
        }
        catch (NumberFormatException e) {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PORT, port) < 0)
                checkError(fptr);
        }

        if ("TCPIP".equals(port)) {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_IPADDRESS, wifiIP) < 0)
                checkError(fptr);
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_IPPORT, wifiPort) < 0)
                checkError(fptr);
        }

        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PROTOCOL, protocol) < 0)
            checkError(fptr);
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_MODEL, model) < 0)
            checkError(fptr);
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_ACCESSPASSWORD, accessPassword) < 0)
            checkError(fptr);
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, userPassword) < 0)
            checkError(fptr);
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_BAUDRATE, baudrate) < 0)
            checkError(fptr);
        if (!StringUtils.isEmpty(vid)) {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_VID, Integer.parseInt(vid)) < 0)
                checkError(fptr);
        }
        if (!StringUtils.isEmpty(pid)) {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PID, Integer.parseInt(pid)) < 0)
                checkError(fptr);
        }

        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);

        // Подключаемся к устройству
        if (fptr.put_DeviceEnabled(true) < 0)
            checkError(fptr);

        // Проверка связи
        if (fptr.GetStatus() < 0)
            checkError(fptr);

        cancelCheck();
        logger.info("ATOL PRINTER STARTED");
    }

    @Override
    protected void finalize() throws Throwable {
        logger.info("ATOL PRINTER DESTROY");
        fptr.destroy();
    }

    private void cancelCheck() throws PrinterException {
        // Отменяем чек, если уже открыт. Ошибки "Неверный режим" и "Чек уже закрыт"
        // не являются ошибками, если мы хотим просто отменить чек
        try {
            if (fptr.CancelCheck() < 0)
                checkError(fptr, false);
        } catch (PrinterException e) {
            int rc = fptr.get_ResultCode();
            if (rc != -16 && rc != -3801)
                throw e;
        }
    }

    public void reportX() throws PrinterException {
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, 30) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);
        if (fptr.put_Mode(IFptr.MODE_REPORT_NO_CLEAR) < 0)
            checkError(fptr);
        if (fptr.SetMode() < 0)
            checkError(fptr);
        if (fptr.put_ReportType(IFptr.REPORT_X) < 0)
            checkError(fptr);
        if (fptr.Report() < 0)
            checkError(fptr);
    }

    public void reportZ() throws PrinterException {
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, 30) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);
        if (fptr.put_Mode(IFptr.MODE_REPORT_CLEAR) < 0)
            checkError(fptr);
        if (fptr.SetMode() < 0)
            checkError(fptr);
        if (fptr.put_ReportType(IFptr.REPORT_Z) < 0)
            checkError(fptr);
        if (fptr.Report() < 0)
            checkError(fptr);
    }

    //выставление счета
    public void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);

        printText("ПРЕДЧЕК");
        printText("");
        printText("ПОЗИЦИИ ОПЛАТЫ", IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
        for(int i = 0; i < record.itemList.size(); i++) {
            ItemRecord r = record.itemList.get(i);
            printText((i + 1) + ". " + r.name, IFptr.ALIGNMENT_LEFT, IFptr.WRAP_WORD);

            double total = r.price * r.quantity;
            printText(r.price + " x " + r.quantity + " = " + total, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);

            if (r.discountSum != null && r.discountSum > 0) {
                printText("Скидка: " + r.discountSum, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
            }
            if (r.discountPercent != null && r.discountPercent > 0) {
                printText("Скидка: " + r.discountPercent + "%", IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
            }
        }
        printText("ИТОГО", IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
        printText("" + record.moneySum, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);

        printText("");
        printText("");
        printText("");

        if (GuestType.TIME.equals(record.type) && record.guestInfoList != null) {
            printText("РАССЧИТЫВАЕМЫЕ ГОСТИ", IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
            int i = 1;
            for(GuestRecord r: record.guestInfoList) {
                String name = r.name;
                if (!StringUtils.isEmpty(r.card)) {
                    name += " (" + r.card + ")";
                };
                printText(i + ". " + name, IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
                printText("время прихода:" + r.startTime, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
                printText("время проведенное время:" + r.minutes, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
                i++;
            }
        }
        if (GuestType.TOGO.equals(record.type)  && record.guestInfoList != null) {
            for(GuestRecord r: record.guestInfoList) {
                String name = r.name;
                if (!StringUtils.isEmpty(r.phone)) {
                    name += ", " + r.phone;
                }
                printText(name);
                printText(r.message);
            }
        }

        printFooter();
    }

    public void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);

        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, userPassword) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);

        cancelCheck();

        // Открываем чек продажи, попутно обработав превышение смены
        try {
            openCheck(IFptr.CHEQUE_TYPE_SELL);
        } catch (PrinterException e) {
            // Проверка на превышение смены
            if (fptr.get_ResultCode() == -3822) {
                reportZ();
                openCheck(IFptr.CHEQUE_TYPE_SELL);
            } else {
                throw e;
            }
        }


        for(ItemRecord r: record.itemList) {
            int discountType = IFptr.DISCOUNT_SUMM;
            double discountSum = r.discountSum != null ? r.discountSum : 0.0;

            if (r.discountPercent != null) {
                discountType = IFptr.DISCOUNT_PERCENT;
                discountSum = r.discountPercent;
            }

            int tax = r.taxNumber != null ? r.taxNumber : 1;

            registrationFZ54(r.name, r.price, r.quantity, discountType, discountSum, tax);
        }

        // Скидка на чек
        //discount(1, IFptr.DISCOUNT_PERCENT, IFptr.DESTINATION_CHECK);

        if (record.creditSum != null && record.creditSum > 0) {
            payment(record.creditSum, 1);   //1 по карте
        }

        if (record.moneySum != null && record.moneySum > 0) {
            payment(record.moneySum, 0);
        }

        // Закрываем чек
        closeCheck(0);
    }

    private void checkRecord(PrintCheckCommandRecord record) throws PrinterException {
        if (record.itemList == null || record.itemList.isEmpty()) {
            throw new PrinterException("Список оплаты пустой");
        }
        if (record.moneySum == null && record.creditSum == null) {
            throw new PrinterException("Итоговое значение для оплаты не задано");
        }
        if (record.moneySum != null && record.moneySum == 0.0 &&
            record.creditSum != null && record.creditSum == 0.0) {
            throw new PrinterException("Итоговое значение для оплаты не задано");
        }
        for(ItemRecord r: record.itemList) {
            if (StringUtils.isEmpty(r.name)) {
                throw new PrinterException("Не задано наименование позиции");
            }
            if (r.price == null) {
                throw new PrinterException("Не задана цена позиции: " + r.name);
            }
            if (r.quantity == null) {
                throw new PrinterException("Не задано количество позиции: " + r.name);
            }

            if (r.discountPercent != null && r.discountSum != null) {
                throw new PrinterException("Нужно задать только один тип скидки - либо в процентах, либо в сумме. Позиция: " + r.name);
            }
        }
    }

    public void printNewGuest(NewGuestCommandRecord record) throws PrinterException {
        printText(record.name);
        printText(record.startTime);
        if (!StringUtils.isEmpty(record.barcodeNum)) {
            printBarcode(IFptr.BARCODE_TYPE_CODE39, record.barcodeNum, 100);
        }
        printText("");
        printText("");
        printText("");
    }

    private void registrationFZ54(String name, double price, double quantity, int discountType,
                                         double discount, int taxNumber) throws PrinterException {
        if (fptr.put_DiscountType(discountType) < 0)
            checkError(fptr);
        if (fptr.put_Summ(discount) < 0)
            checkError(fptr);
        if (fptr.put_TaxNumber(taxNumber) < 0)
            checkError(fptr);
        if (fptr.put_Quantity(quantity) < 0)
            checkError(fptr);
        if (fptr.put_Price(price) < 0)
            checkError(fptr);
        if (fptr.put_TextWrap(IFptr.WRAP_WORD) < 0)
            checkError(fptr);
        if (fptr.put_Name(name) < 0)
            checkError(fptr);
        if (fptr.Registration() < 0)
            checkError(fptr);
    }

    private void payment(double sum, int type) throws PrinterException {
        if (fptr.put_Summ(sum) < 0)
            checkError(fptr);
        if (fptr.put_TypeClose(type) < 0)
            checkError(fptr);
        if (fptr.Payment() < 0)
            checkError(fptr);
        System.out.println(String.format("Remainder: %.2f, Change: %.2f", fptr.get_Remainder(), fptr.get_Change()));
    }

    private void printText(String text, int alignment, int wrap) throws PrinterException {
        if (fptr.put_Caption(text) < 0)
            checkError(fptr);
        if (fptr.put_TextWrap(wrap) < 0)
            checkError(fptr);
        if (fptr.put_Alignment(alignment) < 0)
            checkError(fptr);
        if (fptr.PrintString() < 0)
            checkError(fptr);
    }

    private void printText(String text) throws PrinterException {
        printText(text, IFptr.ALIGNMENT_CENTER, IFptr.WRAP_LINE);
    }

    private void openCheck(int type) throws PrinterException {
        if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0)
            checkError(fptr);
        if (fptr.SetMode() < 0)
            checkError(fptr);
        if (fptr.put_CheckType(type) < 0)
            checkError(fptr);
        if (fptr.OpenCheck() < 0)
            checkError(fptr);
    }

    private void closeCheck(int typeClose) throws PrinterException {
        if (fptr.put_TypeClose(typeClose) < 0)
            checkError(fptr);
        if (fptr.CloseCheck() < 0)
            checkError(fptr);
    }

    private void printFooter() throws PrinterException {
        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, 30) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);
        if (fptr.put_Mode(IFptr.MODE_REPORT_NO_CLEAR) < 0)
            checkError(fptr);
        if (fptr.SetMode() < 0)
            checkError(fptr);
        if (fptr.PrintFooter() < 0)
            checkError(fptr);
    }

    private void printBarcode(int type, String barcode, double scale) throws PrinterException {
        if (fptr.put_Alignment(IFptr.ALIGNMENT_CENTER) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeType(type) < 0)
            checkError(fptr);
        if (fptr.put_Barcode(barcode) < 0)
            checkError(fptr);
        if (fptr.put_Height(0) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeVersion(0) < 0)
            checkError(fptr);
        if (fptr.put_BarcodePrintType(IFptr.BARCODE_PRINTTYPE_AUTO) < 0)
            checkError(fptr);
        if (fptr.put_PrintBarcodeText(false) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeControlCode(true) < 0)
            checkError(fptr);
        if (fptr.put_Scale(scale) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeCorrection(0) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeColumns(3) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeRows(1) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeProportions(50) < 0)
            checkError(fptr);
        if (fptr.put_BarcodeUseProportions(true) < 0)
            checkError(fptr);
        if (fptr.put_BarcodePackingMode(IFptr.BARCODE_PDF417_PACKING_MODE_DEFAULT) < 0)
            checkError(fptr);
        if (fptr.put_BarcodePixelProportions(300) < 0)
            checkError(fptr);
        if (fptr.PrintBarcode() < 0)
            checkError(fptr);
    }

    private void checkError(IFptr fptr) throws PrinterException {
        checkError(fptr, false);
    }

    private void checkError(IFptr fptr, boolean log) throws PrinterException {
        int rc = fptr.get_ResultCode();
        if (rc < 0) {
            String rd = fptr.get_ResultDescription(), bpd = null;
            if (rc == -6) {
                bpd = fptr.get_BadParamDescription();
            }
            if (bpd != null) {
                String message = String.format("[%d] %s (%s)", rc, rd, bpd);
                if (log) {
                    logger.error(message);
                }
                throw new PrinterException(message);
            } else {
                String message = String.format("[%d] %s", rc, rd);
                if (log) {
                    logger.error(message);
                }
                throw new PrinterException(message);
            }
        }
    }

}

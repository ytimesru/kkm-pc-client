package org.bitbucket.ytimes.client.kkm.printer;

import com.atol.drivers.fptr.Fptr;
import com.atol.drivers.fptr.IFptr;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 27.05.17.
 */
public class AtolPrinter implements Printer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private IFptr fptr;
    private String port;
    private String wifiIP;
    private Integer wifiPort;
    private int model;
    private Map<String, Integer> modelList = new HashMap<String, Integer>();

    private String vid = "";
    private String pid = "";
    private int protocol = 2;
    private int accessPassword = 0;
    private int userPassword = 30;
    private int baudrate = 115200;

    public AtolPrinter(String model, String port, String wifiIP, Integer wifiPort) throws PrinterException {
        this.port = port;
        this.wifiIP = wifiIP;
        this.wifiPort = wifiPort;
        modelList.put("ATOL11F", 67);
        modelList.put("ATOL22F", 63);
        modelList.put("ATOL30F", 61);
        modelList.put("ATOL55F", 62);

        if (!modelList.containsKey(model)) {
            throw new PrinterException(0, "Модель пока не поддерживается");
        }

        this.model = modelList.get(model);
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public void setAccessPassword(int accessPassword) {
        this.accessPassword = accessPassword;
    }

    public void setUserPassword(int userPassword) {
        this.userPassword = userPassword;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public synchronized boolean isConnected() throws PrinterException {
        doConnect();
        try {
            // Проверка связи
            if (fptr.GetStatus() < 0) {
                checkError(fptr);
            }

            return true;
        }
        finally {
            doDisconnect();
        }
    }

    public void connect() throws PrinterException {
        if (fptr != null) {
            try {
                finalize();
            }
            catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("START ATOL PRINTER");
        logger.info("PORT: " + port);

        fptr = new Fptr();

        fptr.create();
        fptr.put_DeviceSingleSetting(fptr.SETTING_SEARCHDIR, System.getProperty("java.library.path"));
        fptr.ApplySingleSettings();

        try {
            int i = Integer.parseInt(port);
            logger.info("Connect to port: " + i);
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PORT, i) < 0)
                checkError(fptr);
        }
        catch (NumberFormatException e) {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_PORT, port) < 0)
                checkError(fptr);
        }

        if ("TCPIP".equals(port)) {
            logger.info("Connect to: " + wifiIP + ":" + wifiPort);
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

        doConnect();
        try {
            // Проверка связи
            if (fptr.GetStatus() < 0)
                checkError(fptr);

            cancelCheck();
        }
        finally {
            doDisconnect();
        }

        logger.info("ATOL PRINTER STARTED");
    }

    private void doConnect() throws PrinterException {
        if (fptr.put_DeviceEnabled(true) < 0) {
            checkError(fptr);
        }
    }

    private void doDisconnect() throws PrinterException {
        if (fptr.put_DeviceEnabled(false) < 0) {
            checkError(fptr);
        }
    }

    public void destroy() throws Throwable {
        finalize();
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

    synchronized public void reportX() throws PrinterException {
        doConnect();
        try {
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
        finally {
            doDisconnect();
        }
    }

    synchronized public void startShift() throws PrinterException {
        doConnect();
        try {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, 30) < 0)
                checkError(fptr);
            if (fptr.ApplySingleSettings() < 0)
                checkError(fptr);
            if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0)
                checkError(fptr);
            if (fptr.SetMode() < 0)
                checkError(fptr);
            if (fptr.OpenSession() < 0)
                checkError(fptr);
        }
        finally {
            doDisconnect();
        }
    }

    public void cashIncome(Integer sum) throws PrinterException {
        doConnect();
        try {
            if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, 30) < 0)
                checkError(fptr);
            if (fptr.ApplySingleSettings() < 0)
                checkError(fptr);
            if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0)
                checkError(fptr);
            if (fptr.SetMode() < 0)
                checkError(fptr);
            if (fptr.put_Summ(sum) < 0)
                checkError(fptr);
            if (fptr.CashIncome() < 0) {
                checkError(fptr);
            }
        }
        finally {
            doDisconnect();
        }
    }

    synchronized public void reportZ() throws PrinterException {
        doConnect();
        try {
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
        finally {
            doDisconnect();
        }
    }

    //выставление счета
    synchronized public void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        doConnect();
        try {
            doPrintPredCheck(record);
        }
        finally {
            doDisconnect();
        }
    }

    private void doPrintPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);

        printText("СЧЕТ");
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
        printBoldText("ИТОГО: " + record.moneySum, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);

        if (GuestType.TIME.equals(record.type) && record.guestInfoList != null) {
            printText("");
            printText("РАССЧИТЫВАЕМЫЕ ГОСТИ", IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
            int i = 1;
            for(GuestRecord r: record.guestInfoList) {
                String name = r.name;
                if (!StringUtils.isEmpty(r.card)) {
                    name += " (" + r.card + ")";
                };
                printText(i + ". " + name, IFptr.ALIGNMENT_LEFT, IFptr.WRAP_LINE);
                printText("время прихода: " + r.startTime, IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
                printText("проведенное время: " + r.minutes + " мин.", IFptr.ALIGNMENT_RIGHT, IFptr.WRAP_LINE);
                i++;
            }

            printText("");
            printText("");
        }

        if (GuestType.TOGO.equals(record.type)  && record.guestInfoList != null) {
            printText("");
            for(GuestRecord r: record.guestInfoList) {
                String name = r.name;
                if (!StringUtils.isEmpty(r.phone)) {
                    name += ", " + r.phone;
                }
                printText(name, IFptr.ALIGNMENT_CENTER, IFptr.WRAP_LINE);
                printText(r.message);
            }

            printText("");
            printText("");
        }

        if (record.additionalInfo != null) {
            printText("");
            for(String s: record.additionalInfo) {
                printText(s, IFptr.ALIGNMENT_CENTER, IFptr.WRAP_WORD);
            }
            printText("");
        }

        printHeader();
    }

    synchronized public void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        doConnect();
        try {
            doPrintCheck(record);
        }
        finally {
            doDisconnect();
        }
    }

    private void doPrintCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);

        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, userPassword) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);

        cancelCheck();

        if (record.userFIO != null && !record.userPosition.isEmpty()) {
            String fio = record.userFIO;
            if (record.userPosition != null && !record.userPosition.isEmpty()) {
                fio = record.userPosition + " " + fio;
            }
            setUserFIO(fio);
        }

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

        try {
            BigDecimal discount = new BigDecimal(0.0);
            for (ItemRecord r : record.itemList) {
                BigDecimal price = new BigDecimal(r.price);
                BigDecimal discountPosition = new BigDecimal(0.0);
                if (r.discountSum != null) {
                    discountPosition = new BigDecimal(r.discountSum);
                } else if (r.discountPercent != null) {
                    if (r.discountPercent > 100) {
                        r.discountPercent = 100.0;
                    }
                    BigDecimal value = new BigDecimal(r.price).multiply(new BigDecimal(r.quantity));
                    discountPosition = value.multiply(new BigDecimal(r.discountPercent)).divide(new BigDecimal(100.0));
                }
                discount = discount.add(discountPosition);

                BigDecimal priceWithDiscount = price.subtract(discountPosition);

                logger.info("Name: " + r.name + ", price=" + price + ", discount = " + discountPosition + ", priceWithDiscount = " + priceWithDiscount);

                int tax = r.taxNumber;
                BigDecimal positionSum = priceWithDiscount.multiply(new BigDecimal(r.quantity));
                registrationFZ54(r.name, priceWithDiscount.doubleValue(), r.quantity, positionSum.doubleValue(), tax);
            }

            // Скидка на чек
            logger.info("check discount: " + discount.doubleValue());
            //discount(0, IFptr.DISCOUNT_SUMM, IFptr.DESTINATION_CHECK);

            if (record.creditSum != null && record.creditSum > 0) {
                payment(record.creditSum, 1);   //1 по карте
            }

            if (record.moneySum != null && record.moneySum > 0) {
                payment(record.moneySum, 0);
            }

            if (record.phone != null && !record.phone.isEmpty()) {
                sendCheck(record.phone);
            } else if (record.email != null && !record.email.isEmpty()) {
                sendCheck(record.email);
            }

            // Закрываем чек
            closeCheck(0);
        }
        catch (PrinterException e) {
            cancelCheck();
            throw e;
        }
    }

    synchronized public void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        doConnect();
        try {
            doPrintReturnCheck(record);
        }
        finally {
            doDisconnect();
        }
    }

    private void doPrintReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);

        if (fptr.put_DeviceSingleSetting(IFptr.SETTING_USERPASSWORD, userPassword) < 0)
            checkError(fptr);
        if (fptr.ApplySingleSettings() < 0)
            checkError(fptr);

        cancelCheck();

        if (record.userFIO != null && !record.userPosition.isEmpty()) {
            String fio = record.userFIO;
            if (record.userPosition != null && !record.userPosition.isEmpty()) {
                fio = record.userPosition + " " + fio;
            }
            setUserFIO(fio);
        }

        // Открываем чек продажи, попутно обработав превышение смены
        try {
            openCheck(IFptr.CHEQUE_TYPE_RETURN);
        } catch (PrinterException e) {
            // Проверка на превышение смены
            if (fptr.get_ResultCode() == -3822) {
                reportZ();
                openCheck(IFptr.CHEQUE_TYPE_RETURN);
            } else {
                throw e;
            }
        }

        try {
            BigDecimal discount = new BigDecimal(0.0);
            for (ItemRecord r : record.itemList) {
                BigDecimal price = new BigDecimal(r.price);
                BigDecimal discountPosition = new BigDecimal(0.0);
                if (r.discountSum != null) {
                    discountPosition = new BigDecimal(r.discountSum);
                } else if (r.discountPercent != null) {
                    if (r.discountPercent > 100) {
                        r.discountPercent = 100.0;
                    }
                    BigDecimal value = new BigDecimal(r.price).multiply(new BigDecimal(r.quantity));
                    discountPosition = value.multiply(new BigDecimal(r.discountPercent)).divide(new BigDecimal(100.0));
                }
                discount = discount.add(discountPosition);

                BigDecimal priceWithDiscount = price.subtract(discountPosition);

                logger.info("Name: " + r.name + ", price=" + price + ", discount = " + discountPosition + ", priceWithDiscount = " + priceWithDiscount);

                int tax = r.taxNumber;

                BigDecimal positionSum = priceWithDiscount.multiply(new BigDecimal(r.quantity));
                registrationFZ54(r.name, priceWithDiscount.doubleValue(), r.quantity, positionSum.doubleValue(), tax);
            }

            if (record.creditSum != null && record.creditSum > 0) {
                payment(record.creditSum, 1);   //1 по карте
            }

            if (record.moneySum != null && record.moneySum > 0) {
                payment(record.moneySum, 0);
            }

            // Закрываем чек
            if (Boolean.TRUE.equals(record.testMode)) {
                cancelCheck();
                throw new PrinterException(0, "Тестовый режим. Чек отменен.");
            }
            else {
                closeCheck(0);
            }
        }
        catch (PrinterException e) {
            cancelCheck();
            throw e;
        }
    }

    private void sendCheck(String address) throws PrinterException {
        if (fptr.put_FiscalPropertyNumber(1008) < 0) {
            checkError(fptr);
        }
        if (fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_STRING) < 0) {
            checkError(fptr);
        }
        if (fptr.put_FiscalPropertyValue(address) < 0) {
            checkError(fptr);
        }
        if (fptr.WriteFiscalProperty() < 0) {
            checkError(fptr);
        }
    }

    private void setUserFIO(String fio) throws PrinterException {
        if (fptr.put_FiscalPropertyNumber(1021) < 0) {
            checkError(fptr);
        }
        if (fptr.put_FiscalPropertyType(IFptr.FISCAL_PROPERTY_TYPE_STRING) < 0) {
            checkError(fptr);
        }
        if (fptr.put_FiscalPropertyValue(fio) < 0) {
            checkError(fptr);
        }
        if (fptr.WriteFiscalProperty() < 0) {
            checkError(fptr);
        }
    }

    private void checkRecord(PrintCheckCommandRecord record) throws PrinterException {
        if (record.itemList == null || record.itemList.isEmpty()) {
            throw new PrinterException(0, "Список оплаты пустой");
        }
        if (record.moneySum == null && record.creditSum == null) {
            throw new PrinterException(0, "Итоговое значение для оплаты не задано");
        }
        if (record.moneySum != null && record.moneySum == 0.0 &&
            record.creditSum != null && record.creditSum == 0.0) {
            throw new PrinterException(0, "Итоговое значение для оплаты не задано");
        }
        for(ItemRecord r: record.itemList) {
            if (StringUtils.isEmpty(r.name)) {
                throw new PrinterException(0, "Не задано наименование позиции");
            }
            if (r.price == null) {
                throw new PrinterException(0, "Не задана цена позиции: " + r.name);
            }
            if (r.quantity == null) {
                throw new PrinterException(0, "Не задано количество позиции: " + r.name);
            }

            if (r.discountPercent != null && r.discountSum != null) {
                throw new PrinterException(0, "Нужно задать только один тип скидки - либо в процентах, либо в сумме. Позиция: " + r.name);
            }
        }
    }

    private void discount(double sum, int type, int destination) throws PrinterException {
        if (fptr.put_Summ(sum) < 0)
            checkError(fptr);
        if (fptr.put_DiscountType(type) < 0)
            checkError(fptr);
        if (fptr.put_Destination(destination) < 0)
            checkError(fptr);
        if (fptr.Discount() < 0)
            checkError(fptr);
    }

    private void registrationFZ54(String name, double price, double quantity, double positionSum, int taxNumber) throws PrinterException {
        if (fptr.put_DiscountType(IFptr.DISCOUNT_SUMM) < 0)
            checkError(fptr);
        if (fptr.put_Summ(0) < 0)
            checkError(fptr);


        if (fptr.put_PositionSum(positionSum) < 0)
            checkError(fptr);
        if (fptr.put_Quantity(quantity) < 0)
            checkError(fptr);
        if (fptr.put_Price(price) < 0)
            checkError(fptr);
        if (fptr.put_TaxNumber(taxNumber) < 0)
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
    }

    private void printText(String text, int alignment, int wrap) throws PrinterException {
        if (fptr.put_Caption(text) < 0)
            checkError(fptr);
        if (fptr.put_TextWrap(wrap) < 0)
            checkError(fptr);
        if (fptr.put_FontBold(false) < 0)
            checkError(fptr);
        if (fptr.put_Alignment(alignment) < 0)
            checkError(fptr);
        if (fptr.PrintString() < 0)
            checkError(fptr);
    }

    private void printBoldText(String text, int alignment, int wrap) throws PrinterException {
        if (fptr.put_Caption(text) < 0)
            checkError(fptr);
        if (fptr.put_TextWrap(wrap) < 0)
            checkError(fptr);
        if (fptr.put_FontBold(false) < 0)
            checkError(fptr);
        if (fptr.put_Alignment(alignment) < 0)
            checkError(fptr);
        if (fptr.put_FontDblHeight(true) < 0)
            checkError(fptr);
        if (fptr.put_FontDblWidth(true) < 0)
            checkError(fptr);
        if (fptr.put_FontBold(true) < 0)
            checkError(fptr);
        if (fptr.PrintFormattedText() < 0)
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

    private void printHeader() throws PrinterException {
        if (fptr.PrintHeader() < 0)
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
        checkError(fptr, true);
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
                throw new PrinterException(rc, message);
            } else {
                String message = String.format("[%d] %s", rc, rd);
                if (log) {
                    logger.error(message);
                }
                throw new PrinterException(rc, message);
            }
        }
    }

}

package org.bitbucket.ytimes.client.kkm.printer;

import org.bitbucket.ytimes.client.main.Utils;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import ru.atol.drivers10.fptr.Fptr;
import ru.atol.drivers10.fptr.IFptr;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 27.05.17.
 */
public class AtolPrinter implements Printer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected IFptr fptr;
    private String port;
    private String wifiIP;
    private Integer wifiPort;
    private int model;
    private Map<String, Integer> modelList = new HashMap<String, Integer>();

    private VAT vat = VAT.NO;
    private OFDChannel ofdChannel = null;

    public AtolPrinter(String model, String port, String wifiIP, Integer wifiPort) throws PrinterException {
        this.port = port;
        this.wifiIP = wifiIP;
        this.wifiPort = wifiPort;
        modelList.put("ATOLAUTO", 500);
        modelList.put("ATOLENVD", 500);
        modelList.put("ATOL11F", 67);
        modelList.put("ATOL15F", 78);
        modelList.put("ATOL20F", 81);
        modelList.put("ATOL22F", 63);
        modelList.put("ATOL25F", 57);
        modelList.put("ATOL30F", 61);
        modelList.put("ATOL50F", 80);
        modelList.put("ATOL55F", 62);
        modelList.put("ATOL90F", 72);
        modelList.put("ATOL91F", 82);

        if (!modelList.containsKey(model)) {
            throw new PrinterException(0, "Модель не поддерживается в данной версии коммуникационного модуля");
        }

        this.model = modelList.get(model);
    }

    public void setVat(VAT vat) {
        this.vat = vat;
    }

    public void setOfdChannel(OFDChannel ofdChannel) {
        this.ofdChannel = ofdChannel;
    }

    synchronized public boolean isConnected() throws PrinterException {
        return fptr.isOpened();
    }

    synchronized public ModelInfoRecord getInfo() throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_DATA_TYPE, IFptr.LIBFPTR_DT_STATUS);
        if (fptr.queryData() < 0) {
            checkError(fptr);
        }

        ModelInfoRecord record = new ModelInfoRecord();
        record.serialNumber    = fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER);
        record.modelName       = fptr.getParamString(IFptr.LIBFPTR_PARAM_MODEL_NAME);
        record.unitVersion     = fptr.getParamString(IFptr.LIBFPTR_PARAM_UNIT_VERSION);

        //ОФД
        fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_REG_INFO);
        if (fptr.fnQueryData() < 0) {
            checkError(fptr);
        }

        record.ofdName = fptr.getParamString(1046);


        fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_OFD_EXCHANGE_STATUS);
        if (fptr.fnQueryData() < 0) {
            checkError(fptr);
        }

        record.ofdUnsentCount    = fptr.getParamInt(IFptr.LIBFPTR_PARAM_DOCUMENTS_COUNT);
        Date unsentDateTime = fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME);
        if (unsentDateTime != null) {
            record.ofdUnsentDatetime = Utils.toDateString(unsentDateTime);
        }


        //ФФД
        fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FFD_VERSIONS);
        if (fptr.fnQueryData() < 0) {
            checkError(fptr);
        }

        long deviceFfdVersion    = fptr.getParamInt(IFptr.LIBFPTR_PARAM_DEVICE_FFD_VERSION);
        record.deviceFfdVersion  = getFFDVersion(deviceFfdVersion);
        long fnFfdVersion        = fptr.getParamInt(IFptr.LIBFPTR_PARAM_FN_FFD_VERSION);
        record.fnFfdVersion      = getFFDVersion(fnFfdVersion);
        long ffdVersion          = fptr.getParamInt(IFptr.LIBFPTR_PARAM_FFD_VERSION);
        record.ffdVersion        = getFFDVersion(ffdVersion);

        //ФН
        fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_FN_INFO);
        fptr.fnQueryData();

        record.fnSerial = fptr.getParamString(IFptr.LIBFPTR_PARAM_SERIAL_NUMBER);
        record.fnVersion = fptr.getParamString(IFptr.LIBFPTR_PARAM_FN_VERSION);

        //ФН Дата окончания
        fptr.setParam(IFptr.LIBFPTR_PARAM_FN_DATA_TYPE, IFptr.LIBFPTR_FNDT_VALIDITY);
        if (fptr.fnQueryData() < 0) {
            checkError(fptr);
        }

        Date dateTime = fptr.getParamDateTime(IFptr.LIBFPTR_PARAM_DATE_TIME);
        if (dateTime != null) {
            record.fnDate = Utils.toDateString(dateTime);
        }

        return record;
    }

    private String getFFDVersion(long version) {
        if (version == IFptr.LIBFPTR_FFD_1_0) {
            return "1.0";
        }
        if (version == IFptr.LIBFPTR_FFD_1_0_5) {
            return "1.05";
        }
        if (version == IFptr.LIBFPTR_FFD_1_1) {
            return "1.1";
        }
        return "неизвестная";
    }

    synchronized public void applySettingsAndConnect() throws PrinterException {
        if (fptr != null) {
            try {
                disconnect();
            }
            catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("START ATOL PRINTER");
        logger.info("PORT: " + port);

        fptr = new Fptr();
        fptr.setSingleSetting(fptr.LIBFPTR_SETTING_LIBRARY_PATH, System.getProperty("java.library.path"));
        fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_MODEL, String.valueOf(model));
        if (port.equals("TCPIP")) {
            logger.info("Connect to: " + wifiIP + ":" + wifiPort);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_TCPIP));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPADDRESS, wifiIP);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_IPPORT, String.valueOf(wifiPort));
        }
        else if (port.equals("USBAUTO")) {
            logger.info("Connect to port: " + port);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_USB));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_USB_DEVICE_PATH, "auto");
        }
        else if (port.startsWith("COM")) {
            logger.info("Connect to port: " + port);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_COM));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_COM_FILE, port);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_BAUDRATE, String.valueOf(IFptr.LIBFPTR_PORT_BR_115200));
        }
        else {
            logger.info("Connect to port: " + port);
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_PORT, String.valueOf(IFptr.LIBFPTR_PORT_USB));
            fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_USB_DEVICE_PATH, port);
        }

        if (ofdChannel != null) {
            if (ofdChannel.equals(OFDChannel.PROTO)) {
                logger.info("ОФД средвами транспортного протокола (OFD PROTO 1)");
                fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_OFD_CHANNEL, String.valueOf(IFptr.LIBFPTR_OFD_CHANNEL_PROTO));
            }
            else if (ofdChannel.equals(OFDChannel.ASIS)) {
                logger.info("ОФД используя настройки ККМ (OFD NONE 2)");
                fptr.setSingleSetting(IFptr.LIBFPTR_SETTING_OFD_CHANNEL, String.valueOf(IFptr.LIBFPTR_OFD_CHANNEL_NONE));
            }
            else {
                throw new PrinterException(0, "Не поддерживаемое значение параметра связи с ОФД");
            }
        }

        if (fptr.applySingleSettings() < 0) {
            checkError(fptr);
        }

        logger.info("ATOL PRINTER STARTED");
        connect();

        if (!ofdChannel.equals(OFDChannel.ASIS)) {
            if (ofdChannel.equals(OFDChannel.USB)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 1);
                fptr.writeDeviceSetting();
                logger.info("ОФД через USB (установить EoU модуль)");
            }
            if (ofdChannel.equals(OFDChannel.ETHERNET)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 2);
                fptr.writeDeviceSetting();
                logger.info("ОФД через Ethernet");
            }
            else if (ofdChannel.equals(OFDChannel.WIFI)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 3);
                fptr.writeDeviceSetting();
                logger.info("ОФД через WiFi");
            }
            else if (ofdChannel.equals(OFDChannel.GSM)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 4);
                fptr.writeDeviceSetting();
                logger.info("ОФД через GSM");
            }
            else if (ofdChannel.equals(OFDChannel.TRANSPORT)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 5);
                fptr.writeDeviceSetting();
                logger.info("ОФД через транспортный протокол");
            }
            else if (ofdChannel.equals(OFDChannel.PROTO)) {
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_ID, 276);
                fptr.setParam(IFptr.LIBFPTR_PARAM_SETTING_VALUE, 5);
                fptr.writeDeviceSetting();
                //publishProgress("ОФД через транспортный протокол");
            }
            else {
                throw new PrinterException(0, "Не поддерживаемое значение параметра связи с ОФД: " + ofdChannel.name());
            }
        }
    }

    public void connect() throws PrinterException {
        if (fptr.open() < 0) {
            checkError(fptr);
        }
        cancelCheck();
        logger.info("ATOL PRINTER CONNECTED");
    }

    public void destroy() throws Throwable {
        disconnect();
    }

    protected void disconnect() throws PrinterException {
        if (fptr.close() < 0) {
            checkError(fptr);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        logger.info("ATOL PRINTER DESTROY");
        fptr.destroy();
    }

    synchronized public void reportX(ReportCommandRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_X);
        if (fptr.report() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void reportZ(AbstractCommandRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_CLOSE_SHIFT);
        if (fptr.report() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void startShift(ReportCommandRecord record) throws PrinterException {
        loginOperator(record);
        if (fptr.openShift() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void cashIncome(CashChangeRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, record.sum);
        if (fptr.cashIncome() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void cashOutcome(CashChangeRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, record.sum);
        if (fptr.cashOutcome() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void copyLastDoc(AbstractCommandRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_LAST_DOCUMENT);
        if (fptr.report() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void demoReport(AbstractCommandRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_KKT_DEMO);
        if (fptr.report() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    synchronized public void ofdTestReport(AbstractCommandRecord record) throws PrinterException {
        loginOperator(record);
        fptr.setParam(IFptr.LIBFPTR_PARAM_REPORT_TYPE, IFptr.LIBFPTR_RT_OFD_TEST);
        if (fptr.report() < 0) {
            checkError(fptr);
        }
        if (!waitDocumentClosed()) {
            checkError(fptr);
        }
    }

    //выставление счета
    synchronized public void printPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        doPrintPredCheck(record);
    }

    private void doPrintPredCheck(PrintCheckCommandRecord record) throws PrinterException {
        checkRecord(record);
        if (fptr.beginNonfiscalDocument() < 0) {
            checkError(fptr);
        }

        printText("СЧЕТ (ПРЕДЧЕК)");
        printText("");
        printText("ПОЗИЦИИ ОПЛАТЫ", IFptr.LIBFPTR_ALIGNMENT_CENTER, IFptr.LIBFPTR_TW_WORDS);
        for(int i = 0; i < record.itemList.size(); i++) {
            ItemRecord r = record.itemList.get(i);
            printText((i + 1) + ". " + r.name, IFptr.LIBFPTR_ALIGNMENT_LEFT, IFptr.LIBFPTR_TW_WORDS);

            double total = r.price * r.quantity;
            printText(r.price + " x " + r.quantity + " = " + total, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);

            if (r.discountSum != null && r.discountSum > 0) {
                printText("Скидка: " + r.discountSum, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
            }
            if (r.discountPercent != null && r.discountPercent > 0) {
                printText("Скидка: " + r.discountPercent + "%", IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
            }
        }
        printBoldText("ИТОГО: " + record.moneySum, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);

        if (GuestType.TIME.equals(record.type) && record.guestInfoList != null) {
            printText("");
            printText("РАССЧИТЫВАЕМЫЕ ГОСТИ", IFptr.LIBFPTR_ALIGNMENT_LEFT, IFptr.LIBFPTR_TW_CHARS);
            int i = 1;
            for(GuestRecord r: record.guestInfoList) {
                String name = r.name;
                if (!StringUtils.isEmpty(r.card)) {
                    name += " (" + r.card + ")";
                };
                printText(i + ". " + name, IFptr.LIBFPTR_ALIGNMENT_LEFT, IFptr.LIBFPTR_TW_CHARS);
                printText("время прихода: " + r.startTime, IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
                printText("проведенное время: " + r.minutes + " мин.", IFptr.LIBFPTR_ALIGNMENT_RIGHT, IFptr.LIBFPTR_TW_CHARS);
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
                printText(name, IFptr.LIBFPTR_ALIGNMENT_CENTER, IFptr.LIBFPTR_TW_CHARS);
                printText(r.message);
            }

            printText("");
            printText("");
        }

        if (record.additionalInfo != null) {
            printText("");
            for(String s: record.additionalInfo) {
                printText(s, IFptr.LIBFPTR_ALIGNMENT_CENTER, IFptr.LIBFPTR_TW_WORDS);
            }
            printText("");
        }

        if (fptr.endNonfiscalDocument() < 0) {
            checkError(fptr);
        }
    }

    synchronized public void printCheck(PrintCheckCommandRecord record) throws PrinterException {
        doPrintCheck(record, IFptr.LIBFPTR_RT_SELL);
    }

    synchronized public void printReturnCheck(PrintCheckCommandRecord record) throws PrinterException {
        doPrintCheck(record, IFptr.LIBFPTR_RT_SELL_RETURN);
    }

    private void doPrintCheck(PrintCheckCommandRecord record, int checkType) throws PrinterException {
        checkRecord(record);
        cancelCheck();
        loginOperator(record);

        // Открываем чек продажи, попутно обработав превышение смены
        try {
            openCheck(record, checkType);
        } catch (PrinterException e) {
            // Проверка на превышение смены
            if (e.getCode() == IFptr.LIBFPTR_ERROR_SHIFT_EXPIRED) {
                reportZ(record);
                openCheck(record, checkType);
            } else {
                throw e;
            }
        }

        try {
            BigDecimal totalPrice = new BigDecimal(0.0);
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
                BigDecimal priceWithDiscount = price.subtract(discountPosition);

                logger.info("Name: " + r.name + ", price=" + price + ", discount = " + discountPosition + ", priceWithDiscount = " + priceWithDiscount);
                registrationFZ54(r.name, priceWithDiscount.doubleValue(), r.quantity, r.vatValue, r.type);

                totalPrice = totalPrice.add(priceWithDiscount.multiply(new BigDecimal(r.quantity)));
            }

            if (record.creditSum != null && record.creditSum > 0) {
                payment(record.creditSum, IFptr.LIBFPTR_PT_ELECTRONICALLY);
            }

            if (record.moneySum != null && record.moneySum > 0) {
                payment(record.moneySum, IFptr.LIBFPTR_PT_CASH);
            }

            logger.info("Total price = " + totalPrice);
            if (Boolean.TRUE.equals(record.dropPenny)) {
                double totalWithoutPenny = totalPrice.setScale(0, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                fptr.setParam(IFptr.LIBFPTR_PARAM_SUM, totalWithoutPenny);
                if (fptr.receiptTotal() < 0) {
                    checkError(fptr);
                }
            }

            // Закрываем чек
            if (Boolean.TRUE.equals(record.testMode)) {
                cancelCheck();
            }
            else {
                if (fptr.closeReceipt() < 0) {
                    checkError(fptr);
                }
                if (!waitDocumentClosed()) {
                    cancelCheck();
                }
                continuePrint();
            }
        }
        catch (PrinterException e) {
            cancelCheck();
            throw e;
        }
    }

    protected void checkRecord(PrintCheckCommandRecord record) throws PrinterException {
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
        if (Boolean.TRUE.equals(record.onlyElectronically)) {
            if (StringUtils.isEmpty(record.phone) && StringUtils.isEmpty(record.email)) {
                throw new PrinterException(0, "Для электронных чеков обязателно задание телефона или email покупателя");
            }
        }
    }

    private int getVatNumber(VAT vatValue) throws PrinterException {
        if (vatValue == null) {
            return IFptr.LIBFPTR_TAX_NO;
        }
        if (vatValue.equals(VAT.NO)) {
            return IFptr.LIBFPTR_TAX_NO;
        }
        if (vatValue.equals(VAT.VAT0)) {
            return IFptr.LIBFPTR_TAX_VAT0;
        }
        if (vatValue.equals(VAT.VAT10)) {
            return IFptr.LIBFPTR_TAX_VAT10;
        }
        if (vatValue.equals(VAT.VAT18)) {
            return IFptr.LIBFPTR_TAX_VAT18;
        }
        if (vatValue.equals(VAT.VAT110)) {
            return IFptr.LIBFPTR_TAX_VAT110;
        }
        if (vatValue.equals(VAT.VAT118)) {
            return IFptr.LIBFPTR_TAX_VAT118;
        }
        throw new PrinterException(0, "Неизвестный тип налога: " + vatValue);
    }

    private void registrationFZ54(String name, double price, double quantity, VAT itemVat, ItemType type) throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_COMMODITY_NAME, name);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PRICE, price);
        fptr.setParam(IFptr.LIBFPTR_PARAM_QUANTITY, quantity);

        VAT vatValue = this.vat;
        if (itemVat != null) {
            vatValue = itemVat;
        }
        int vatNumber = getVatNumber(vatValue);

        fptr.setParam(IFptr.LIBFPTR_PARAM_TAX_TYPE, vatNumber);
        if (ItemType.SERVICE.equals(type)) {
            fptr.setParam(1212, 4);
        }
        if (fptr.registration() < 0) {
            checkError(fptr);
        }
    }

    private void payment(double sum, int type) throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_TYPE, type);
        fptr.setParam(IFptr.LIBFPTR_PARAM_PAYMENT_SUM, sum);
        fptr.payment();
    }

    private void openCheck(PrintCheckCommandRecord record, int type) throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_TYPE, type);
        if (!StringUtils.isEmpty(record.phone)) {
            if (record.phone.length() == 10 && record.phone.startsWith("9")) {
                record.phone = "+7" + record.phone;
            }
            fptr.setParam(1008, record.phone);
        }
        else if (!StringUtils.isEmpty(record.email)) {
            fptr.setParam(1008, record.email);
        }

        if (Boolean.TRUE.equals(record.onlyElectronically)) {
            fptr.setParam(IFptr.LIBFPTR_PARAM_RECEIPT_ELECTRONICALLY, true);
        }

        if (!StringUtils.isEmpty(record.emailFrom)) {
            fptr.setParam(1117, record.emailFrom);
        }

        if (!StringUtils.isEmpty(record.billingLocation)) {
            fptr.setParam(1187, record.billingLocation);
        }

        if (fptr.openReceipt() < 1) {
            checkError(fptr);
        }
    }

    protected void checkError(IFptr fptr) throws PrinterException {
        checkError(fptr, true);
    }

    private void checkError(IFptr fptr, boolean log) throws PrinterException {
        int rc = fptr.errorCode();
        if (rc > 0) {
            if (log) {
                logger.error(fptr.errorDescription());
            }
            throw new PrinterException(rc, fptr.errorDescription());
        }
    }

    private boolean waitDocumentClosed() {
        int count = 0;
        while (fptr.checkDocumentClosed() < 0) {
            // Не удалось проверить состояние документа.
            // Вывести пользователю текст ошибки,
            // попросить устранить неполадку и повторить запрос
            String errorDescription = fptr.errorDescription();
            logger.error(errorDescription);
            if ("Не поддерживается в данной версии".equalsIgnoreCase(errorDescription)) {
                break;
            }

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                break;
            }
            count++;
            if (count > 20) {
                break;
            }
        }

        if (!fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_CLOSED)) {
            return false;
        }
        return true;
    }

    private void continuePrint() {
        int count = 0;

        if (!fptr.getParamBool(IFptr.LIBFPTR_PARAM_DOCUMENT_PRINTED)) {
            while (fptr.continuePrint() < 0) {
                String errorDescription = fptr.errorDescription();
                logger.error(errorDescription);
                if ("Не поддерживается в данной версии".equalsIgnoreCase(errorDescription)) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;
                }
                count++;
                if (count > 20) {
                    break;
                }
            }
        }
    }

    private void loginOperator(AbstractCommandRecord record) {
        if (StringUtils.isEmpty(record.userFIO)) {
            return;
        }
        String fio = record.userFIO;
        if (!StringUtils.isEmpty(record.userPosition)) {
            fio = record.userPosition + " " + fio;
        }
        fptr.setParam(1021, fio);
        if (!StringUtils.isEmpty(record.userINN)) {
            fptr.setParam(1203, record.userINN);
        }
        fptr.operatorLogin();
    }

    private void cancelCheck() throws PrinterException {
        // Отменяем чек, если уже открыт. Ошибки "Неверный режим" и "Чек уже закрыт"
        // не являются ошибками, если мы хотим просто отменить чек
        try {
            if (fptr.cancelReceipt() < 0)
                checkError(fptr, false);
        } catch (PrinterException e) {
            int rc = e.getCode();
            if (rc != IFptr.LIBFPTR_ERROR_DENIED_IN_CLOSED_RECEIPT) {
                throw e;
            }
        }
    }

    protected void printText(String text) throws PrinterException {
        printText(text, IFptr.LIBFPTR_ALIGNMENT_CENTER, IFptr.LIBFPTR_TW_WORDS);
    }

    protected void printText(String text, int alignment, int wrap) throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_TEXT, text);
        fptr.setParam(IFptr.LIBFPTR_PARAM_ALIGNMENT, alignment);
        fptr.setParam(IFptr.LIBFPTR_PARAM_TEXT_WRAP, wrap);
        fptr.printText();
    }

    protected void printBoldText(String text, int alignment, int wrap) throws PrinterException {
        fptr.setParam(IFptr.LIBFPTR_PARAM_TEXT, text);
        fptr.setParam(IFptr.LIBFPTR_PARAM_ALIGNMENT, alignment);
        fptr.setParam(IFptr.LIBFPTR_PARAM_TEXT_WRAP, wrap);
        fptr.setParam(IFptr.LIBFPTR_PARAM_FONT_DOUBLE_WIDTH, true);
        fptr.setParam(IFptr.LIBFPTR_PARAM_FONT_DOUBLE_HEIGHT, true);
        fptr.printText();
    }



}

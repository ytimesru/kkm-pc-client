package org.bitbucket.ytimes.client.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import org.bitbucket.ytimes.client.egais.EGAISProcessor;
import org.bitbucket.ytimes.client.egais.EgaisException;
import org.bitbucket.ytimes.client.egais.records.TTNRecord;
import org.bitbucket.ytimes.client.kitchen.KitchenPrinter;
import org.bitbucket.ytimes.client.kitchen.Sam4sKitchenPrinter;
import org.bitbucket.ytimes.client.kkm.printer.*;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.bitbucket.ytimes.client.kkm.services.ConfigService;
import org.bitbucket.ytimes.client.screen.record.ScreenInfoRecord;
import org.bitbucket.ytimes.client.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by andrey on 01.10.17.
 */
@Component
public class WebServer extends NanoHTTPD {
    public static String version = "2.0.7";

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper mapper = new ObjectMapper();
    private Printer printer;
    private KitchenPrinter kitchenPrinter = null;
    private int port;

    @Value("${verificationCode}")
    private String verificationCode;

    @Autowired
    private ConfigService configService;

    @Autowired
    private EGAISProcessor egaisProcessor;

    @Autowired
    private WSServer screenService;

    @Autowired
    public WebServer(@Value("${port}") int port) {
        super(port);
        this.port = port;
    }

    @PostConstruct
    private void onStart() {
        try {
            logger.info("Server started: ");
            String clientIP = Utils.getClientIP();

            if (clientIP != null) {
                logger.info("  Device address: " + clientIP);
                logger.info("  Port: " + port);
            }
            else {
                logger.info("  Device address: unknown");
                logger.info("  Port: " + port);
            }

            initPrinter(false);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        NanoHTTPD.Response response = null;
        if (method.equals(NanoHTTPD.Method.POST)) {
            final HashMap<String, String> map = new HashMap<String, String>();
            try {
                session.parseBody(map);
                final String json = map.get("postData");

                logger.info("received: " + json);

                Result res = new Result();
                res.success = true;
                try {
                    res.res = processAction(json);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    res.success = false;
                    res.errorMessage = e.getMessage();
                    res.errorClass = e.getClass().getSimpleName();
                }
                response = newFixedLengthResponse(mapper.writeValueAsString(res));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = newFixedLengthResponse("{success: false, errorMessage: \"" + e.getMessage() + "\"}");
            }
        }
        else {
            response = newFixedLengthResponse("");
        }
        response = addCORSHeaders(response);
        return response;
    }

    private NanoHTTPD.Response addCORSHeaders(NanoHTTPD.Response resp) {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "origin,accept,content-type");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        resp.addHeader("Access-Control-Max-Age", "" + (42 * 60 * 60));
        return resp;
    }

    private Object processAction(String json) throws PrinterException, EgaisException, IOException {
        ActionRecord action = parseMessage(json, ActionRecord.class);
        if (action == null) {
            throw new IllegalArgumentException("error parse ActionRecord");
        }
        checkCode(action.code);
        checkShopId(action.shopId);

        logger.info("Обработка действия: " + action.action);
        if ("config".equals(action.action)) {
            ConfigRecord record = parseMessage(action.data, ConfigRecord.class);
            applyConfig(record);
        }
        else if ("status".equals(action.action)) {
            StatusRecord record = new StatusRecord();
            record.config = getConfig();
            record.version = version;
            if (printer != null) {
                try {
                    record.isConnected = printer.isConnected();
                    if (Boolean.TRUE.equals(record.isConnected)) {
                        record.info = printer.getInfo();
                    }
                }
                catch (Exception e) {
                    record.lastError = e.getMessage();
                    logger.error(e.getMessage(), e);
                }
            }
            return record;
        }
        else if (action.action.startsWith("egais")) {
            String egaisEnabled = configService.getValue("egaisENABLED", "false");
            if (!"true".equals(egaisEnabled)) {
                throw new IllegalArgumentException("Поддержка ЕГАИС в коммуникационном модуле выключена");
            }
            try {
                if ("egais/ttnincomelist".equals(action.action)) {
                    String requestGuid = action.data;
                    return egaisProcessor.getAvailableTTNList(requestGuid);
                } else if ("egais/ttnnotanswerrequest".equals(action.action)) {
                    return egaisProcessor.sendNotAnswerTTNRequest();
                } else if ("egais/ttnnotanswerresponse".equals(action.action)) {
                    String requestGuid = action.data;
                    return egaisProcessor.loadNotAnswerTTNResponse(requestGuid);
                } else if ("egais/ttnactresult".equals(action.action)) {
                    String requestGuid = action.data;
                    return egaisProcessor.getTTNActResult(requestGuid);
                } else if ("egais/ttnresult".equals(action.action)) {
                    String requestGuid = action.data;
                    return egaisProcessor.getTTNResult(requestGuid);
                } else if ("egais/ttnreject".equals(action.action)) {
                    TTNRecord record = parseMessage(action.data, TTNRecord.class);
                    return egaisProcessor.rejectTTN(record);
                } else if ("egais/ttnaccept".equals(action.action)) {
                    TTNRecord record = parseMessage(action.data, TTNRecord.class);
                    return egaisProcessor.acceptTTN(record);
                } else if ("egais/ttnacceptpartial".equals(action.action)) {
                    TTNRecord record = parseMessage(action.data, TTNRecord.class);
                    return egaisProcessor.acceptPartialTTN(record);
                }
            }
            catch (Exception e) {
                throw new EgaisException(e.getMessage(), e);
            }
        }
        else if (action.action.startsWith("screen")) {
            if ("screen/clear".equals(action.action)) {
                screenService.setInfo(null);
            }
            else if ("screen/set".equals(action.action)) {
                ScreenInfoRecord record = parseMessage(action.data, ScreenInfoRecord.class);
                screenService.setInfo(record);
            }
        }
        else if (action.action.startsWith("kitchen")) {
            if (kitchenPrinter != null && "kitchen/print".equals(action.action)) {
                PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
                kitchenPrinter.print(record);
            }
        }
        else {
            if (printer == null) {
                throw new IllegalArgumentException("Не настроен принтер чеков. Проверьте настройки системы в разделе Оборудование");
            }
            else {
                if (!printer.isConnected()) {
                    printer.connect();
                }

                if ("printCheck".equals(action.action)) {
                    PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
                    printer.printCheck(record);
                }
                else if ("printReturnCheck".equals(action.action)) {
                    PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
                    printer.printReturnCheck(record);
                }
                else if ("printPredCheck".equals(action.action)) {
                    PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
                    printer.printPredCheck(record);
                }
                else if ("reportX".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.reportX(record);
                }
                else if ("reportZ".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.reportZ(record);
                }
                else if ("copyLastDoc".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.copyLastDoc(record);
                }
                else if ("ofdTest".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.ofdTestReport(record);
                }
                else if ("openSession".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.startShift(record);
                }
                else if ("cashIncome".equals(action.action)) {
                    CashChangeRecord record = parseMessage(action.data, CashChangeRecord.class);
                    printer.cashIncome(record);
                }
                else if ("cashOutcome".equals(action.action)) {
                    CashChangeRecord record = parseMessage(action.data, CashChangeRecord.class);
                    printer.cashOutcome(record);
                }
                else {
                    throw new IllegalArgumentException("Неизвестная команда: " + action.action + ". Вероятно требуется обновить " +
                            "модуль для связи с кассой до последней версии");
                }
                logger.info("Обработано действие: " + action.action);
            }
        }
        return null;
    }

    private void checkCode(String code) throws PrinterException {
        String verificationCode = configService.getValue("verificationCode", this.verificationCode);
        if (StringUtils.isEmpty(code) || !code.equals(verificationCode)) {
            throw new PrinterException(0, "Код подтверждения не совпадает, доступ запрещен");
        }
    }

    private void checkShopId(Long shopId) throws PrinterException {
        String value = configService.getValue("shopId", null);
        Long currentShopId = StringUtils.isEmpty(value) ? null : Long.parseLong(value);

        if (currentShopId != null && shopId != null && !currentShopId.equals(shopId)) {
            logger.info("Current shop: " + currentShopId + ", request shop: " + shopId);
            throw new PrinterException(PrinterException.WRONG_SHOP, "Коммуникационный модуль для связи с оборудованием настроен для работы с другим заведением. Доступ запрещен.");
        }
    }

    private <T> T parseMessage(String message, Class<T> tClass) throws IOException {
        return mapper.readValue(message, tClass);
    }

    private void applyConfig(ConfigRecord record) throws IOException, PrinterException {
        try {
            configService.setValue("verificationCode", record.verificationCode);
            configService.setValue("shopId", record.shopId != null ? record.shopId + "" : null);
            configService.setValue("model", record.model);
            configService.setValue("port", record.port);
            configService.setValue("wifiIP", record.wifiIP);
            configService.setValue("wifiPort", record.wifiPort != null ? record.wifiPort + "" : null);
            configService.setValue("vat", record.vat != null ? record.vat.name() : VAT.NO.name());
            configService.setValue("ofd", record.ofd != null ? record.ofd.name() : OFDChannel.PROTO.name());
            configService.setValue("egaisENABLED", Boolean.TRUE.equals(record.egaisENABLED) ? "true" : "false");
            configService.setValue("egaisFSRARID", record.egaisFSRARID);
            configService.setValue("egaisUTMAddress", record.egaisUTMAddress);
            configService.setValue("kitchenPrinterModel", record.kitchenPrinterModel);
            configService.setValue("kitchenPrinterIP", record.kitchenPrinterIP);
            configService.setValue("kitchenPrinterPort", record.kitchenPrinterPort != null ? record.kitchenPrinterPort + "" : null);
            configService.setValue("kitchenPrinterNumber", record.kitchenPrinterNumber != null ? record.kitchenPrinterNumber + "" : null);
            configService.setValue("accountExternalId", record.accountExternalId);
            configService.setValue("accountExternalBaseUrl", record.accountExternalBaseUrl);
            if (record.params != null && record.params.size() > 0) {
                for (String keys : record.params.keySet()) {
                    configService.setValue(keys, record.params.get(keys));
                }
            }

            configService.save();
        }
        finally {
            initPrinter(true);
        }
    }

    private ConfigRecord getConfig() {
        ConfigRecord record = new ConfigRecord();
        record.params = new HashMap<String, String>();
        record.model = "ATOLAUTO";
        record.port  = "USBAUTO";
        record.verificationCode = this.verificationCode;
        record.vat = VAT.NO;
        record.ofd = OFDChannel.PROTO;
        record.wifiPort = 5555;

        record.egaisENABLED = false;
        record.egaisUTMAddress = "http://localhost:8080/";


        for(String keys: configService.getAllKeys()) {
            String value = configService.getValue(keys, null);
            if (keys.equals("verificationCode")) {
                record.verificationCode = configService.getValue("verificationCode", this.verificationCode);
            }
            if (keys.equals("shopId")) {
                record.shopId = StringUtils.isEmpty(value) ? null : Long.parseLong(value);
            }
            else if (keys.equals("model")) {
                record.model = value;
            }
            else if (keys.equals("port")) {
                record.port = value;
            }
            else if (keys.equals("wifiIP")) {
                record.wifiIP = value;
            }
            else if (keys.equals("wifiPort")) {
                if (value != null) {
                    try {
                        record.wifiPort = Integer.parseInt(value);
                    }
                    catch (NumberFormatException e) {}
                }
                else {
                    record.wifiPort = 5555;
                }
            }
            else if (keys.equals("vat")) {
                if (!StringUtils.isEmpty(value)) {
                    record.vat = VAT.valueOf(value);
                }
                else {
                    record.vat = VAT.NO;
                }
            }
            else if (keys.equals("ofd")) {
                if (!StringUtils.isEmpty(value)) {
                    record.ofd = OFDChannel.valueOf(value);
                }
                else {
                    record.ofd = OFDChannel.PROTO;
                }
            }
            else if (keys.equals("egaisENABLED")) {
                record.egaisENABLED = "true".equals(value);
            }
            else if (keys.equals("egaisFSRARID")) {
                record.egaisFSRARID = value;
            }
            else if (keys.equals("egaisUTMAddress")) {
                record.egaisUTMAddress = value;
            }
            else if (keys.equals("kitchenPrinterModel")) {
                record.kitchenPrinterModel = value;
            }
            else if (keys.equals("kitchenPrinterIP")) {
                record.kitchenPrinterIP = value;
            }
            else if (keys.equals("kitchenPrinterPort")) {
                try {
                    record.kitchenPrinterPort = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {}
            }
            else if (keys.equals("kitchenPrinterNumber")) {
                try {
                    record.kitchenPrinterNumber = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {}
            }
            else if (keys.equals("accountExternalId")) {
                record.accountExternalId = value;
            }
            else if (keys.equals("accountExternalBaseUrl")) {
                record.accountExternalBaseUrl = value;
            }
            else {
                record.params.put(keys, value);
            }
        }

        return record;
    }

    private synchronized void initPrinter(boolean demoOnConnect) throws PrinterException {
        logger.info("Init printer");
        if (printer != null) {
            logger.info("Destroy prev printer");

            try {
                printer.destroy();
            }
            catch (Throwable e) {}
        }

        ConfigRecord config = getConfig();
        if ("TEST".equals(config.model)) {
            printer = new TestPrinter();
        }
        else if (config.model.startsWith("ATOLENVD")) {
            AtolPrinter p = new AtolEnvdPrinter(config.model, config.port, config.wifiIP, config.wifiPort);
            p.setVat(config.vat);
            p.setOfdChannel(config.ofd);

            printer = p;
            p.applySettingsAndConnect();
        }
        else if (config.model.startsWith("ATOL")) {
            AtolPrinter p = new AtolPrinter(config.model, config.port, config.wifiIP, config.wifiPort);
            p.setVat(config.vat);
            p.setOfdChannel(config.ofd);

            printer = p;
            p.applySettingsAndConnect();
        }
        else if (config.model.startsWith("POSPRINTER")) {
            if (StringUtils.isEmpty(config.wifiIP) || config.wifiPort == null) {
                throw new PrinterException(0, "POS принтер поддерживает только Wifi подключение");
            }
            printer = new POSPrinter(config.wifiIP, config.wifiPort);
            try {
                printer.connect();
                if (demoOnConnect) {
                    printer.demoReport(null);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new PrinterException(0, e.getMessage());
            }
        }

        if (config.kitchenPrinterModel != null) {
            if (config.kitchenPrinterModel.equals("POS")) {
                kitchenPrinter = new Sam4sKitchenPrinter(config.kitchenPrinterIP, config.kitchenPrinterPort, config.kitchenPrinterNumber);
            }
        }

        logger.info("Init printer completed");
    }

    public Printer getPrinter() {
        return printer;
    }
}

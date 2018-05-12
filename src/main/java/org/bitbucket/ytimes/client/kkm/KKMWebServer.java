package org.bitbucket.ytimes.client.kkm;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import org.bitbucket.ytimes.client.kkm.printer.AtolPrinter;
import org.bitbucket.ytimes.client.kkm.printer.Printer;
import org.bitbucket.ytimes.client.kkm.printer.PrinterException;
import org.bitbucket.ytimes.client.kkm.printer.TestPrinter;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by andrey on 01.10.17.
 */
@Component
public class KKMWebServer extends NanoHTTPD {
    public static String version = "2.0.0";

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper mapper = new ObjectMapper();
    private Printer printer;
    private int port;

    @Value("${verificationCode}")
    private String verificationCode;

    @Autowired
    private ConfigService configService;

    @Autowired
    public KKMWebServer(@Value("${port}") int port) {
        super(port);
        this.port = port;
    }

    @PostConstruct
    private void onStart() {
        try {
            logger.info("Server started: ");

            boolean hasAddress = false;
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress.toString().startsWith("/192")) {
                        logger.info("  Device address: " + inetAddress.toString().substring(1));
                        hasAddress = true;
                    }
                    else if (inetAddress.toString().startsWith("192")) {
                        logger.info("  Device address: " + inetAddress.toString());
                        hasAddress = true;
                    }
                }
            }

            if (!hasAddress) {
                logger.info("  Device address: unknown");
            }
            logger.info("  Port: " + port);

            initPrinter();
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

    private Object processAction(String json) throws PrinterException, IOException {
        ActionRecord action = parseMessage(json, ActionRecord.class);
        if (action == null) {
            throw new IllegalArgumentException("error parse ActionRecord");
        }
        checkCode(action.code);

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
                }
                catch (Exception e) {
                    record.lastError = e.getMessage();
                    logger.error(e.getMessage(), e);
                }
            }
            return record;
        }
        else {
            if (printer == null) {
                throw new IllegalArgumentException("Не настроен принтер чеков. Проверьте настройки системы в разделе Оборудование");
            }
            else {
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
                    printer.reportX();
                }
                else if ("reportZ".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.reportZ();
                }
                else if ("openSession".equals(action.action)) {
                    ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
                    printer.startShift();
                }
                else if ("cashIncome".equals(action.action)) {
                    CashIncomeRecord record = parseMessage(action.data, CashIncomeRecord.class);
                    printer.cashIncome(record.sum);
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
        if (code == null || code.trim().isEmpty() || !code.equals(verificationCode)) {
            throw new PrinterException(0, "Код подтверждения не совпадает, доступ запрещен");
        }
    }

    private <T> T parseMessage(String message, Class<T> tClass) throws IOException {
        return mapper.readValue(message, tClass);
    }

    private void applyConfig(ConfigRecord record) throws IOException, PrinterException {
        try {
            configService.setValue("verificationCode", record.verificationCode);
            configService.setValue("model", record.model);
            configService.setValue("port", record.port);
            configService.setValue("wifiIP", record.wifiIP);
            configService.setValue("wifiPort", record.wifiPort != null ? record.wifiPort + "" : null);
            if (record.params != null && record.params.size() > 0) {
                for (String keys : record.params.keySet()) {
                    configService.setValue(keys, record.params.get(keys));
                }
            }

            configService.save();
        }
        finally {
            initPrinter();
        }
    }

    private ConfigRecord getConfig() {
        ConfigRecord record = new ConfigRecord();
        record.params = new HashMap<String, String>();
        record.model = "NONE";
        record.verificationCode = this.verificationCode;
        for(String keys: configService.getAllKeys()) {
            String value = configService.getValue(keys, null);
            if (keys.equals("verificationCode")) {
                record.verificationCode = configService.getValue("verificationCode", this.verificationCode);
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
            else {
                record.params.put(keys, value);
            }
        }

        return record;
    }

    private synchronized void initPrinter() throws PrinterException {
        logger.info("Init printer");
        if (printer != null) {
            logger.info("Destroy prev printer");

            try {
                printer.destroy();
            }
            catch (Throwable e) {}
        }
        printer = null;

        String model = configService.getValue("model", "NONE");
        if ("TEST".equals(model)) {
            printer = new TestPrinter();
        }
        else if (model.startsWith("ATOL")) {
            String port = configService.getValue("port");
            String wifiIP = null;
            Integer wifiPort = null;
            if ("TCPIP".equals(port)) {
                wifiIP = configService.getValue("wifiIP", "");
                wifiPort = configService.getIntValue("wifiPort");
            }

            AtolPrinter p = new AtolPrinter(model, port, wifiIP, wifiPort);
            if (configService.contains("vid")) {
                p.setVid(configService.getValue("vid"));
            }
            if (configService.contains("pid")) {
                p.setVid(configService.getValue("pid"));
            }
            if (configService.contains("protocol")) {
                p.setProtocol(configService.getIntValue("protocol"));
            }
            if (configService.contains("accessPassword")) {
                p.setAccessPassword(configService.getIntValue("accessPassword"));
            }
            if (configService.contains("userPassword")) {
                p.setUserPassword(configService.getIntValue("userPassword"));
            }
            if (configService.contains("baudrate")) {
                p.setBaudrate(configService.getIntValue("baudrate"));
            }

            p.connect();
            printer = p;
        }
        logger.info("Init printer completed");
    }

}

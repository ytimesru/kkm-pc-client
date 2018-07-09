package org.bitbucket.ytimes.client.kkm.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.bitbucket.ytimes.client.kkm.printer.Printer;
import org.bitbucket.ytimes.client.kkm.record.DeviceModuleCheckRecord;
import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;
import org.bitbucket.ytimes.client.kkm.record.ServerResult;
import org.bitbucket.ytimes.client.main.Utils;
import org.bitbucket.ytimes.client.main.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
public class CronService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${moduleipurl}")
    private String moduleIpUrl;

    @Autowired
    private ConfigService configService;

    @Autowired
    private WebServer webServer;

    //every 120 sec
    @Scheduled(fixedRate = 120000)
    public void printCheckFromServer() throws Exception {
        logger.info("Load checks for print");
        String accountExternalId = configService.getValue("accountExternalId", null);
        if (StringUtils.isEmpty(accountExternalId)) {
            return;
        }
        String externalBaseUrl = configService.getValue("accountExternalBaseUrl", null);
        if (StringUtils.isEmpty(externalBaseUrl)) {
            return;
        }

        String url = externalBaseUrl + "util/module/check/listForPrint";

        List<NameValuePair> form = Form.form()
                .add("accountExternalId", accountExternalId)
                .build();

        Content content = Request.Post(url)
                .connectTimeout(30000)
                .bodyForm(form)
                .execute()
                .returnContent();

        String s = content.asString();
        logger.info("Receive check list for print: " + s);

        ServerResult<DeviceModuleCheckRecord> result = Utils.parseMessage(s, new TypeReference<ServerResult<DeviceModuleCheckRecord>>() {});
        if (!result.isSuccess()) {
            String error = "Server error";
            if (result.getErrors() != null && result.getErrors().size() > 0) {
                error = result.getErrors().get(0).getMessage();
            }
            logger.error(error);
            return;
        }

        Printer printer = webServer.getPrinter();
        if (!printer.isConnected()) {
            printer.connect();
        }

        for(DeviceModuleCheckRecord checkRecord: result.getRows()) {
            PrintCheckCommandRecord record = Utils.parseMessage(checkRecord.body, PrintCheckCommandRecord.class);
            try {
                printer.printCheck(record);

            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                try {
                    sendPrintCheckErrorToServer(externalBaseUrl, checkRecord.guid, e.getMessage());
                }
                catch (Exception e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
    }

    //every 15 sec
    @Scheduled(fixedRate = 15000)
    public void sendIpIfNeed() throws Exception {
        //logger.info("Check client ip");

        String moduleUUID = configService.getValue("moduleUUID", null);
        if (StringUtils.isEmpty(moduleUUID)) {
            return;
        }

        String clientIP = null;
        try {
            clientIP = Utils.getClientIP();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (clientIP == null) {
            return;
        }

        String moduleIP = configService.getValue("moduleIP", null);
        if (StringUtils.isEmpty(moduleIP) || !moduleIP.equals(clientIP)) {
            logger.info("New Client IP: " + clientIP);
            configService.setValue("moduleIP", clientIP);
            configService.setValue("moduleIPSent", "false");
            configService.save();
            moduleIP = clientIP;
        }


        String value = configService.getValue("moduleIPSent", "false");
        if ("true".equals(value)) {
            return;
        }

        try {
            if (sendIPToServer(moduleUUID, moduleIP)) {
                configService.setValue("moduleIPSent", "true");
                configService.save();
            }
        }
        catch (HttpResponseException e) {
            if ("Service Unavailable".equals(e.getMessage())) {
                logger.error("HTTP Service Unavailable (no internet)");
            }
            else {
                logger.error(e.getMessage(), e);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean sendIPToServer(String moduleUUID, String IP) throws IOException {
        logger.info("Send IP to server: " + moduleUUID + ", " + IP);
        List<NameValuePair> form = Form.form()
                .add("uuid", moduleUUID)
                .add("ip", IP)
                .build();
        Content content = Request.Post(moduleIpUrl)
                .connectTimeout(10000)
                .bodyForm(form)
                .execute()
                .returnContent();

        String s = content.asString();
        return "OK".equals(s);
    }

    private void sendPrintCheckErrorToServer(String baseUrl, String checkGuid, String errorMessage) throws IOException {
        logger.info("Send error to server: " + checkGuid + ", " + errorMessage);

        String url = baseUrl + "module/check/updateError";

        List<NameValuePair> form = Form.form()
                .add("guid", checkGuid)
                .add("errorMessage", errorMessage)
                .build();
        Content content = Request.Post(url)
                .connectTimeout(30000)
                .bodyForm(form)
                .execute()
                .returnContent();

        String s = content.asString();
        logger.info("Send error response: " + s);
    }

}

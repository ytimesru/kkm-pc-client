package org.bitbucket.ytimes.client.kkm.services;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.bitbucket.ytimes.client.kkm.Utils;
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

}

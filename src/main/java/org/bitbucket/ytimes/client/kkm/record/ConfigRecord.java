package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ConfigRecord {

    public String verificationCode;
    public Long shopId;

    // KKM
    public String model;
    public String port;
    public String wifiIP;
    public Integer wifiPort;
    public VAT vat;
    public OFDChannel ofd;
    public Map<String, String> params;

    //EGAIS
    public Boolean egaisENABLED;
    public String egaisFSRARID;
    public String egaisUTMAddress;

    //Kitchen
    public String kitchenPrinterModel = "NONE";
    public String kitchenPrinterIP = "192.168.0.253";
    public Integer kitchenPrinterPort = 6001;
    public Integer kitchenPrinterNumber = null;

    //for remote print check
    public String accountExternalId;
    public String accountExternalBaseUrl;

}

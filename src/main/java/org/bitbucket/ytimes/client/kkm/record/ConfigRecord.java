package org.bitbucket.ytimes.client.kkm.record;

import java.util.Map;

public class ConfigRecord {

    public String verificationCode;

    // KKM

    public String model;
    public String port;
    public String wifiIP;
    public Integer wifiPort;
    public VAT vat;
    public OFDChannel ofd;
    public Map<String, String> params;

    //EGAIS
    public String egaisUTMAddress;

}

package org.bitbucket.ytimes.client.egais.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TTNRecord {

    public String id;
    public String date;
    public String number;
    public String supplierFullName;
    public String supplierShortName;
    public String supplierINN;
    public String supplierKPP;
    public List<TTNPositionRecord> itemList;

    public String wayBillLink;
    public String form2RegInfoLink;

    public String actNote;

}

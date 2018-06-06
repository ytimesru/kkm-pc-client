package org.bitbucket.ytimes.client.egais.records;

import java.util.List;

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

}

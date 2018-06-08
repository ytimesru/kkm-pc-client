package org.bitbucket.ytimes.client.egais.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TTNPositionRecord {

    public String alcCode;
    public Double alcVolume;

    public String shortName;
    public String fullName;


    public Integer quantity;
    public Double price;

    public String identity;
    public String FARegId;
    public String F2RegId;

    //ACT
    public Integer actualQuantity;

}

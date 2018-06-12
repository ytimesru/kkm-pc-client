package org.bitbucket.ytimes.client.screen.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ItemRecord {

    public String name;
    public Double price;
    public Double quantity;

}

package org.bitbucket.ytimes.client.screen.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ScreenInfoRecord {

    public List<ClientRecord> clientList;
    public List<ItemRecord> itemList;

}

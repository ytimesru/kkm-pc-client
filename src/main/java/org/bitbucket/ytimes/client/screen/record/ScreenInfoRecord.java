package org.bitbucket.ytimes.client.screen.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bitbucket.ytimes.client.kkm.record.GuestRecord;
import org.bitbucket.ytimes.client.kkm.record.ItemRecord;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ScreenInfoRecord {

    public String type;
    public List<GuestRecord> clientList;
    public List<ItemRecord> itemList;

}

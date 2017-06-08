package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 28.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PrintCheckCommandRecord extends AbstractCommandRecord {

    public List<ItemRecord> itemList;
    public List<GuestRecord> guestInfoList;
    public GuestType type;
    public Boolean repeat;

    //total sum
    public Double creditSum;
    public Double moneySum;

    //OFD
    public String email;
    public String phone;

}

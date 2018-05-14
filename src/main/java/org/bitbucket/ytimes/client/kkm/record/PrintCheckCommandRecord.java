package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by root on 28.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PrintCheckCommandRecord extends AbstractCommandRecord {

    public List<ItemRecord> itemList;
    public List<GuestRecord> guestInfoList;
    public GuestType type;

    //TODO доп. инфо, чтобы вывести рекламмную акцию в чеке
    public List<String> additionalInfo;

    //total sum
    public Double creditSum;
    public Double moneySum;

    //OFD
    public String email;
    public String phone;

    public Boolean onlyElectronically = false;
    public Boolean dropPenny = false;
    public Boolean testMode;

}

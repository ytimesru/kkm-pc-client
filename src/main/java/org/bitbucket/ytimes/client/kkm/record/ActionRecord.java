package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by root on 27.05.17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActionRecord {

    public String code;
    public Long shopId;
    public String action;
    public String data;

}

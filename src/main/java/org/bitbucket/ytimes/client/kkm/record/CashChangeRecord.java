package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by andrey on 07.10.17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CashChangeRecord extends AbstractCommandRecord {

    public Integer sum;

}

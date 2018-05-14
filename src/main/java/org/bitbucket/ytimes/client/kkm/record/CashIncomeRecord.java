package org.bitbucket.ytimes.client.kkm.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by andrey on 07.10.17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CashIncomeRecord extends AbstractCommandRecord {

    public Integer sum;

}

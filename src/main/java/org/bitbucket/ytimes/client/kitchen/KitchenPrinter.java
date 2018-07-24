package org.bitbucket.ytimes.client.kitchen;

import org.bitbucket.ytimes.client.kkm.printer.PrinterException;
import org.bitbucket.ytimes.client.kkm.record.PrintCheckCommandRecord;

/**
 * Created by andrey on 22.06.18.
 */

public interface KitchenPrinter {

    void print(PrintCheckCommandRecord record) throws PrinterException;

}

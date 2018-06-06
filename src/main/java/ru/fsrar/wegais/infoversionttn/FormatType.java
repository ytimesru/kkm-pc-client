//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.infoversionttn;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FormatType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FormatType">
 *   &lt;restriction base="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50">
 *     &lt;enumeration value="WayBill"/>
 *     &lt;enumeration value="WayBill_v2"/>
 *     &lt;enumeration value="WayBill_v3"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "FormatType")
@XmlEnum
public enum FormatType {

    @XmlEnumValue("WayBill")
    WAY_BILL("WayBill"),
    @XmlEnumValue("WayBill_v2")
    WAY_BILL_V_2("WayBill_v2"),
    @XmlEnumValue("WayBill_v3")
    WAY_BILL_V_3("WayBill_v3");
    private final String value;

    FormatType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FormatType fromValue(String v) {
        for (FormatType c: FormatType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
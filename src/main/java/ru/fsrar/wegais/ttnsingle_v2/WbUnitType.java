//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.ttnsingle_v2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WbUnitType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WbUnitType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Packed"/>
 *     &lt;enumeration value="Unpacked"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WbUnitType")
@XmlEnum
public enum WbUnitType {

    @XmlEnumValue("Packed")
    PACKED("Packed"),
    @XmlEnumValue("Unpacked")
    UNPACKED("Unpacked");
    private final String value;

    WbUnitType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WbUnitType fromValue(String v) {
        for (WbUnitType c: WbUnitType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
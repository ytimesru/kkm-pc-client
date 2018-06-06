//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.commonenum;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeChargeOn.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeChargeOn">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Пересортица"/>
 *     &lt;enumeration value="Излишки"/>
 *     &lt;enumeration value="Продукция, полученная до 01.01.2016"/>
 *     &lt;enumeration value="Производство_Сливы"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeChargeOn")
@XmlEnum
public enum TypeChargeOn {


    /**
     * Излишки пересортица.
     * 
     */
    @XmlEnumValue("\u041f\u0435\u0440\u0435\u0441\u043e\u0440\u0442\u0438\u0446\u0430")
    ПЕРЕСОРТИЦА("\u041f\u0435\u0440\u0435\u0441\u043e\u0440\u0442\u0438\u0446\u0430"),

    /**
     * Излишки продукции. В случае обнаружения излишков продукции организация предоставляет в ЕГАИС акт постановки на баланс с указанием основания «Излишки».
     * 
     */
    @XmlEnumValue("\u0418\u0437\u043b\u0438\u0448\u043a\u0438")
    ИЗЛИШКИ("\u0418\u0437\u043b\u0438\u0448\u043a\u0438"),

    /**
     * Продукция, полученная до 01.01.2016
     * 
     */
    @XmlEnumValue("\u041f\u0440\u043e\u0434\u0443\u043a\u0446\u0438\u044f, \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u043d\u0430\u044f \u0434\u043e 01.01.2016")
    ПРОДУКЦИЯ_ПОЛУЧЕННАЯ_ДО_01_01_2016("\u041f\u0440\u043e\u0434\u0443\u043a\u0446\u0438\u044f, \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u043d\u0430\u044f \u0434\u043e 01.01.2016"),

    /**
     * Собственное производство/сливы продукции
     * 
     */
    @XmlEnumValue("\u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0441\u0442\u0432\u043e_\u0421\u043b\u0438\u0432\u044b")
    ПРОИЗВОДСТВО_СЛИВЫ("\u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0441\u0442\u0432\u043e_\u0421\u043b\u0438\u0432\u044b");
    private final String value;

    TypeChargeOn(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeChargeOn fromValue(String v) {
        for (TypeChargeOn c: TypeChargeOn.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

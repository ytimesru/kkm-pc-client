//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.clientref_v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Иностранные организации
 * 
 * <p>Java class for OrgInfoForeignReply_v2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrgInfoForeignReply_v2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="FO" type="{http://fsrar.ru/WEGAIS/ClientRef_v2}FOType"/>
 *         &lt;element name="TS" type="{http://fsrar.ru/WEGAIS/ClientRef_v2}TSReplyType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrgInfoForeignReply_v2", propOrder = {
    "fo",
    "ts"
})
public class OrgInfoForeignReplyV2 {

    @XmlElement(name = "FO")
    protected FOType fo;
    @XmlElement(name = "TS")
    protected TSReplyType ts;

    /**
     * Gets the value of the fo property.
     * 
     * @return
     *     possible object is
     *     {@link FOType }
     *     
     */
    public FOType getFO() {
        return fo;
    }

    /**
     * Sets the value of the fo property.
     * 
     * @param value
     *     allowed object is
     *     {@link FOType }
     *     
     */
    public void setFO(FOType value) {
        this.fo = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link TSReplyType }
     *     
     */
    public TSReplyType getTS() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TSReplyType }
     *     
     */
    public void setTS(TSReplyType value) {
        this.ts = value;
    }

}

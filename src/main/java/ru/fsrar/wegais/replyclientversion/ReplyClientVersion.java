//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.replyclientversion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.fsrar.wegais.clientref_v2.OrgInfoV2;


/**
 * Ответ на запрос о клиентах
 * 
 * <p>Java class for ReplyClientVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplyClientVersion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VersionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Client" type="{http://fsrar.ru/WEGAIS/ClientRef_v2}OrgInfo_v2" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReplyClientVersion", propOrder = {
    "versionDate",
    "client"
})
public class ReplyClientVersion {

    @XmlElement(name = "VersionDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar versionDate;
    @XmlElement(name = "Client")
    protected OrgInfoV2 client;

    /**
     * Gets the value of the versionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getVersionDate() {
        return versionDate;
    }

    /**
     * Sets the value of the versionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setVersionDate(XMLGregorianCalendar value) {
        this.versionDate = value;
    }

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link OrgInfoV2 }
     *     
     */
    public OrgInfoV2 getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrgInfoV2 }
     *     
     */
    public void setClient(OrgInfoV2 value) {
        this.client = value;
    }

}

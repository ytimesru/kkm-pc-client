//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.ticket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Квитанция
 * 
 * <p>Java class for TicketType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TicketType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="TicketDate" type="{http://fsrar.ru/WEGAIS/Common}DateWTime" minOccurs="0"/>
 *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TransportId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RegID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocHash" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://fsrar.ru/WEGAIS/Ticket}TicketResultType" minOccurs="0"/>
 *         &lt;element name="OperationResult" type="{http://fsrar.ru/WEGAIS/Ticket}OperationResultType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TicketType", propOrder = {

})
public class TicketType {

    @XmlElement(name = "TicketDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar ticketDate;
    @XmlElement(name = "Identity")
    protected String identity;
    @XmlElement(name = "DocId", required = true)
    protected String docId;
    @XmlElement(name = "TransportId", required = true)
    protected String transportId;
    @XmlElement(name = "RegID")
    protected String regID;
    @XmlElement(name = "DocHash")
    protected String docHash;
    @XmlElement(name = "DocType")
    protected String docType;
    @XmlElement(name = "Result")
    protected TicketResultType result;
    @XmlElement(name = "OperationResult")
    protected OperationResultType operationResult;

    /**
     * Gets the value of the ticketDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTicketDate() {
        return ticketDate;
    }

    /**
     * Sets the value of the ticketDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTicketDate(XMLGregorianCalendar value) {
        this.ticketDate = value;
    }

    /**
     * Gets the value of the identity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Gets the value of the docId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocId() {
        return docId;
    }

    /**
     * Sets the value of the docId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocId(String value) {
        this.docId = value;
    }

    /**
     * Gets the value of the transportId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportId() {
        return transportId;
    }

    /**
     * Sets the value of the transportId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportId(String value) {
        this.transportId = value;
    }

    /**
     * Gets the value of the regID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegID() {
        return regID;
    }

    /**
     * Sets the value of the regID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegID(String value) {
        this.regID = value;
    }

    /**
     * Gets the value of the docHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocHash() {
        return docHash;
    }

    /**
     * Sets the value of the docHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocHash(String value) {
        this.docHash = value;
    }

    /**
     * Gets the value of the docType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets the value of the docType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocType(String value) {
        this.docType = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link TicketResultType }
     *     
     */
    public TicketResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link TicketResultType }
     *     
     */
    public void setResult(TicketResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the operationResult property.
     * 
     * @return
     *     possible object is
     *     {@link OperationResultType }
     *     
     */
    public OperationResultType getOperationResult() {
        return operationResult;
    }

    /**
     * Sets the value of the operationResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationResultType }
     *     
     */
    public void setOperationResult(OperationResultType value) {
        this.operationResult = value;
    }

}

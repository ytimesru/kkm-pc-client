//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.actinventoryabinfo;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Справка А к ТТН
 * 
 * <p>Java class for InformARegType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformARegType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Quantity" type="{http://fsrar.ru/WEGAIS/Common}PositiveDecimalType"/>
 *         &lt;element name="BottlingDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime"/>
 *         &lt;element name="TTNNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="TTNDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime"/>
 *         &lt;element name="EGAISFixNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50" minOccurs="0"/>
 *         &lt;element name="EGAISFixDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformARegType", propOrder = {

})
public class InformARegType {

    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;
    @XmlElement(name = "BottlingDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar bottlingDate;
    @XmlElement(name = "TTNNumber", required = true)
    protected String ttnNumber;
    @XmlElement(name = "TTNDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar ttnDate;
    @XmlElement(name = "EGAISFixNumber")
    protected String egaisFixNumber;
    @XmlElement(name = "EGAISFixDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar egaisFixDate;

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantity(BigDecimal value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the bottlingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBottlingDate() {
        return bottlingDate;
    }

    /**
     * Sets the value of the bottlingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBottlingDate(XMLGregorianCalendar value) {
        this.bottlingDate = value;
    }

    /**
     * Gets the value of the ttnNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTTNNumber() {
        return ttnNumber;
    }

    /**
     * Sets the value of the ttnNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTTNNumber(String value) {
        this.ttnNumber = value;
    }

    /**
     * Gets the value of the ttnDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTTNDate() {
        return ttnDate;
    }

    /**
     * Sets the value of the ttnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTTNDate(XMLGregorianCalendar value) {
        this.ttnDate = value;
    }

    /**
     * Gets the value of the egaisFixNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEGAISFixNumber() {
        return egaisFixNumber;
    }

    /**
     * Sets the value of the egaisFixNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEGAISFixNumber(String value) {
        this.egaisFixNumber = value;
    }

    /**
     * Gets the value of the egaisFixDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEGAISFixDate() {
        return egaisFixDate;
    }

    /**
     * Sets the value of the egaisFixDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEGAISFixDate(XMLGregorianCalendar value) {
        this.egaisFixDate = value;
    }

}

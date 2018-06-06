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
 * Запись в справке Б к ТТН, Информация о отгрузке
 * 
 * <p>Java class for InformBTypeRegItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformBTypeRegItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Identity" type="{http://fsrar.ru/WEGAIS/Common}IdentityType"/>
 *         &lt;element name="TTNNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="TTNDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime"/>
 *         &lt;element name="Quantity" type="{http://fsrar.ru/WEGAIS/Common}PositiveDecimalType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformBTypeRegItem", propOrder = {

})
public class InformBTypeRegItem {

    @XmlElement(name = "Identity", required = true)
    protected String identity;
    @XmlElement(name = "TTNNumber", required = true)
    protected String ttnNumber;
    @XmlElement(name = "TTNDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar ttnDate;
    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;

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

}

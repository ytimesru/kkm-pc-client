//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.repproducedproduct_v3;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.fsrar.wegais.productref_v2.ProductInfoV2;


/**
 * Сырье использованное для производства продукции
 * 
 * <p>Java class for UsedResourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UsedResourceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="IdentityRes" type="{http://fsrar.ru/WEGAIS/Common}IdentityType"/>
 *         &lt;element name="Product" type="{http://fsrar.ru/WEGAIS/ProductRef_v2}ProductInfo_v2"/>
 *         &lt;element name="RegForm2" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50" minOccurs="0"/>
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
@XmlType(name = "UsedResourceType", propOrder = {

})
public class UsedResourceType {

    @XmlElement(name = "IdentityRes", required = true)
    protected String identityRes;
    @XmlElement(name = "Product", required = true)
    protected ProductInfoV2 product;
    @XmlElement(name = "RegForm2")
    protected String regForm2;
    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;

    /**
     * Gets the value of the identityRes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityRes() {
        return identityRes;
    }

    /**
     * Sets the value of the identityRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityRes(String value) {
        this.identityRes = value;
    }

    /**
     * Gets the value of the product property.
     * 
     * @return
     *     possible object is
     *     {@link ProductInfoV2 }
     *     
     */
    public ProductInfoV2 getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductInfoV2 }
     *     
     */
    public void setProduct(ProductInfoV2 value) {
        this.product = value;
    }

    /**
     * Gets the value of the regForm2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegForm2() {
        return regForm2;
    }

    /**
     * Sets the value of the regForm2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegForm2(String value) {
        this.regForm2 = value;
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

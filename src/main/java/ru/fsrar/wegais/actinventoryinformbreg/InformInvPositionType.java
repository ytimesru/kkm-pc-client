//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.actinventoryinformbreg;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InformInvPositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformInvPositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Identity" type="{http://fsrar.ru/WEGAIS/Common}IdentityType"/>
 *         &lt;element name="InformARegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="InformB">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="InformBItem" type="{http://fsrar.ru/WEGAIS/ActInventoryInformBReg}InformInvBRegItem" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformInvPositionType", propOrder = {

})
public class InformInvPositionType {

    @XmlElement(name = "Identity", required = true)
    protected String identity;
    @XmlElement(name = "InformARegId", required = true)
    protected String informARegId;
    @XmlElement(name = "InformB", required = true)
    protected InformInvPositionType.InformB informB;

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
     * Gets the value of the informARegId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformARegId() {
        return informARegId;
    }

    /**
     * Sets the value of the informARegId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformARegId(String value) {
        this.informARegId = value;
    }

    /**
     * Gets the value of the informB property.
     * 
     * @return
     *     possible object is
     *     {@link InformInvPositionType.InformB }
     *     
     */
    public InformInvPositionType.InformB getInformB() {
        return informB;
    }

    /**
     * Sets the value of the informB property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformInvPositionType.InformB }
     *     
     */
    public void setInformB(InformInvPositionType.InformB value) {
        this.informB = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="InformBItem" type="{http://fsrar.ru/WEGAIS/ActInventoryInformBReg}InformInvBRegItem" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "informBItem"
    })
    public static class InformB {

        @XmlElement(name = "InformBItem", required = true)
        protected List<InformInvBRegItem> informBItem;

        /**
         * Gets the value of the informBItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the informBItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInformBItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link InformInvBRegItem }
         * 
         * 
         */
        public List<InformInvBRegItem> getInformBItem() {
            if (informBItem == null) {
                informBItem = new ArrayList<InformInvBRegItem>();
            }
            return this.informBItem;
        }

    }

}

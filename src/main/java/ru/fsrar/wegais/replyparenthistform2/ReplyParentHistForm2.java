//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.replyparenthistform2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Ответ на запрос о движении по форме Б
 * 
 * <p>Java class for ReplyParentHistForm2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplyParentHistForm2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="InformF2RegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50" minOccurs="0"/>
 *         &lt;element name="HistForm2Date" type="{http://fsrar.ru/WEGAIS/Common}DateWTime"/>
 *         &lt;element name="ParentHist" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="step" type="{http://fsrar.ru/WEGAIS/ReplyParentHistForm2}stepBType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ReplyParentHistForm2", propOrder = {

})
public class ReplyParentHistForm2 {

    @XmlElement(name = "InformF2RegId")
    protected String informF2RegId;
    @XmlElement(name = "HistForm2Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar histForm2Date;
    @XmlElement(name = "ParentHist")
    protected ReplyParentHistForm2 .ParentHist parentHist;

    /**
     * Gets the value of the informF2RegId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformF2RegId() {
        return informF2RegId;
    }

    /**
     * Sets the value of the informF2RegId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformF2RegId(String value) {
        this.informF2RegId = value;
    }

    /**
     * Gets the value of the histForm2Date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHistForm2Date() {
        return histForm2Date;
    }

    /**
     * Sets the value of the histForm2Date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHistForm2Date(XMLGregorianCalendar value) {
        this.histForm2Date = value;
    }

    /**
     * Gets the value of the parentHist property.
     * 
     * @return
     *     possible object is
     *     {@link ReplyParentHistForm2 .ParentHist }
     *     
     */
    public ReplyParentHistForm2 .ParentHist getParentHist() {
        return parentHist;
    }

    /**
     * Sets the value of the parentHist property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplyParentHistForm2 .ParentHist }
     *     
     */
    public void setParentHist(ReplyParentHistForm2 .ParentHist value) {
        this.parentHist = value;
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
     *         &lt;element name="step" type="{http://fsrar.ru/WEGAIS/ReplyParentHistForm2}stepBType" maxOccurs="unbounded" minOccurs="0"/>
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
        "step"
    })
    public static class ParentHist {

        protected List<StepBType> step;

        /**
         * Gets the value of the step property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the step property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getStep().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StepBType }
         * 
         * 
         */
        public List<StepBType> getStep() {
            if (step == null) {
                step = new ArrayList<StepBType>();
            }
            return this.step;
        }

    }

}

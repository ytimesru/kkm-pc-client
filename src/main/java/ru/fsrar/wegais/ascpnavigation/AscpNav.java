//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.ascpnavigation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Сведения с автоматических средств контроля положения
 * 
 * <p>Java class for AscpNav complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AscpNav">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Sensor">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="IMEI" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TimeUTC" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Latitude" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Longitude" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="CountSatellite" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Accuracy" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="Course" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="Speed" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="DataLevelGauge">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LevelGauge" type="{http://fsrar.ru/WEGAIS/AscpNavigation}DataType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AscpNav", propOrder = {
    "sensor",
    "timeUTC",
    "latitude",
    "longitude",
    "countSatellite",
    "accuracy",
    "course",
    "speed",
    "dataLevelGauge"
})
public class AscpNav {

    @XmlElement(name = "Sensor", required = true)
    protected AscpNav.Sensor sensor;
    @XmlElement(name = "TimeUTC", required = true)
    protected BigInteger timeUTC;
    @XmlElement(name = "Latitude", required = true)
    protected BigDecimal latitude;
    @XmlElement(name = "Longitude", required = true)
    protected BigDecimal longitude;
    @XmlElement(name = "CountSatellite", required = true)
    protected BigInteger countSatellite;
    @XmlElement(name = "Accuracy", required = true)
    protected BigDecimal accuracy;
    @XmlElement(name = "Course", required = true)
    protected BigInteger course;
    @XmlElement(name = "Speed", required = true)
    protected BigInteger speed;
    @XmlElement(name = "DataLevelGauge", required = true)
    protected AscpNav.DataLevelGauge dataLevelGauge;

    /**
     * Gets the value of the sensor property.
     * 
     * @return
     *     possible object is
     *     {@link AscpNav.Sensor }
     *     
     */
    public AscpNav.Sensor getSensor() {
        return sensor;
    }

    /**
     * Sets the value of the sensor property.
     * 
     * @param value
     *     allowed object is
     *     {@link AscpNav.Sensor }
     *     
     */
    public void setSensor(AscpNav.Sensor value) {
        this.sensor = value;
    }

    /**
     * Gets the value of the timeUTC property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTimeUTC() {
        return timeUTC;
    }

    /**
     * Sets the value of the timeUTC property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTimeUTC(BigInteger value) {
        this.timeUTC = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLatitude(BigDecimal value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLongitude(BigDecimal value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the countSatellite property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCountSatellite() {
        return countSatellite;
    }

    /**
     * Sets the value of the countSatellite property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCountSatellite(BigInteger value) {
        this.countSatellite = value;
    }

    /**
     * Gets the value of the accuracy property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the value of the accuracy property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAccuracy(BigDecimal value) {
        this.accuracy = value;
    }

    /**
     * Gets the value of the course property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCourse() {
        return course;
    }

    /**
     * Sets the value of the course property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCourse(BigInteger value) {
        this.course = value;
    }

    /**
     * Gets the value of the speed property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSpeed() {
        return speed;
    }

    /**
     * Sets the value of the speed property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSpeed(BigInteger value) {
        this.speed = value;
    }

    /**
     * Gets the value of the dataLevelGauge property.
     * 
     * @return
     *     possible object is
     *     {@link AscpNav.DataLevelGauge }
     *     
     */
    public AscpNav.DataLevelGauge getDataLevelGauge() {
        return dataLevelGauge;
    }

    /**
     * Sets the value of the dataLevelGauge property.
     * 
     * @param value
     *     allowed object is
     *     {@link AscpNav.DataLevelGauge }
     *     
     */
    public void setDataLevelGauge(AscpNav.DataLevelGauge value) {
        this.dataLevelGauge = value;
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
     *         &lt;element name="LevelGauge" type="{http://fsrar.ru/WEGAIS/AscpNavigation}DataType" maxOccurs="unbounded"/>
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
        "levelGauge"
    })
    public static class DataLevelGauge {

        @XmlElement(name = "LevelGauge", required = true)
        protected List<DataType> levelGauge;

        /**
         * Gets the value of the levelGauge property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the levelGauge property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLevelGauge().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DataType }
         * 
         * 
         */
        public List<DataType> getLevelGauge() {
            if (levelGauge == null) {
                levelGauge = new ArrayList<DataType>();
            }
            return this.levelGauge;
        }

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
     *       &lt;all>
     *         &lt;element name="IMEI" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString"/>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Sensor {

        @XmlElement(name = "IMEI", required = true)
        protected String imei;

        /**
         * Gets the value of the imei property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIMEI() {
            return imei;
        }

        /**
         * Sets the value of the imei property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIMEI(String value) {
            this.imei = value;
        }

    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.05 at 08:50:12 PM SAMT 
//


package ru.fsrar.wegais.ascpnavigation;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.fsrar.wegais.ascpnavigation package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.fsrar.wegais.ascpnavigation
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AscpNav }
     * 
     */
    public AscpNav createAscpNav() {
        return new AscpNav();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link AscpNav.Sensor }
     * 
     */
    public AscpNav.Sensor createAscpNavSensor() {
        return new AscpNav.Sensor();
    }

    /**
     * Create an instance of {@link AscpNav.DataLevelGauge }
     * 
     */
    public AscpNav.DataLevelGauge createAscpNavDataLevelGauge() {
        return new AscpNav.DataLevelGauge();
    }

}

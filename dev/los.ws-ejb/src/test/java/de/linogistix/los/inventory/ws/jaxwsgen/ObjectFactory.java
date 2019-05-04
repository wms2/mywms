/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.ws.jaxwsgen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.linogistix.los.inventory.ws.jaxwsgen package. 
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

    private final static QName _InventoryException_QNAME = new QName("http://ws.inventory.los.linogistix.de/", "InventoryException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.linogistix.los.inventory.ws.jaxwsgen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InventoryTO }
     * 
     */
    public InventoryTO createInventoryTO() {
        return new InventoryTO();
    }

    /**
     * Create an instance of {@link InventoryException }
     * 
     */
    public InventoryException createInventoryException() {
        return new InventoryException();
    }

    /**
     * Create an instance of {@link InventoryTOArray }
     * 
     */
    public InventoryTOArray createInventoryTOArray() {
        return new InventoryTOArray();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InventoryException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.inventory.los.linogistix.de/", name = "InventoryException")
    public JAXBElement<InventoryException> createInventoryException(InventoryException value) {
        return new JAXBElement<InventoryException>(_InventoryException_QNAME, InventoryException.class, null, value);
    }

}

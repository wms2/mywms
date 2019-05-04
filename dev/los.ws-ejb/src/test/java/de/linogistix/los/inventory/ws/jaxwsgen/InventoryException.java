/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.ws.jaxwsgen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InventoryException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InventoryException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bundleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bundleResolver" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inventoryExceptionKey" type="{http://ws.inventory.los.linogistix.de/}inventoryExceptionKey" minOccurs="0"/>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parameters" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryException", namespace = "http://ws.inventory.los.linogistix.de/", propOrder = {
    "bundleName",
    "bundleResolver",
    "inventoryExceptionKey",
    "key",
    "message",
    "parameters"
})
public class InventoryException {

    protected String bundleName;
    protected String bundleResolver;
    protected InventoryExceptionKey inventoryExceptionKey;
    protected String key;
    protected String message;
    protected List<Object> parameters;

    /**
     * Gets the value of the bundleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * Sets the value of the bundleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBundleName(String value) {
        this.bundleName = value;
    }

    /**
     * Gets the value of the bundleResolver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBundleResolver() {
        return bundleResolver;
    }

    /**
     * Sets the value of the bundleResolver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBundleResolver(String value) {
        this.bundleResolver = value;
    }

    /**
     * Gets the value of the inventoryExceptionKey property.
     * 
     * @return
     *     possible object is
     *     {@link InventoryExceptionKey }
     *     
     */
    public InventoryExceptionKey getInventoryExceptionKey() {
        return inventoryExceptionKey;
    }

    /**
     * Sets the value of the inventoryExceptionKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventoryExceptionKey }
     *     
     */
    public void setInventoryExceptionKey(InventoryExceptionKey value) {
        this.inventoryExceptionKey = value;
    }

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Object>();
        }
        return this.parameters;
    }

}

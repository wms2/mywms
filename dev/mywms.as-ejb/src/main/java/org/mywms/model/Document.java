/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Lob;

import org.mywms.globals.DocumentTypes;

/**
 * This business object contains the information regarding documents
 * held by the myWMS system. The basic properties of a document are:
 * <ul>
 * <li> document name
 * <li> document type
 * <li> creation date
 * <li> modification date
 * </ul>
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name="mywms_document", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name", "client_id"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Document
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String name = null;
    private String type = DocumentTypes.TEXT_PLAIN.toString();
    private int size = 0;
    private byte[] document = new byte[0];

    /**
     * @return Returns the document.
     */
    @Column(nullable = false)
    @Lob
    public byte[] getDocument() {
        return this.document;
    }

    /**
     * @param document The document to set.
     */
    public void setDocument(byte[] document) {
        this.document = document;
        size = document.length;
    }

    /**
     * The name of the document represents the filename. The document
     * name must be unique. Automatical generated documents must ensure
     * to create unique document names.
     * 
     * @return Returns the name.
     */
    @Column(nullable = false)
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the size.
     */
    @Column(name="document_size")
    public int getSize() {
        return this.size;
    }

    /**
     * @param size The size to set.
     */
    @SuppressWarnings("unused")
    private void setSize(int size) {
        this.size = size;
    }

    /**
     * @return Returns the type.
     */
    @Column(nullable = false)
    public String getType() {
        return this.type;
    }

    /**
     * Setter for property type. The type is encoded as in http. Some
     * typical examples are:
     * <ul>
     * <li> <code>application/pdf</code> for PDF documents
     * <li> <code>application/msword</code> for MS-Word documents
     * <li> <code>application/rtf</code> for RTF documents
     * <li> <code>application/rtf</code> for RTF documents
     * <li> <code>image/bmp</code>
     * <li> <code>image/jpg</code>
     * <li> <code>image/png</code>
     * <li> <code>text/html</code>
     * <li> <code>text/plain</code>
     * <li> <code>text/rtf</code>
     * <li> <code>text/xml</code>
     * </ul>
     * 
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toUniqueString() {
        return getName();
    }
    
    
}

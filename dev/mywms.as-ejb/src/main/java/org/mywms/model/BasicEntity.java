/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * This class implements the basic entity used in the myWMS project. It
 * contains a created timestamp and a system unique id. It also includes
 * a version, used by the persistence layer to support optimistic
 * transactions.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@MappedSuperclass
public class BasicEntity
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private Long id;

    private int version;

    private Date created = new Date();

    private Date modified = new Date();

    private int lock = 0;

    private String additionalContent = null;

    public BasicEntity(){
    	
    }
    
    public BasicEntity(Long id){
    	setId(id);
    }
    
    /**
     * The id of an entity is an imutable and unique identifier of an
     * entity. Ids are used by the underlying persistence engine to
     * identify an entity.
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqEntities")
    @SequenceGenerator(name = "seqEntities", sequenceName = "seqEntities")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The creation date is an imutable date, filled during
     * instantiation of the entity.
     * 
     * @return Returns the created.
     */
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return this.created;
    }

    protected void setCreated(Date created) {
        this.created = created;
    }

    /**
     * The version is a technical feature, used by the underlying
     * persinstance engines, to support optimistic transactional
     * behaviour. Whenever a Entity is merged back to from outer
     * changes, the change-operation fails, if the version number has
     * been changed meanwhile. On the other hand: if the version of the
     * entity is the same as before the change, it is for shure, that
     * the entity has not been changed meanwhile.
     * 
     * @return Returns the version.
     */
    @Version
    public int getVersion() {
        return this.version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    /**
     * @see #setLock(int)
     * @return Returns the lock.
     */
    @Column(name="entity_lock")
    public int getLock() {
        return this.lock;
    }

    /**
     * Sets the lock of an entity. Locking an entity is a business
     * operation and not a technical feature: For instance, a locked
     * StorageLocation must not be used for warehouse operations. The
     * individual implementation may implement its own set of locks.
     * Also, not in all entities the lock is usefull. (e.g. LogItem)
     * 
     * @param lock The lock to set.
     */
    public void setLock(int lock) {
        this.lock = lock;
    }

    /**
     * Checks if the entity is locked.
     * 
     * @return true, if the entity is locked
     */
    @Transient
    public boolean isLocked() {
        return lock != 0;
    }

    /**
     * @return Returns the modified.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getModified() {
        return this.modified;
    }

    protected void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * A callback method, used to update the modified date during update
     * operations regarding this entity.
     */
    @PrePersist
    @PreUpdate
    protected void updateModifiedDate() {
        modified = new Date();
    }

    /**
     * Returns some additional content stored to this entity, if
     * available. Content should be stored and expected using a XML
     * format.
     * 
     * @return Returns the additionalContent.
     */
    public String getAdditionalContent() {
        return this.additionalContent;
    }

    /**
     * @see #getAdditionalContent()
     * @param additionalContent The additionalContent to set.
     */
    public void setAdditionalContent(String additionalContent) {
        this.additionalContent = additionalContent;
    }

    @Override
    public String toString() {
        return toUniqueString();
    }

    /**
     * Override in extended classes to return a unique string
     * representation of the entity that is human readable, e.g. an
     * order number. Typically this is a property annotated with
     * <code>@Column(nullable=false, unique=true)</code>.
     * @return a unique string representation of the entity
     */
    public String toUniqueString() {
    	return (getId()==null?"":getId().toString());
    }

    /**
     * Returns a description of this entity, basically all properties as
     * key-value-pairs. Override in extended classes to gain
     * performance. This method uses reflections.
     * 
     * @return a description of this entity
     */
    public String toDescriptiveString() {
        StringBuffer b = new StringBuffer();
        try {
            BeanInfo info = Introspector.getBeanInfo(getClass());
            java.beans.PropertyDescriptor[] d = info.getPropertyDescriptors();

            b.append(getClass().getSimpleName());
            b.append(": ");

            for (int i = 0; i < d.length; i++) {
                try {
                	
                	if (d[i].getName().equals("class")){
                		continue;
                	}
                    b.append("[");
                    b.append(d[i].getName());
                    b.append("=");
                    try {
                        b.append(d[i].getReadMethod().invoke(
                            this,
                            new Object[0]).toString());
                    }
                    catch (Throwable t) {
                        b.append("?");
                    }
                    b.append("]");
                }
                catch (Throwable ex) {
                    continue;
                }
            }
            return new String(b);
        }
        catch (Throwable t) {
            return super.toString();
        }
    }

    /**
     * Returns a shot description of this entity, basically no properties as
     * key-value-pairs. Override in extended classes to gain
     * performance. This method gives not much information.
     * 
     * @return a description of this entity
     */
    public String toShortString() {
    	return getClass().getSimpleName() + ": [id=" + getId() + "]";
    }

    @Override
    /**
     * Two objects with the same id have the same hashCode
     */
    public int hashCode() {
    	if( getId()!=null ) {
    		return getId().hashCode();
    	}
    	return 0;
    }

    /**
     * @return true if obj has the same id as this instance
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        // In list operations with lazy loaded entities, you may get a proxy with the correct id but a different class  
        else if ( obj instanceof BasicEntity ) {
            BasicEntity entity = (BasicEntity) obj;
            
            if( getId()==null || entity.getId()==null ) {
            	return false;
            }
            if (entity.getId().equals(getId())) {
                return true;
            }
            return false;
        }
        
        return false;
    }

    
    /**
	 * Adds the content. Preserves existing content!
	 * @param s
	 */
	public void addAdditionalContent(String s){
		
		String prev = getAdditionalContent()==null?"":getAdditionalContent();
		
		setAdditionalContent(s + "\n--- "+ new SimpleDateFormat().format(new Date()) +"  ----\n\n" + prev);
	}
}

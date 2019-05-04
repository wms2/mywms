package org.mywms.model;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BasicClientAssignedEntity
    extends BasicEntity
{

    private static final long serialVersionUID = 1L;

    private Client client = null;

    /**
     * @see #setClient(Client)
     * @return Returns the client.
     */
    @ManyToOne(optional = false)
    public Client getClient() {
        return this.client;
    }

    /**
     * Identifies the owner (client/Mandant) of the entity. The client
     * refers to the multi-warehouse capabilities of myWMS.
     * 
     * @param client The client to set.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Returns a shot description of this entity, basically no properties as
     * key-value-pairs. Override in extended classes to gain
     * performance. This method gives not much information.
     * 
     * @return a description of this entity
     */
    public String toShortString() {
    	return super.toShortString() + "[client=" + client.getNumber() + "]";
    }

}

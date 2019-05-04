/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.bo.BONode;
import java.util.List;
import org.mywms.model.BasicEntity;

/**
 *
 * Holds a given set of entites - does not query at all
 *
 * @author andreas
 */
public class BOQueryEntityListModel extends BOQueryModel<BasicEntity>{

    private List entities;

    public BOQueryEntityListModel(BONode node, List entities){
        super(node);
        this.entities = entities;
        setResultSetSize(entities.size());
    }

    /**
     * @return the entities
     */
    public List getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List entities) {
        this.entities = entities;
        setResultSetSize(entities.size());
    }


}

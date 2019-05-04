/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;

/**
 * @see de.linogistix.los.location.entityservice.LOSRackService
 *  
 * @author Markus Jordan
 * @version $Revision: 320 $ provided by $Author: jordan $
 */
@Stateless 
public class LOSRackServiceBean 
	extends BasicServiceBean<LOSRack> 
	implements LOSRackService 
{	
    @EJB
    private LOSStorageLocationService slService;
	@EJB
	private EntityGenerator entityGenerator;
	
    public LOSRack createRackWithLocations(Client client, String name, int rows, int columns, LOSStorageLocationType type)
            throws UniqueConstraintViolatedException
    {
        LOSRack rack = createRack(client, name);
        rack.setNumberOfRows(rows);
        rack.setNumberOfColumns(columns);
        
        for (int r=1;r<=rows;r++){
            for (int c=1;c<=columns;c++){
                createRackLocation(client, name, type, c, r, rack);
            }
        }
        
        return rack;
    }
        
    public LOSRack createRack(Client client, String name)
            throws UniqueConstraintViolatedException
    {
        if (client == null || name == null) {
            throw new NullPointerException(
                    "createStorageLocation: parameter == null");
        }
        
        client = manager.merge(client);
        
        try{
            getByName(client, name);
            
            // TODO: erweiterung der ServiceExceptionKeys
            throw new UniqueConstraintViolatedException(
                    ServiceExceptionKey.STORAGELOCATION_NAME_NOT_UNIQUE);
            
        }catch(EntityNotFoundException enf){}
        
        LOSRack rack = entityGenerator.generateEntity(LOSRack.class);
        rack.setClient(client);
        rack.setName(name);
        
        manager.persist(rack);
        
        return rack;
    }
    
    //------------------------------------------------------------------------
    public LOSStorageLocation createRackLocation(Client client, 
                                              String name, 
                                              LOSStorageLocationType type,
                                              int xPos,
                                              int yPos,
                                              LOSRack rack) 
    {
        if ((client == null || name == null) || (type == null || rack == null)) 
        {
            throw new NullPointerException(
                    "createRackLocation: parameter == null");
        }
        
        client = manager.merge(client);
        type = manager.merge(type);
        rack = manager.merge(rack);
        
        // TODO : SpaltenNr und ZeilenNr pruefen
        
        LOSStorageLocation rl = entityGenerator.generateEntity(LOSStorageLocation.class);
        rl.setClient(client);
        rl.setName(name);
        rl.setType(type);
        rl.setXPos(xPos);
        rl.setYPos(yPos);
        rl.setZPos(1);
        rl.setRack(rack);
        
        
        manager.persist(rl);
        
        return rl;
    }

    //------------------------------------------------------------------------
    public LOSRack getByName(Client client, String name) 
            throws EntityNotFoundException 
    {    
        Query query = manager.createQuery("SELECT r FROM "
                        + LOSRack.class.getSimpleName()
                        + " r "
                        + "WHERE r.name=:name "
                        + "AND r.client=:cl");

        query.setParameter("name", name);
        query.setParameter("cl", client);

        try {
            LOSRack r = (LOSRack) query.getSingleResult();
            return r;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }
    }

    //------------------------------------------------------------------------
    public LOSStorageLocation getByPosition(LOSRack r, int x, int y, int z) {
        
    	StringBuffer sb = new StringBuffer("SELECT rl FROM ");
    	sb.append(LOSStorageLocation.class.getSimpleName()+" rl ");
    	sb.append("WHERE rl.rack=:rack ");
    	sb.append("AND rl.XPos=:x ");
    	sb.append("AND rl.YPos=:y ");
    	sb.append("AND rl.ZPos=:z");
    	
        Query query = manager.createQuery(sb.toString());
        
        query.setParameter("rack", r);
        query.setParameter("x", x);
        query.setParameter("y", y);
        query.setParameter("z", z);
       
        try{
        	LOSStorageLocation rl = (LOSStorageLocation)query.getSingleResult();
	        return rl;    
        }catch(NoResultException nre){
        	return null;
        }
    }

    //------------------------------------------------------------------------
    public List<LOSStorageLocation> getFreePlacesForType(LOSRack r, UnitLoadType ulType) 
    {
		
		return null;
	}
    
    //------------------------------------------------------------------------
    @Override
    public void deleteAll() {
    	slService.deleteAll();

        Query query = manager.createQuery(
                        "DELETE FROM "+LOSRack.class.getSimpleName());
        query.executeUpdate();
    }
    
    
    
    @SuppressWarnings("unchecked")
	public List<LOSRack> getByAisle(String aisle) {
		String queryStr = "SELECT o FROM "+LOSRack.class.getSimpleName()+" o WHERE aisle=:aisle"; 

        Query query = manager.createQuery(queryStr);
	    query.setParameter("aisle", aisle);
	
		return query.getResultList();
    }

}

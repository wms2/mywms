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
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.location.Area;
import de.wms2.mywms.location.AreaEntityService;
import de.wms2.mywms.location.AreaUsages;

/**
 * 
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Stateless
public class LOSAreaServiceBean extends BasicServiceBean<Area> implements LOSAreaService, LOSAreaServiceRemote {
	public final static String PROPERTY_KEY_AREA_DEFAULT = "AREA_DEFAULT";

	@EJB
	private LOSSystemPropertyService propertyService;
	@Inject
	private ClientBusiness clientService;
	@EJB
	private EntityGenerator entityGenerator;
	@Inject
	private AreaEntityService areaEntityService;

	public Area createLOSArea(Client c, String name) {
		Area a = entityGenerator.generateEntity(Area.class);
		a.setName(name);

		manager.persist(a);
		manager.flush();

		return a;
	}

	public Area getByName(Client client, String name) throws EntityNotFoundException {
		String queryStr = "SELECT o FROM " + Area.class.getSimpleName() + " o " + "WHERE o.name=:name";
		Query query = manager.createQuery(queryStr);

		query.setParameter("name", name);

		try {
			Area area = (Area) query.getSingleResult();
			return area;
		} catch (NoResultException ex) {
			throw new EntityNotFoundException(ServiceExceptionKey.NO_AREA_WITH_NAME);
		}
	}

	public List<Area> getForGoodsIn() {
		return areaEntityService.getForGoodsIn();
	}

	public List<Area> getForGoodsOut() {
		return areaEntityService.getForGoodsOut();
	}

	public List<Area> getForStorage() {
		return areaEntityService.getForStorage();
	}

	public List<Area> getForPicking() {
		return areaEntityService.getForPicking();
	}

	public List<Area> getForTransfer() {
		return areaEntityService.getForTransfer();
	}

	public Area getDefault() {
		String name = propertyService.getStringDefault(PROPERTY_KEY_AREA_DEFAULT, "Default");

		Client client = clientService.getSystemClient();
		Area area = null;
		try {
			area = getByName(client, name);
		} catch (EntityNotFoundException e) {
		}

		if (area == null) {
			area = createLOSArea(client, name);
			area.setUseFor(AreaUsages.PICKING, true);
			area.setUseFor(AreaUsages.STORAGE, true);
		}

		return area;
	}

}

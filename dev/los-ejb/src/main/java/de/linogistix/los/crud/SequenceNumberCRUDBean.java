package de.linogistix.los.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.entityservice.SequenceNumberService;
import de.wms2.mywms.sequence.SequenceNumber;




/**
 * @author krane
 *
 */
@Stateless
public class SequenceNumberCRUDBean extends BusinessObjectCRUDBean<SequenceNumber> implements SequenceNumberCRUDRemote {

	@EJB 
	private SequenceNumberService service;
	
	@Override
	protected BasicService<SequenceNumber> getBasicService() {
		return service;
	}
}

package de.linogistix.los.entityservice;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
@Stateless
public class SequenceNumberServiceBean extends BasicServiceBean<SequenceNumber> implements SequenceNumberService {
}

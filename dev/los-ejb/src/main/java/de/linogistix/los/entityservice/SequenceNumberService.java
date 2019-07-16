package de.linogistix.los.entityservice;

import javax.ejb.Local;

import org.mywms.service.BasicService;

import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
@Local
public interface SequenceNumberService extends BasicService<SequenceNumber> {
}

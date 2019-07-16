package de.linogistix.los.crud;

import javax.ejb.Remote;

import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
@Remote
public interface SequenceNumberCRUDRemote extends BusinessObjectCRUDRemote<SequenceNumber> {
}

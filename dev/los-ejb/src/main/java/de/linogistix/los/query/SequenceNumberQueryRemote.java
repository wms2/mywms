package de.linogistix.los.query;

import javax.ejb.Remote;

import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
@Remote
public interface SequenceNumberQueryRemote extends BusinessObjectQueryRemote<SequenceNumber> {
}

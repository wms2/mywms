package de.linogistix.los.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.sequence.SequenceNumber;

/**
 * @author krane
 *
 */
public class SequenceNumberTO extends BODTO<SequenceNumber> {

	private static final long serialVersionUID = 1L;

	private String name;
	private long counter = 0;
	private long endCounter = 999999;
	private String format;

	public SequenceNumberTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public SequenceNumberTO(Long id, int version, String name, String format, long counter, long endCounter) {
		super(id, version, name);
		this.name = name;
		this.format = format;
		this.counter = counter;
		this.endCounter = endCounter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public long getEndCounter() {
		return endCounter;
	}

	public void setEndCounter(long endCounter) {
		this.endCounter = endCounter;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}

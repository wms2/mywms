package org.mywms.globals;

public enum SerialNoRecordType {

	/**
	 * Serial numbers will not be recorded during processes.
	 */
	NO_RECORD,
	/**
	 * Serial numbers will only be recorded during goods out process.
	 */
	GOODS_OUT_RECORD,
	/**
	 * Serial numbers will be recorded during all processes.
	 */
	ALWAYS_RECORD
}

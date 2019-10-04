/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.sequence;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;

/**
 * Business service to handle sequences
 * 
 * @author krane
 *
 */
@Stateless
public class SequenceBusiness {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final static long MAX_TRIES = 1000;

	@Inject
	private GenericEntityService entityService;

	@Inject
	private SequenceTransactionService sequenceEngine;

	/**
	 * Reads the next sequence number of the given sequence. If the sequence does
	 * not exist it will be generated with default-values.
	 * <p>
	 * The generated number is validated with an exists query on entityType,
	 * attributeName and the generated number. Only if no entity exists the number
	 * is returned.
	 * <p>
	 * If it is not possible to generate a number after several tries, an Exception
	 * is thrown.
	 * 
	 * @param name          Name of the sequence
	 * @param entityType    Class of the entity for validation
	 * @param attributeName Attribute name of the entity for validation
	 * @throws BusinessException
	 */
	public String readNextValue(String name, Class<? extends BasicEntity> entityType, String attributeName) {
		String logStr = "readNextSequenceNumber ";

		for (int i = 0; i < MAX_TRIES; i++) {
			String number = readNextValue(name);

			if (!entityService.exists(entityType, attributeName, number)) {
				return number;
			}
		}

		logger.log(Level.WARNING, logStr + "Cannot generate sequence number. sequence=" + name
				+ ", validation entityType=" + entityType + ", validation attributeName=" + attributeName);
		throw new RuntimeException("Generation of sequencenumber failed");
	}

	/**
	 * Reads the next sequence value of the given sequence.
	 * <p>
	 * If the sequence does not exist it will be generated. Handled in a new
	 * transaction.
	 * 
	 * @param name The name of the sequence
	 */
	public String readNextValue(String name) {
		String logStr = "readNextValue ";

		long tries = 0;
		String nextValue = null;
		do {
			try {
				nextValue = sequenceEngine.readNextValue(name);
			} catch (EJBException e) {
				if (e.getCausedByException() instanceof OptimisticLockException) {
					logger.log(Level.WARNING, logStr + "OptimisticLockException. Try again...");
					tries++;
				} else {
					logger.log(Level.SEVERE,
							logStr + "Exception occured. " + e.getClass().getName() + ", " + e.getMessage(), e);
					throw new RuntimeException("Generation of sequencenumber failed");
				}
			}

		} while (nextValue == null && tries < MAX_TRIES);

		if (nextValue == null) {
			logger.log(Level.SEVERE,
					logStr + "Sequence name=" + name + " exceeded maxTries <" + MAX_TRIES + "> attempts");
			throw new RuntimeException("Generation of sequencenumber failed");
		}

		return nextValue;
	}

	/**
	 * Generates the next value with the given base value.
	 * <p>
	 * To the base value a '-i' is added, where i is a counter that starts with
	 * 'startCounter'. The generated number is validated with an exists query on
	 * entityType, attributeName and the generated number. Only if no entity exists
	 * the number is returned.
	 * <p>
	 * If it is not possible to generate a number after several tries, an Exception
	 * is thrown.
	 * 
	 * @param baseValue
	 * @param startCounter
	 * @param entityType    Class of the entity for validation
	 * @param attributeName Attribute name of the entity for validation
	 * @throws BusinessException
	 */
	public String readNextCounterValue(String baseValue, int startCounter, Class<? extends BasicEntity> entityType,
			String attributeName) {
		String logStr = "readNextCounterValue ";

		for (int i = 0; i < MAX_TRIES; i++) {
			String number = String.format("%s-%d", baseValue, (startCounter + i));

			if (!entityService.exists(entityType, attributeName, number)) {
				return number;
			}
		}

		logger.log(Level.WARNING, logStr + "Cannot generate sequence number. baseNumber=" + baseValue
				+ ", validation entityType=" + entityType + ", validation attributeName=" + attributeName);
		throw new RuntimeException("Generation of sequencenumber failed");
	}

	public SequenceNumber createNotExisting(String name, String format, long startValue, long endValue)
			throws BusinessException {
		return sequenceEngine.createNotExisting(name, format, startValue, endValue);
	}

	public SequenceNumber read(String name) {
		return sequenceEngine.read(name);
	}

}

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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;

/**
 * Separate service to handle sequence access in separate transactions.
 * <p>
 * The normal access to the sequences is done with the sequence business
 * service.
 * 
 * @author krane
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SequenceTransactionService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	public String readNextValue(String name) {
		String logStr = "getNextValue ";

		SequenceNumber sequence = read(name);
		if (sequence == null) {
			logger.log(Level.INFO, logStr + "Generate a new sequence. name=" + name);
			sequence = manager.createInstance(SequenceNumber.class);
			sequence.setName(name);
			manager.persist(sequence);
		}

		// check the counter
		long currentCounter = sequence.getCounter();
		currentCounter += 1;
		if (currentCounter > sequence.getEndCounter()) {
			logger.log(Level.WARNING, logStr + "Sequence exceeded limit. Cannot get a new value. name=" + name
					+ ", endCounter=" + sequence.getEndCounter());
			throw new RuntimeException("Sequence (" + name + ") is exceeded");
		}
		sequence.setCounter(currentCounter);

		// check format definition
		String format = sequence.getFormat();
		if (StringUtils.isBlank(format)) {
			format = "%1$06d";
			sequence.setFormat(format);
		}

		try {
			String value = String.format(format, currentCounter, new Date());
			return value;

		} catch (Throwable t) {
			logger.log(Level.SEVERE,
					logStr + "Error formating number. EX=" + t.getClass().getName() + ", " + t.getMessage());
		}

		return "" + currentCounter;
	}

	public SequenceNumber createNotExisting(String name, String format, long startValue, long endValue)
			throws BusinessException {
		SequenceNumber sequence = read(name);
		if (sequence == null) {
			sequence = manager.createInstance(SequenceNumber.class);
			sequence.setName(name);
			sequence.setFormat(format);
			sequence.setEndCounter(endValue);
			sequence.setCounter(startValue);
			manager.persistValidated(sequence);
		}

		return sequence;
	}

	public SequenceNumber read(String name) {
		String jpql = "select entity FROM " + SequenceNumber.class.getName() + " entity ";
		jpql += " where entity.name = :name ";
		Query query = manager.createQuery(jpql);
		query.setParameter("name", name);

		try {
			SequenceNumber sequence = (SequenceNumber) query.getSingleResult();
			manager.lock(sequence, LockModeType.WRITE);
			return sequence;
		} catch (NoResultException e) {
		}
		return null;
	}
}

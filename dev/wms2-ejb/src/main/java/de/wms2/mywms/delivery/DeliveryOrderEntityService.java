/* 
Copyright 2019 Matthias Krane
info@krane.engineer

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
package de.wms2.mywms.delivery;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.wms2.mywms.client.ClientBusiness;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.sequence.SequenceBusiness;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;

/**
 *
 * @author krane
 */
@Stateless
public class DeliveryOrderEntityService {

	@Inject
	private OrderStrategyEntityService orderStratService;
	@Inject
	private PersistenceManager manager;
	@Inject
	private ClientBusiness clientBusiness;
	@Inject
	private SequenceBusiness sequenceBusiness;

	public DeliveryOrder create(Client client, OrderStrategy strat) throws BusinessException {
		if (client == null) {
			client = clientBusiness.getCurrentUsersClient();
		}
		if (strat == null) {
			strat = orderStratService.getDefault(client);
		}

		String number = sequenceBusiness.readNextValue(DeliveryOrder.class, "orderNumber");

		DeliveryOrder order = manager.createInstance(DeliveryOrder.class);
		order.setOrderNumber(number);
		order.setClient(client);
		order.setOrderStrategy(strat);
		manager.persist(order);
		manager.flush();

		order.setLines(new ArrayList<DeliveryOrderLine>());

		return order;
	}

	public DeliveryOrder readByOrderNumber(String orderNumber) {
		String jpql = "SELECT entity from " + DeliveryOrder.class.getName() + " entity ";
		jpql += " WHERE entity.orderNumber=:orderNumber";
		Query query = manager.createQuery(jpql);
		query.setParameter("orderNumber", orderNumber);
		try {
			return (DeliveryOrder) query.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

}

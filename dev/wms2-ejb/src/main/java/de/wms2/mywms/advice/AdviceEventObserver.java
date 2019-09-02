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
package de.wms2.mywms.advice;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLineCollectEvent;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLineDeletedEvent;

/**
 * @author krane
 *
 */
@Stateless
public class AdviceEventObserver {

	@Inject
	private AdviceBusiness adviceBusiness;

	public void listen(@Observes GoodsReceiptLineCollectEvent event) throws BusinessException {
		GoodsReceiptLine goodsReceiptLine = event.getGoodsReceiptLine();
		if (goodsReceiptLine == null) {
			return;
		}
		AdviceLine adviceLine = goodsReceiptLine.getAdviceLine();
		if (adviceLine == null) {
			return;
		}

		BigDecimal amount = goodsReceiptLine.getAmount();
		adviceBusiness.checkReceiveAmount(adviceLine, amount);
		adviceBusiness.addConfirmedAmount(adviceLine, amount);
	}

	public void listen(@Observes GoodsReceiptLineDeletedEvent event) throws BusinessException {
		GoodsReceiptLine goodsReceiptLine = event.getGoodsReceiptLine();
		if (goodsReceiptLine == null) {
			return;
		}
		AdviceLine adviceLine = event.getAdviceLine();
		if (adviceLine == null) {
			return;
		}
		BigDecimal amount = goodsReceiptLine.getAmount();
		adviceBusiness.removeConfirmedAmount(adviceLine, amount);
	}
}

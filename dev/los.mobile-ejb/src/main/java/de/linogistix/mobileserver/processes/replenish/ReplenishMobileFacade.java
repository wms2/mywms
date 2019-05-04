package de.linogistix.mobileserver.processes.replenish;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remote;
import javax.faces.model.SelectItem;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

@Remote
public interface ReplenishMobileFacade {
	
	public Client getDefaultClient();
	public List<SelectItem> getCalculatedOrders( String code );
	public ReplenishMobileOrder loadOrderById(long id);
	public ReplenishMobileOrder getReservedOrder() throws FacadeException;
//	public void readAddOn(ReplenishMobileOrder order);
	public void startOrder( ReplenishMobileOrder order ) throws FacadeException;
	public void resetOrder( ReplenishMobileOrder order ) throws FacadeException ;
	public ReplenishMobileOrder checkSource( ReplenishMobileOrder order, String code ) throws FacadeException;
	public ReplenishMobileOrder checkDestination( ReplenishMobileOrder order, String code ) throws FacadeException;
	public void checkAmountPicked(ReplenishMobileOrder order, BigDecimal amount ) throws FacadeException;;
	public void confirmOrder( ReplenishMobileOrder order ) throws FacadeException;

	

	public ReplenishMobileOrder loadOrderByDestination( String locationName ) throws FacadeException;
	public ReplenishMobileOrder requestReplenish(ReplenishMobileOrder mOrder) throws FacadeException;

}

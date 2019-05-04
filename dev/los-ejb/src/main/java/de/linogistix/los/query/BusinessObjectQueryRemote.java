/*
 * BOTopologyService.java
 *
 * Created on 13. September 2006, 11:46
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.query;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;

import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;


/**
 * Interface defining a set of methods/services that can be used on any type of
 * BusinessObject to query instances of this type.
 * <p>
 * Base interface that can be extended to add additional query services for a type of 
 * BusinessObject.  
 * <p>
 * Convention for extended interfaces is as follows:
 *
 *<ul>
 *<li> Names of query methods must start with <I>query</I>. That is important for some clients to 
 *     get all query services by reflection.
 *<li> Any parameter must be of primitive type or of type <code>TemplateQuery</code> 
 *     or of type <code>QueryDetail</code>
 *<li> A method can have none, one or more parameters
 *<li> Return values must be of type <code>T</code> or <code>List<T></code>
 *<li> Every method should declare to throw Subclass of <code>ServiceException</code>
 *</ul>
 *
 *@see TemplateQuery
 *@see QueryDetail
 *
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
//@WebService
//@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface BusinessObjectQueryRemote<T extends BasicEntity> extends java.rmi.Remote{

	/**
	 * Query BusinessObject by Id. Contract is that entities are returned
     * "eager", i.e. with fully inititialized members.
	 * 
	 * @param ID the database id (primary key)
	 * @return The BusinessObject with the given ID or null
	 * @throws BusinessObjectNotFoundException
	 */
  T queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException;
  
  /**
   * Returns a List of {@link BODTO} containing elements with given ids.
   * 
   * @param ids the ids to retrieve
   * @param detail
   * @return
 * @throws BusinessObjectQueryException 
 * @throws BusinessObjectNotFoundException 
   */
  LOSResultList<BODTO<T>> queryHandlesById(List<Long> ids, QueryDetail detail) throws BusinessObjectNotFoundException, BusinessObjectQueryException;
  
  /**
   * Query BusinessObjects by template. Elemenets of returned List might be lazily fetched, i.e. null! 
   *
   * @param detail QueryDetail
   * @param query a TemplateQuery
   * @throws BusinessObjectNotFoundException
   * @return a list of BusinessObejcts matching the template or empty list 
   */
  LOSResultList<T> queryByTemplate(QueryDetail detail, TemplateQuery query) 
    throws BusinessObjectNotFoundException, BusinessObjectQueryException; 
  
  /**
   * Query BusinessObjects by template. Elemenets of returned List might be lazily fetched, i.e. null! 
   *
   * @param detail QueryDetail
   * @param query a TemplateQuery
   * @throws BusinessObjectNotFoundException
   * @return a list of BusinessObejcts matching the template or empty list 
   */
  LOSResultList<BODTO<T>> queryByTemplateHandles(QueryDetail detail, TemplateQuery query) 
    throws BusinessObjectNotFoundException, BusinessObjectQueryException; 
  
  
  /**
   * Query all BusinessObjects. Elemenets of returned List might be lazily fetched, i.e. having members 
   * that are not inititalized
   * 
   * @param detail narrowing the result set (max number) and <code>order by</code> information
   * @return a list of all BusinessObjects
   */
  LOSResultList<T> queryAll(QueryDetail detail) throws BusinessObjectQueryException;
  
  /**
   * Instead of retrieving large amounts of "real" entities it is faster to 
   * retrieve handels as placeholder.
   *
   *
   * @param detail QueryDetail
   * @param query a TemplateQuery
   * @throws BusinessObjectNotFoundException
   */

  LOSResultList<BODTO<T>> queryAllHandles(QueryDetail detail) throws BusinessObjectQueryException; 
  
  /**
   * Query BusinessObject by logical identity. Contract is that entities are returnef
   * "eager", i.e. with fully inititialized members.
   *
   * @param identity an logical unique id, e.g. name, id
   * @return
   */
  T queryByIdentity(String identity) throws BusinessObjectNotFoundException;
  
  /**
   * Some entities are only unique by an attribute in conjunction with the client (i.e. ItemData, Lot)
   * 
   * @param c
   * @param identity
   * @return
   * @throws BusinessObjectNotFoundException
   */
  public List<T> queryByIdentity(Client c, String identity)  throws BusinessObjectNotFoundException;
  
  
  /**
   * 
   * Can be used in autocompletion fields.
   * 
   * @param typed
   * @return {@link BODTO}s that match the given String
   */
   LOSResultList<BODTO<T>> autoCompletion(String typed);
   LOSResultList<BODTO<T>> autoCompletion(String typed, boolean count);
   LOSResultList<BODTO<T>> autoCompletion(String typed, QueryDetail det);
   LOSResultList<BODTO<T>> autoCompletion(String typed, String[] filtered, QueryDetail det);

  
  /**
   * 
   * Can be used in autocompletion fields.
   * 
   * Restricts result set to given {@link Client}
   * 
   * @param typed
   * @param Client Client
   * @return {@link BODTO}s that match the given String
   */
   LOSResultList<BODTO<T>> autoCompletion(String typed, Client client);
   LOSResultList<BODTO<T>> autoCompletion(String typed, Client client, QueryDetail det);
   LOSResultList<BODTO<T>> autoCompletion(String typed, Client client, String[] filtered, QueryDetail det);
  
  
   LOSResultList<BODTO<T>> autoCompletion(String typed, Client client, TemplateQueryWhereToken[] tokens);
   LOSResultList<BODTO<T>> autoCompletion(String typed, Client client, TemplateQueryWhereToken[] tokens, QueryDetail det);
  
  /**
   * The name of the property that holds a unique name (default: id)
   * @return
   */
  String getUniqueNameProp() ;
  
  /**
   * Exports to byte arrayed excel document, e.g. excel
   * 
   * @param method The method to be invoked on this bean
   * @param methodArgs The method arguments of the method to be invoked on this bean
   * @param exportProperties might be null
   * @return excel document as byte array
   * @throws ReportException
   */
  
  /**
   * Returns Class of implemented {@link BODTO}
   */
    @SuppressWarnings("rawtypes")
	public Class getBODTOClass();
    
    /**
     * Returns Class object of Business Object given by T.
     * @return
     */
    public Class<? extends Object> getBoClass() ;
	

	@SuppressWarnings("rawtypes")
	public byte[] exportExcel(
			String title,
			String methodName,
			Class[] methodArgTypes,
			Object[] methodArgs
			) throws FacadeException;
    
    /**
	 * Returns all properties values within database that match given searchString.
	 *    
	 * @param c only query entities from given client
	 * @param searchProperty the property of the entity
	 * @param searchString the string to match
	 * @param maxResults the maximum number of results
	 * @return
	 * @throws FacadeException 
	 */
	public List<String> autoCompletionStringProperty(Client c, String searchProperty, String searchString, int maxResults) throws FacadeException;

	/**
     * Returns all properties values within database that match given searchString.
     *    
     * @param c only query entities from given client
     * @param searchProperty the property of the entity
     * @param searchString the string to match
     * @param maxResults the maximum number of results
     * @param params TODO
     * @return
     * @throws FacadeException 
     */
    public List<String> autoCompletionStringProperty(Client c, String searchProperty, String searchString, int maxResults, Object... params) throws FacadeException;
    	
}

/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.user;

import java.util.List;

import org.mywms.model.User;
import org.mywms.service.UserServiceException;

import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.test.TestUtilities;
import de.linogistix.los.user.crud.UserCRUDRemote;
import de.linogistix.los.user.query.UserQueryRemote;
import junit.framework.TestCase;

public class UserCRUDBeanTest extends TestCase {

	
	
	UserCRUDRemote crud;

	UserQueryRemote usersQuery;

	ClientQueryRemote clientQueryRemote;
	
	protected void setUp() throws Exception {
		super.setUp();
		crud = (UserCRUDRemote) TestUtilities.beanLocator
				.getStateless(UserCRUDRemote.class);
		usersQuery = (UserQueryRemote) TestUtilities.beanLocator
				.getStateless(UserQueryRemote.class);
		clientQueryRemote = TestUtilities.beanLocator.getStateless(ClientQueryRemote.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateTDelete() {
		User u = new User();
		u.setFirstname("Andreas");
		u.setLastname("Trautmann");
		u.setName("andreas");
		u.setPassword("1234");
		u.setClient(clientQueryRemote.getSystemClient());
		
		try{
			List<User> l = usersQuery.queryByName("andreas", new QueryDetail(0,Integer.MAX_VALUE));
			if (l.size() > 0){
				crud.delete(l.get(0));
			}
			crud.create(u);
			// change of id!!!
			u = usersQuery.queryByName("andreas", new QueryDetail(0,Integer.MAX_VALUE)).get(0);
			crud.delete(u);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testUpdate() {
		User u = new User();
		u.setFirstname("Andreas");
		u.setLastname("Trautmann");
		u.setName("andreas");
		u.setPassword("1234");
		u.setClient(clientQueryRemote.getSystemClient());
		
		User update = null;
		
		try {
			crud.create(u);
			List<User> l = usersQuery.queryByName("andreas", new QueryDetail(0,Integer.MAX_VALUE));
			update = l.get(0);
			update.setFirstname("Heike");
			
			crud.update(update);
			
			update = crud.retrieve(update.getId());
			assertEquals(update.getFirstname(),"Heike");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		} finally{
			try {
				crud.delete(update);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
			}
		}
	}

	public void testRetrieve() {
		User u = new User();
		u.setFirstname("Andreas");
		u.setLastname("Trautmann");
		u.setName("andreas");
		u.setPassword("1234");
		u.setClient(clientQueryRemote.getSystemClient());
		
		User retrieved = null;
		
		try {
			crud.create(u);
			List<User> l = usersQuery.queryByName("andreas", new QueryDetail(0,Integer.MAX_VALUE));
			u = l.get(0);
			retrieved = crud.retrieve(u.getId());
//			assertEquals(u, retrieved);	
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		} finally{
			try {
				crud.delete(retrieved);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
			}
		}
	}
		
	public void testChangePassword(){
		
		User u = new User();
		u.setFirstname("Andreas");
		u.setLastname("Trautmann");
		u.setName("andreas");
		u.setPassword("1234");
		u.setClient(clientQueryRemote.getSystemClient());
		
		User retrieved = null;
		
		try{
			retrieved = crud.create(u);
			// weak password
			try{
				retrieved.setPassword(""); // too short
				crud.update(retrieved);
				fail("too weak");
			} catch (UserServiceException ex){
				//OK
			}
			try{
				retrieved.setPassword("ANDREAS"); // no digit
				crud.update(retrieved);
				fail("too weak");
			} catch (UserServiceException ex){
				//OK
			}
			try{
				retrieved.setPassword("11111"); // no character
				crud.update(retrieved);
				fail("too weak");
			} catch (UserServiceException ex){
				//OK
			}
			try{
				retrieved.setPassword("tooooooooooooo long"); // no character
				crud.update(retrieved);
				fail("too weak");
			} catch (UserServiceException ex){
				//OK
			}
			
			try{
				retrieved.setPassword("123456AZ"); // not so weak
				crud.update(retrieved);
			} catch (UserServiceException ex){
				//
				fail(ex.getMessage());
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		} finally{
			try {
				crud.delete(retrieved);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
			}
		}
		
	}


}

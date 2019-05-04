/*
 * UserQueryBeanTest.java
 *
 * Created on 13. September 2006, 12:40
 *
 * Copyright (tClass) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.test.TestUtilities;
import de.linogistix.los.user.query.UserQueryRemote;

import java.util.List;

import org.mywms.model.User;

import junit.framework.TestCase;

public class UserQueryBeanTest extends TestCase {

  UserQueryRemote usersQuery;

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    usersQuery = (UserQueryRemote) TestUtilities.beanLocator.getStateless(UserQueryRemote.class);
  }

  public void testUserQuery() {
    User user;
    List<User> users;
    QueryDetail d = new QueryDetail(0, Integer.MAX_VALUE);
    try {
      d.setMaxResults(1000);

      users = usersQuery.queryAll(d);
      assertTrue("No users found", users.size() < 1);

      user = usersQuery.queryByName("guest", null).get(0);
      assertNotNull("No user guest found", user);

    } catch (Throwable t) {
      fail(t.getMessage());
    }
  }
}

/*
 * BOQueryByTemplateException.java
 *
 * Created on 16. Januar 2007, 23:46
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.query;

import java.text.ParseException;
import org.mywms.facade.FacadeException;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryByTemplateException extends FacadeException{
  
  /** Creates a new instance of BOQueryByTemplateException */
  public BOQueryByTemplateException(NumberFormatException ex, String propertyName) {
    super(ex.getMessage(),"ERROR.NumberFormatException",new Object[]{propertyName});
  }
  
  /** Creates a new instance of BOQueryByTemplateException */
  public BOQueryByTemplateException(IllegalArgumentException ex, String propertyName) {
    super(ex.getMessage(),"ERROR.IllegalArgument",new Object[]{propertyName});
  }
  
  /** Creates a new instance of BOQueryByTemplateException */
  public BOQueryByTemplateException(ParseException ex, String value) {
    super(ex.getMessage(),"ERROR.ParseException",new Object[]{value});
  }
  
}

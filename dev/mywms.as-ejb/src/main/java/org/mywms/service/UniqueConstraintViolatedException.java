/**
 * 
 */
package org.mywms.service;

import org.mywms.globals.ServiceExceptionKey;

/**
 * UniqueConstraintViolatedException is thrown, if a unique constraint
 * is violated. This could be for example a duplicate name, where unique
 * names are required or a duplicate id.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public class UniqueConstraintViolatedException
    extends ServiceException
{
    private static final long serialVersionUID = 1L;

    public UniqueConstraintViolatedException(ServiceExceptionKey messageKey) {
        super(messageKey);
    }
}

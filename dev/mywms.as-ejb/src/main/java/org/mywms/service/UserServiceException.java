package org.mywms.service;

import org.mywms.facade.FacadeException;
import org.mywms.res.BundleResolver;

public class UserServiceException extends FacadeException {

	private static final long serialVersionUID = 1L;

	public UserServiceException(String msg, String key, Object[] params){
		super(msg, key, params);
		setBundleResolver(BundleResolver.class);
	}
	
	@Override
	public String getBundleName() {
        return "/org/mywms/res/ServiceExceptionKeyBundle";
	}
}

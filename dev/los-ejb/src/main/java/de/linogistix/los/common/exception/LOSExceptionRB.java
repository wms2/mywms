package de.linogistix.los.common.exception;

import java.util.Arrays;

import javax.ejb.ApplicationException;

import org.mywms.facade.FacadeException;

@ApplicationException(rollback = true)
@SuppressWarnings("rawtypes")
public class LOSExceptionRB extends FacadeException {
	private static final long serialVersionUID = 1L;
	
	public LOSExceptionRB( String text  ) {
		super(text, null, new Object[]{});
		
		// Without a bundleResolver the FacadeException throws NullPointerExceptions
		// Not nice, but necessary
        setBundleResolver(this.getClass());
	}
	
	public LOSExceptionRB( String key, Class bundleResolver ) {
		super(key, key, new Object[]{});
		String bundleName = bundleResolver.getPackage().getName() + ".Bundle";
		setBundleName(bundleName);
        setBundleResolver(bundleResolver);
	}
	
	public LOSExceptionRB( String key, Class bundleResolver, String bundleName ) {
		super(key, key, new Object[]{}, bundleName);
        setBundleResolver(bundleResolver);
	}
	
	public LOSExceptionRB( String key, Object parameter, Class bundleResolver ) {
		super(key + ": " + parameter, key, new Object[]{parameter});
		String bundleName = bundleResolver.getPackage().getName() + ".Bundle";
		setBundleName(bundleName);
        setBundleResolver(bundleResolver);
	}
	
	public LOSExceptionRB( String key, Object parameter, Class bundleResolver, String bundleName ) {
		super(key + ": " + parameter, key, new Object[]{parameter}, bundleName);
        setBundleResolver(bundleResolver);
	}
	
	public LOSExceptionRB( String key, Object[] parameters, Class bundleResolver ) {
		super(key + ": " + Arrays.toString(parameters), key, parameters);
		String bundleName = bundleResolver.getPackage().getName() + ".Bundle";
		setBundleName(bundleName);
        setBundleResolver(bundleResolver);
	}
	
	public LOSExceptionRB( String key, Object[] parameters, Class bundleResolver, String bundleName ) {
		super(key + ": " + Arrays.toString(parameters), key, parameters, bundleName);
        setBundleResolver(bundleResolver);
	}
	
}

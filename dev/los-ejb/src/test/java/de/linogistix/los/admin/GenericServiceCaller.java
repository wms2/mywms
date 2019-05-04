/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.admin;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import de.linogistix.los.test.TestUtilities;

public class GenericServiceCaller {

	private final static Logger log = Logger.getLogger(GenericServiceCaller.class);
	/**
	 * @param args
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		String classname = args[0];
		String methodname  = args[1];
		try{
			
			Class[] paramClass = new Class[0];
			
			Object[] arguments = new Object[0];
			
			if (args.length > 2){
				paramClass = new Class[args.length-2];
				arguments = new Object[args.length-2];
			}
			
			for (int i=2; i < args.length; i++){
				String s;
				if (args[i].startsWith("_")){
					s = args[i].substring(2);
					char paramType = args[i].charAt(2);
					switch(paramType){
						case 'L': 
							paramClass[i-2] = Long.class;
							arguments[i-2] = Long.parseLong(s);
							break;
						case 'I': 
							paramClass[i] = Integer.class;
							arguments[i-2] = Integer.parseInt(s);
							break;
						case 'S': 
							paramClass[i] = String.class;
							arguments[i-2] = s;
							break;
						default: log.error("Unknown parameter type for " + args[i] );
					}
				} else{
					paramClass[i] = String.class;
				}
			}
			
			Class clazz = Class.forName(classname);
			Method m = clazz.getMethod(methodname, paramClass);
			
			Object service = TestUtilities.beanLocator.getStateless(clazz);
			m.invoke(service, arguments);
			
		} catch (Throwable t){
			log.error(t.getMessage(), t);
			System.exit(0);
		}
		System.exit(1);

	}

}

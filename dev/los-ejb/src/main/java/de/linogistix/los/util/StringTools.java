/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util;

/**
 * @author krane
 *
 */
public class StringTools {
	public static int compare( String s1, String s2 ) {
		if( s1==null && s2==null ) {
			return 0;
		}
		if( s1==null && s2!=null ) {
			return 1;
		}
		if( s1!=null && s2==null ) {
			return -1;
		}
		return s1.compareTo(s2);
	}
	
	public static boolean isEmpty( String s ) {
		if( s == null ) {
			return true;
		}
		if( s.length()==0 ) {
			return true;
		}
		return false;
	}
	
	public static boolean contains( String source, String... x ) {
		if( source == null ) {
			return false;
		}
		for( String s : x ) {
			if( source.contains(s) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Makes the source to a String with the given length. 
	 * Missing characters are filled.
	 *  
	 * @param source
	 * @param length
	 * @param fill
	 * @return
	 */
	public static String setStringLength( String source, int length, char fill ) {
		if( source == null ) {
			source = "";
		}
		if( source.length() == length ) {
			return source;
		}
		if( source.length() > length ) {
			return source.substring(0, length);
		}
		while( source.length() < length ) {
			source = source + fill;
		}
		return source;
	}
	
	/**
	 * Makes the source to a String with maximal the given length. 
	 * Missing characters are not filled.
	 * 
	 * @param source
	 * @param length
	 * @return
	 */
	public static String setStringLength( String source, int length ) {
		if( source == null ) {
			source = "";
		}
		if( source.length() > length ) {
			return source.substring(0, length);
		}

		return source;
	}	
}

/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

public class OSInfo {
	private static final String OS_NAME = "os.name"; //$NON-NLS-1$

	private static final String OS_ARCH = "os.arch"; //$NON-NLS-1$

	// Platform identifiers: Windows, Linux, Mac OS, ...

	/**
	 * Returns whether the underlying operating system is a Windows or Windows
	 * NT platform.
	 * 
	 * @return isWindows32Platform
	 * 
	 * @see #org.columba.core.base.OSInfo.isWindowsPlatform()
	 * @see #org.columba.core.base.OSInfo.isWinNTPlatform()
	 */
	public static boolean isWin32Platform() {
		return (isWindowsPlatform() || isWinNTPlatform());
	}
        
        public static boolean isWinPlatform() {
            if (System.getProperty(OS_NAME).toLowerCase().indexOf("windows") != -1) {
                return true;
            }
            return false;
        }

	/**
	 * Returns whether the underlying operating system is a Windows NT platform.
	 * 
	 * @return isWinNTPlatform
	 * 
	 * @see #org.columba.core.base.OSInfo.isWinNT()
	 * @see #org.columba.core.base.OSInfo.isWin2K()
	 * @see #org.columba.core.base.OSInfo.isWin2K3()
	 * @see #org.columba.core.base.OSInfo.isWinXP()
	 */
	public static boolean isWinNTPlatform() {
		return (isWinNT() || isWin2K() || isWin2K3() || isWinXP());
	}

	/**
	 * Returns whether the underlying operating system is a Windows platform.
	 * 
	 * @return isWindowsPlatform
	 * 
	 * @see #org.columba.core.base.OSInfo.isWin95()
	 * @see #org.columba.core.base.OSInfo.isWin98()
	 * @see #org.columba.core.base.OSInfo.isWinME()
	 */
	public static boolean isWindowsPlatform() {
		return (isWin95() || isWin98() || isWinME());
	}

	// Single OS identifiers: Window 95, Window 98, ...

	/**
	 * Returns whether the underlying operating system is Windows 95.
	 * 
	 * @return isWin95
	 */
	public static boolean isWin95() {
		return "Windows 95".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows 98.
	 * 
	 * @return isWin98
	 */
	public static boolean isWin98() {
		return "Windows 98".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows ME.
	 * 
	 * @return isWinME
	 */
	public static boolean isWinME() {
		return "Windows ME".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows NT.
	 * 
	 * @return isWinNT
	 */
	public static boolean isWinNT() {
		return "Windows NT".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows 2000.
	 * 
	 * @return isWin2K
	 */
	public static boolean isWin2K() {
		return "Windows 2000".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows 2003.
	 * 
	 * @return isWin2K3
	 */
	public static boolean isWin2K3() {
		return "Windows 2003".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Windows XP.
	 * 
	 * @return isWinXP
	 */
	public static boolean isWinXP() {
		return "Windows XP".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Linux.
	 * 
	 * @return isLinux
	 */
	public static boolean isLinux() {
		return "Linux".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is Solaris.
	 * 
	 * @return isSolaris
	 */
	public static boolean isSolaris() {
		return "Solaris".equalsIgnoreCase(System.getProperty(OS_NAME)); //$NON-NLS-1$
	}

	/**
	 * Returns whether the underlying operating system is some MacOS.
	 * 
	 * @return isMac
	 */
	public static boolean isMac() {
		return System.getProperty(OS_NAME).toLowerCase().indexOf("mac") != -1; //$NON-NLS-1$
	}

	public static boolean isAMD64Bit() {
		return System.getProperty(OS_ARCH).toLowerCase().indexOf("amd64") != -1; //$NON-NLS-1$
	}
}
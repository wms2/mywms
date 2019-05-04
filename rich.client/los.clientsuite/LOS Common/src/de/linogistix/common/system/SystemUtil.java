/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.common.system;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.runtime.RuntimeServicesRemote;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInfo;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author artur
 */
public class SystemUtil {


    private static SystemUtil instance = null;

    /**
     * prevent instantation
     */
    private SystemUtil() {
    }

    public synchronized static SystemUtil getInstance() {
        if (instance == null) {
            instance = new SystemUtil();
        }
        return instance;
    }

    public static void disableErrorDialog() {
         System.setProperty("netbeans.exception.report.min.level", "9999");
    }


    public static void printSystemPorperties() {
        Properties sysprops = System.getProperties();
        Enumeration propnames = sysprops.propertyNames();
        while (propnames.hasMoreElements()) {
            String propname = (String) propnames.nextElement();
            System.out.println(
                    propname + "=" + System.getProperty(propname));
        }
    }

    public static boolean isConnected() {
        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            RuntimeServicesRemote sc = (RuntimeServicesRemote) loc.getStateless(RuntimeServicesRemote.class);
            sc.ping();
            return true;
        } catch (Throwable t) {

        }
        return false;
    }

    public static String getNetbeansUserDir() {
        return System.getProperty("netbeans.user");
    }

    private static void printSystemInfo(PrintStream ps) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
        Date date = new Date();

        ps.println("-------------------------------------------------------------------------------"); // NOI18N
        ps.println("Netbeans User = "+System.getProperty("netbeans.user"));
        ps.println("Boot = "+System.getProperty("sun.boot.class.path")); // NOI18N


        // std extensions
        ps.println("JavaExtDirs = "+System.getProperty("java.ext.dirs"));
        ps.println("JavaEndorsedDirs = "+System.getProperty("java.endorsed.dirs"));


        ps.println(">Log Session: "+df.format (date)); // NOI18N
        ps.println(">System Info: "); // NOI18N

        String buildNumber = System.getProperty ("netbeans.buildnumber"); // NOI18N
//        String currentVersion = NbBundle.getMessage(Installer.class, "currentVersion", buildNumber );
//        ps.println("  Product Version         = " + currentVersion); // NOI18N
        ps.println("  Operating System        = " + System.getProperty("os.name", "unknown")
                   + " version " + System.getProperty("os.version", "unknown")
                   + " running on " +  System.getProperty("os.arch", "unknown"));
        ps.println("  Java; VM; Vendor        = " + System.getProperty("java.version", "unknown") + "; " +
                   System.getProperty("java.vm.name", "unknown") + " " + System.getProperty("java.vm.version", "") + "; " +
                   System.getProperty("java.vendor", "unknown"));
        ps.println("  Java Home               = " + System.getProperty("java.home", "unknown"));
        ps.print(  "  System Locale; Encoding = " + Locale.getDefault()); // NOI18N
        String branding = NbBundle.getBranding ();
        if (branding != null) {
            ps.print(" (" + branding + ")"); // NOI18N
        }
        ps.println("; " + System.getProperty("file.encoding", "unknown")); // NOI18N
        ps.println("  Home Directory          = " + System.getProperty("user.home", "unknown"));
        ps.println("  Current Directory       = " + System.getProperty("user.dir", "unknown"));
//        ps.print(  "  User Directory          = "); // NOI18N
        ps.print(  "  Installation            = "); // NOI18N
        String nbdirs = System.getProperty("netbeans.dirs");
        if (nbdirs != null) { // noted in #67862: should show all clusters here.
            StringTokenizer tok = new StringTokenizer(nbdirs, File.pathSeparator);
            while (tok.hasMoreTokens()) {
                ps.print(FileUtil.normalizeFile(new File(tok.nextToken())));
                ps.print("\n                            "); //NOI18N
            }
        }
        ps.println("  Application Classpath   = " + System.getProperty("java.class.path", "unknown")); // NOI18N
        ps.println("  Startup Classpath       = " + System.getProperty("netbeans.dynamic.classpath", "unknown")); // NOI18N
        ps.println("-------------------------------------------------------------------------------"); // NOI18N
    }


/*    public void putClientProperty(String key, String value) {
//        putClientProperty("TopComponentAllowDockAnywhere",Boolean.TRUE);
        putClientProperty(key, value);
    }*/

    public static JFrame getMainWindow() {
        return (JFrame)WindowManager.getDefault().getMainWindow();
    }

    /**
     *
     * @param path for the url. e.g to retrieve the layer.xml from the common modul
     * entry follow path "de/linogistix/common/layer.xml" (have from manifest.mf)
     * alternative load from disk e.g.  "file:///c:/test/layer.xml"
     * @return
     */
    public URL getResource(String path) {
        //Untestet alternate URL u = new URL("nbresloc:/org/yourorghere/addedsfs/newLayer.xml");
        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource(path);
        return url;
    }

    /**
     *
     * @param path for the url. e.g to retrieve the layer.xml from the common modul
     * entry follow path "de/linogistix/common/layer.xml" (have from manifest.mf)
     * URL url = new URL("nbresloc:de/linogistix/wm/layerExt.xml");
     * @return
     */
    public static URL getResourceNb(String path) {
        try {
            URL url = new URL("nbresloc:"+path);
            return url;
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static String getSpecVersion() {
        return getSpecVersion("de.linogistix.common");
    }
    public static String getSpecVersion(String codeBase) {
        if( codeBase == null ) {
            return null;
        }
        String specVersion = null;
        try {
            Collection modules = Lookup.getDefault().lookupAll(ModuleInfo.class);
            for( Object o : modules ) {
                ModuleInfo info = (ModuleInfo)o;
                if( codeBase.equals(info.getCodeName()) ) {
                    specVersion = info.getSpecificationVersion().toString();
                    return specVersion;
                }
            }
        }
        catch( Throwable t ) {
        }

        return specVersion;
    }
}

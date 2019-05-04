/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.action;

import de.linogistix.common.exception.NoViewerException;
import de.linogistix.common.util.ExceptionAnnotator;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 *
 * @author trautm
 */
public class OpenDocumentTask {
    
    private static final Logger log = Logger.getLogger(OpenDocumentTask.class.getName());
    private static final String osName = System.getProperty("os.name").toLowerCase();
    public static final boolean linux = osName.startsWith("linux");
    public static final boolean macosx = osName.startsWith("mac os x");
    public static final boolean win95 = osName.equals("windows 95");
    public static final boolean winAny = osName.startsWith("windows");
    
    public static void openDocument(String url) {
        try {

            //-------------------------------------------

            // posted by David G. Simmons, see http://www.nabble.com/open-pdf-viewer-from-code-to7584869.html#a7584869
            String osName = System.getProperty("os.name");

            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (osName.equalsIgnoreCase("Linux")) { //  Linux

                if (url.endsWith("pdf") || url.endsWith("PDF")) {
                    String[] pdfViewers = {
                        "evince", "kpdf", "xpdf", "Xpdf", "gv",
                        "acroread", "acrobat"
                    };
                    String viewer = null;
                    // pick the PDF Viewer
                    for (int count = 0; count < pdfViewers.length &&
                            viewer == null; count++) {
                        if (Runtime.getRuntime().exec(
                                new String[]{"which", pdfViewers[count]}).waitFor() == 0) {
                            viewer = pdfViewers[count];
                        }
                    }
                    if (viewer == null) // got no pdf viewer
                    {
                        throw new NoViewerException("pdf");
                    } else { // got a pdf viewer, so launch it

                        int ind = url.indexOf(":");
                        if (ind >= 0) {
                            url.substring(ind);
                        }
                        Runtime.getRuntime().exec(new String[]{viewer, url});
                    }
                // launch html urls in a browser
                } else if (url.startsWith("http") || url.endsWith("html") || url.endsWith("htm") || url.endsWith("HTML") ||
                        url.endsWith("HTM")) {
                    String[] browsers = { // list of html browsers
                        "firefox", "opera", "konqueror", "epiphany",
                        "mozilla", "netscape"
                    };
                    String browser = null;
                    for (int count = 0; count < browsers.length &&
                            browser == null; count++) {
                        if (Runtime.getRuntime().exec(
                                new String[]{"which", browsers[count]}).waitFor() == 0) {
                            browser = browsers[count];
                        }
                    }
                    if (browser == null) // got no browser, bummer
                    {
                        throw new NoViewerException("html");
                    } else {
                        Runtime.getRuntime().exec(new String[]{browser, url});
                    }
                }
            }
        } catch (Exception ex) { // bollux!

            ExceptionAnnotator.annotate(ex);
        }

    }
}

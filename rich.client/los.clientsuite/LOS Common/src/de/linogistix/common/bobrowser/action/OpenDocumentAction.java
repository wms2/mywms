/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.exception.NoViewerException;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Document;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class OpenDocumentAction extends NodeAction {

    private static final Logger log = Logger.getLogger(OpenDocumentAction.class.getName());
    private static final String osName = System.getProperty("os.name").toLowerCase();
    public static final boolean linux = osName.startsWith("linux");
    public static final boolean macosx = osName.startsWith("mac os x");
    public static final boolean win95 = osName.equals("windows 95");
    public static final boolean winAny = osName.startsWith("windows");

    public String getName() {
        return NbBundle.getMessage(CommonBundleResolver.class, "OpenDocumentAction");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Document.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {

        if (activatedNodes.length != 1) {
            return false;
        }

        return true;
    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        try {
            for (Node n : activatedNodes) {
                if (n == null) {
                    continue;
                }
                if (n instanceof BOMasterNode) {
                    Long id = ((BOMasterNode) n).getEntity().getId();
                    BOMasterNode m = ((BOMasterNode) n);
                    BusinessObjectQueryRemote q = m.getBo().getQueryService();
                    BasicEntity entity = q.queryById(id);
                    if (entity instanceof Document) {
                        Document d = (Document) entity;
                        byte[] document = d.getDocument();
                        //TODO store in tmp
                        File f = new File("c:\\");
                        File outf = new File(f, d.getName() + ".pdf");
                        FileOutputStream out = new FileOutputStream(outf);
                        if (document.length == 0) {
                            FacadeException ex = new FacadeException("Document is empty", "BusinessException.DocumentEmpty", null);
                            ex.setBundleResolver(CommonBundleResolver.class);
                            ExceptionAnnotator.annotate(ex);
                            return;
                        }
                        out.write(document);
                        out.close();
                        // open id viewer
                        String url = outf.getAbsolutePath();
                        openDocument(url);
                    } else {
                        ExceptionAnnotator.annotate(new RuntimeException("Can only be invoked on Document type "));
                    }

                } else {
                    ExceptionAnnotator.annotate(new RuntimeException("wrong Node typ: " + n.toString()));
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }
    }

    public void openDocument(String url) {
        try{
            
//        if (macosx) {
//            Runtime.getRuntime().exec(new String[]{"open", url});
//            System.out.println("open " + url);
//        } else if (linux) {
//            Runtime.getRuntime().exec(new String[]{"./xdg-open", url});
//        } else if (win95) {
//            Runtime.getRuntime().exec(new String[]{"command.com", "/C", "start",
//                        url
//                    });
//        } else if (winAny) {
//            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/C", "start",
//                        url
//                    });
//        } else {
//            Runtime.getRuntime().exec(new String[]{"open", url});
//        }

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


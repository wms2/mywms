/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.los.common.businessservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.ejb.Stateless;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.DocumentTypes;

import de.linogistix.los.report.ReportException;
import de.linogistix.los.report.ReportExceptionKey;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPrintServiceBean implements LOSPrintService {
	private static final Logger log = Logger.getLogger(LOSPrintServiceBean.class);

	public void print(String printer, byte[] bytes, String type) throws FacadeException {
		String logStr = "print ";
		try {

			PrintService printService;
			DocPrintJob job;
			DocFlavor fl;
			Doc doc;

			DocAttributeSet das = new HashDocAttributeSet();
			Object printObject;
			if (type == null || type.length() == 0){
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else if (type.equals(DocumentTypes.APPLICATION_PDF.toString())) {
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else if (type.equals(DocumentTypes.TEXT_XML.toString())) {
				fl = DocFlavor.BYTE_ARRAY.AUTOSENSE;
				printObject = bytes;
			} else {
				log.warn("Unknown type: " + type);
				fl = DocFlavor.INPUT_STREAM.AUTOSENSE;
				printObject = new ByteArrayInputStream(bytes);
			}
			
			if (printer == null || printer.length() == 0) {
				log.info("Won't print. Printer not defined");
				return;
			}
			if (printer != null && printer.equalsIgnoreCase(NO_PRINTER)){
				log.info("Won't print. Printer: " + printer);
				return;
			}
			
			
			if( printer.startsWith("cmd:") ) {
				String cmd = printer.substring(4);
				
				File tmpFile = File.createTempFile("mywms", ".prn");
				FileOutputStream fileOut = new FileOutputStream(tmpFile);
				fileOut.write(bytes);
				fileOut.flush();
				fileOut.close();
				
				cmd = cmd.replace( ":file:", tmpFile.getAbsolutePath());
				log.info(logStr + cmd);
				
				Process p = null;
				try {
					p = Runtime.getRuntime().exec(cmd);
				} catch (Throwable ex) {
					log.error(logStr + ex.getClass().getSimpleName()+", "+ex.getMessage());
				}
				
				new LOSPrintServiceCleanupThread(tmpFile,  p).start();
			}
			else {
				if( printer.startsWith("prn:") ) {
					printer = printer.substring(4);
				}
				
				if (printer.equalsIgnoreCase(DEFAULT_PRINTER)) {
					printService = PrintServiceLookup.lookupDefaultPrintService();
				} else {
					printService = getNamedPrintService(fl,printer);
				}
				if (printService == null) {
					log.error(logStr+"printer not found: " + printer);
					throw new ReportException(ReportExceptionKey.PRINT_FAILED, printer);
				}
				
				doc = new SimpleDoc(printObject, fl, das);
				job = printService.createPrintJob();	
				PrintJobWatcher watcher = null;
				
				if (fl instanceof DocFlavor.INPUT_STREAM) {
					watcher = new PrintJobWatcher(job);
				}
				
				job.print(doc, null);
	
				if (watcher != null){
					watcher.waitForDone();
					((InputStream)printObject).close();
				}
			}			

		} catch (FacadeException ex) {
			throw ex;
		} catch (Throwable ex) {
			log.error(logStr+"Exception occured on printing: "+ex.getMessage(), ex);
			throw new ReportException(ReportExceptionKey.PRINT_FAILED, printer);
		}

	}

	
	private PrintService getNamedPrintService(DocFlavor flav, String prnName) throws FacadeException {
		PrintService[] prnSvcs;
		PrintService prnSvc = null;
		
		// get all print services for this machine
		prnSvcs = PrintServiceLookup.lookupPrintServices(flav, null);

		if (prnSvcs.length > 0) {
			int ii = 0;
			while (ii < prnSvcs.length) {
				if (prnSvcs[ii].getName().equalsIgnoreCase(prnName)) {
					prnSvc = prnSvcs[ii];
					log.debug("Named Printer selected: "
							+ prnSvcs[ii].getName() + "*");
					break;
				}
				ii++;
			}
		}

		if (prnSvc == null) {
			log.info("Printer not found. name=" + prnName);
			int ii = 0;
			while (ii < prnSvcs.length) {
				log.info("Named Printer found: " + prnSvcs[ii].getName());
				ii++;
			}
			throw new ReportException(ReportExceptionKey.PRINTER_UNDEFINED, prnName);
		}

		return prnSvc;
	}
	
	private static class PrintJobWatcher {
        // true if it is safe to close the print job's input stream
        boolean done = false;
        
        PrintJobWatcher(DocPrintJob job) {
            // Add a listener to the print job
            job.addPrintJobListener(new PrintJobAdapter() {
                public void printJobCanceled(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobCompleted(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobFailed(PrintJobEvent pje) {
                    allDone();
                }
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    allDone();
                }
                void allDone() {
                    synchronized (PrintJobWatcher.this) {
                        done = true;
                        PrintJobWatcher.this.notify();
                    }
                }
            });
        }
        public synchronized void waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
            }
        }
    }
	
	class LOSPrintServiceCleanupThread extends Thread {
		private File file;
		private Process process;
		
		public LOSPrintServiceCleanupThread( File file, Process process ) {
			this.file = file;
			this.process = process;
			
		}
		
		public void run() {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
			try {
				if( this.process != null ) {
					this.process.destroy();
				}
			} catch (Throwable t) {
				log.warn(this.getClass().getSimpleName()+"Cannot destoy. "+t.getClass().getSimpleName()+", "+t.getMessage());
			}
			try {
				if( this.file != null )  {
					this.file.delete();
				}
			} catch (Throwable t) {
				log.warn(this.getClass().getSimpleName()+"Cannot delete tmp file="+file.getAbsolutePath()+". "+t.getClass().getSimpleName()+", "+t.getMessage());
			}
		}
	}
}

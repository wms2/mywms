/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.report;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.DocumentTypes;

import de.linogistix.los.common.businessservice.LOSJasperReportGenerator;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.StorageLocationLabel;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.report.ReportException;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;

/**
 *
 * @author trautm
 */
@Stateless
public class StorageLocationLabelReportBean implements StorageLocationLabelReport {

    private static final Logger log = Logger.getLogger(StorageLocationLabelReportBean.class);
    @EJB
    private LOSJasperReportGenerator reportGenerator;
    @PersistenceContext(unitName = "myWMS")
    private EntityManager manager;
	@Inject
	private StorageLocationEntityService locationService;

   
    /**
     * 
     * @param rowNumber prints arrow down for all row <code>numbers &lt rowNumber</code>
     * @return
     * @throws de.linogistix.los.location.exception.LOSLocationException
     * @throws de.linogistix.los.query.BusinessObjectQueryException
     * @throws de.linogistix.los.report.ReportException
     */
    @SuppressWarnings("unchecked")
	public StorageLocationLabel generateStorageLocationLabels(int offset) throws LOSLocationException, BusinessObjectQueryException, ReportException {
        try {

            StorageLocationLabel doc = new StorageLocationLabel();
            doc.setName("StorageLocationLabels");
            doc.setType(DocumentTypes.APPLICATION_PDF.toString());

            StringBuffer b = new StringBuffer();
            b.append("SELECT NEW ");
            b.append(StorageLocationLabelTO.class.getName());
            b.append("(");
            b.append("sl.name,");
            b.append("sl.yPos,");
            b.append(")");

            b.append(" FROM ");
            b.append(StorageLocation.class.getName());
            b.append(" sl ");
            
            Query q = manager.createQuery(new String(b));
            List<StorageLocationLabelTO> labels = q.getResultList();
            
            for (StorageLocationLabelTO to : labels){
                if (to.getOffset() -  offset < 0) {
                    to.setOffset(to.getOffset() -  offset);
                } else{
                    // avoid 0
                    to.setOffset(to.getOffset() -  offset + 1);
                }
            }
            
            return this.generateStorageLocationLabels(labels);
        } catch (Throwable ex) {
            log.error(ex, ex);
            throw new ReportException();
        }
    }
    
    public StorageLocationLabel generateRackLabels(List<String> list) throws LOSLocationException, BusinessObjectQueryException, ReportException {
        List<StorageLocationLabelTO> labels = new ArrayList<StorageLocationLabelTO>();
        for (String rack : list){
           for (StorageLocation loc : locationService.readList(null, null, rack, null, null) ){
            StorageLocationLabelTO to = new StorageLocationLabelTO(loc.getName(), 0);
            labels.add(to);
           }
        }
        return generateStorageLocationLabels(labels);
    }

    public StorageLocationLabel generateStorageLocationLabels(List<StorageLocationLabelTO> labels) throws LOSLocationException, BusinessObjectQueryException, ReportException {
        try {

            StorageLocationLabel doc = new StorageLocationLabel();
            doc.setName("StorageLocationLabels");
            doc.setType(DocumentTypes.APPLICATION_PDF.toString());

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("REPORT_LOCALE", Locale.GERMANY);
            
            byte[] bytes = reportGenerator.createPdf(doc.getClient(), doc.getName(), BundleResolver.class, labels, parameters);           
            doc.setDocument(bytes);
            
            return doc;
        } catch (Throwable ex) {
            log.error(ex, ex);
            throw new ReportException();
        }

    }
    
    public static Image getDownArrow(){
        try {

            InputStream is = BundleResolver.class.getResourceAsStream("/de/linogistix/los/location/res/downarrow.png");
            BufferedImage i = ImageIO.read(is);
            
            return i;
        } catch (IOException ex) {
            throw new MissingResourceException("de.linogistix.los.location.res.downarrow.png", "de.linogistix.los.location.res.downarrow.png", "de.linogistix.los.location.res.downarrow.png");
        }
    }
    
    public static Image getUpArrow(){
        try {

            InputStream is = BundleResolver.class.getResourceAsStream("/de/linogistix/los/location/res/uparrow.png");
            BufferedImage i = ImageIO.read(is);
            return i;
        } catch (IOException ex) {
            throw new MissingResourceException("de.linogistix.los.location.res.uparrow.png", "de.linogistix.los.location.res.uparrow.png", "de.linogistix.los.location.res.downarrow.uparrow.png");
        } 
    }

    
}

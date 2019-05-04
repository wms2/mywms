/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.action;

import de.linogistix.reports.gui.component.BOExportWizard;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.query.BOQueryModel;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.action.OpenDocumentTask;
import de.linogistix.common.bobrowser.bo.JasperExporterNode;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.report.ReportException;
import de.linogistix.los.util.TypeResolver;
import de.linogistix.reports.res.ReportsBundleResolver;
import java.awt.Dialog;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BOExportExcelAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOExportExcelAction.class.getName());
    
    private String[] roles = new String[]{
        Role.ADMIN.toString(),
        Role.OPERATOR.toString(),
        Role.FOREMAN.toString(),
        Role.INVENTORY.toString(),
        Role.CLEARING.toString()
    };

    public BOExportExcelAction() {
        
        putValue(SMALL_ICON, new ImageIcon(ImageUtilities.loadImage("de/linogistix/reports/res/icon/Excel.gif", true)));
        
    }

    
    
    public String getName() {
        return NbBundle.getMessage(ReportsBundleResolver.class, "BOExportExcelAction");
    }

    protected String iconResource() {
        return "de/linogistix/reports/res/icon/Excel.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {

        boolean ret = false;

        if (activatedNodes.length > 0) {

            Node n = activatedNodes[0];
            Node p;
            while ((p = n.getParentNode()) != null) {
                if (n instanceof BOQueryNode || p instanceof BOQueryNode) {
                    ret = true;
                    break;
                } else if (n instanceof BOMasterNode || p instanceof BOMasterNode) {
                    ret = true;
                    break;
                }
                n = p;

            }
            if (!ret){
                for (Node node : activatedNodes){
                    if (node instanceof JasperExporterNode){
                        // better iterate over all or not????
                        ret = true;
                        break;
                    }
                }
            }

        }

        ret = ret && checkRoles();

        return ret;
    }

    /**
     * Checks whether the logged in user is allowed to see this node.
     */
    public boolean checkRoles() {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(getRoles());
    }

    public String[] getRoles() {
        return roles;
    }

    protected void performAction(Node[] activatedNodes) {

        System.setProperty("net.sf.jasperreports.properties", "de/linogistix/ireport/jasperreports.properties");

        BasicEntity e;
        BOQueryNode qn = null;

        if (activatedNodes == null) {
            return;
        }

        try {

            Node n = activatedNodes[0];
            Node p;
            while ((p = n.getParentNode()) != null) {
                if (n instanceof BOQueryNode) {
                    qn = (BOQueryNode) n;
                    break;
                } else if (p instanceof BOQueryNode) {
                    qn = (BOQueryNode) p;
                    break;
                }

                n = p;
            }

            BOExportWizard w = new BOExportWizard(null, ".xls", "*.xls", "export.xls");

            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            byte[] bytes;
            String name = "";

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();

                if (w.getSelectionMode() == BOExportWizard.SelectionMode.ALL) {
                
                    if (qn == null) {
                        throw new RuntimeException("No BOQueryNode found");
                    }
                    
                    BOQueryModel model = qn.getModel();
                    BOQueryComponentProvider provider = model.getProvider();
                    Method m = provider.getMethod();
                    QueryDetail det = model.getQueryDetail();
                    det.setMaxResults(5000);
                    if (provider.getQueryMethodParameters(model.getQueryDetail(), model.getSearchString()) == null || provider.getQueryMethodParameters(model.getQueryDetail(), model.getSearchString()).length == 0) {
                        bytes = provider.getQueryRemote().exportExcel(name, m.getName(), new Class[0], null);
                    } else {
                        Object[] params = provider.getQueryMethodParameters(det, model.getSearchString());
                        Class[] paramTypes = provider.getQueryMethodParameterTypes();
                        bytes = provider.getQueryRemote().exportExcel(name, m.getName(), paramTypes, params);
                    }

                } else {
//                    GenericExcelExporter exp = new GenericExcelExporter();
                    List l = new ArrayList();
                    JasperExporterNode exportNode = null;
                    Object exportBean = null;
                    for (Node act : activatedNodes) {
                        exportNode = (JasperExporterNode) act;
                        exportBean = exportNode.getExportBeanObject();
                        l.add(exportBean);
                    }
                    if (l.size() > 0 && exportNode != null) {
                        List<PropertyDescriptor> descr = new ArrayList<PropertyDescriptor>();
                        List<Property> setProps = new ArrayList<Property>();
                        for (Node.PropertySet set : exportNode.getExportPropertySets()){
                            setProps.addAll(Arrays.asList(set.getProperties()));
                        }

                        PropertyDescriptor pd;

                        try{
                            pd = new PropertyDescriptor("name",
                                            exportBean.getClass(), "getName", null);
                            pd.setDisplayName("name");
                            descr.add(pd);
                        } catch (IntrospectionException ex){
                            log.warning(ex.getMessage());
                        }
                        
                        for (Property prop : setProps){
                            try{
                                
                                if (TypeResolver.isBooleanType(prop.getValueType())){
                                   //ok
                                } else if (TypeResolver.isPrimitiveType(prop.getValueType())){
                                    //ok
                                } else if (TypeResolver.isDateType(prop.getValueType())){
                                    //ok
                                } else if (TypeResolver.isEnumType(prop.getValueType())){
                                    log.warning("Skip type " + prop.getValueType());
                                    continue;
                                } else{
                                    log.warning("Skip type " + prop.getValueType());
                                    continue;
                                }
                                
                                pd = new PropertyDescriptor(prop.getName(),
                                        exportBean.getClass());
                                pd.setDisplayName(prop.getDisplayName());
                                descr.add(pd);
                            } catch (Throwable t){
                                log.log(Level.INFO,t.getMessage(), t);
                                continue;
                            }
                        }
                        bytes = export(name, l, descr);
                        if (bytes == null) {
                            throw new ReportException();
                        }
                    } else {
                        throw new RuntimeException("No selection");
                    }
                }

                OutputStream out = new FileOutputStream(w.getFileName());
                out.write(bytes);
                out.close();

                if (w.isOpen()) {
                    OpenDocumentTask.openDocument(w.getFileName());
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

    //--------------------------------------------------------------------------
    public byte[] export(String title, List<BODTO> exportList,
            List<PropertyDescriptor> props) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        if (exportList == null || exportList.size() == 0) {
            return new byte[0];
        }

        try {
            Object bean = exportList.get(0);
            BeanInfo infoTo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] d = infoTo.getPropertyDescriptors();

            if (props == null || props.isEmpty()) {

                props = new ArrayList<PropertyDescriptor>();

                for (int i = 0; i < d.length; i++) {
                    try {
                        
                        Class pType = d[i].getPropertyType();
                        
                        if (pType.isAssignableFrom(Class.class)) {
                            continue;
                        }

                        if (TypeResolver.isBooleanType(pType)){
                           //ok
                        } else if (TypeResolver.isPrimitiveType(pType)){
                            //ok
                        } else if (TypeResolver.isDateType(pType)){
                            //ok
                        } else if (TypeResolver.isEnumType(pType)){
                            log.warning("Skip type " + pType);
                            continue;
                        } else{
                            log.warning("Skip type " + pType);
                            continue;
                        }
                        
                        if (d[i].getName().equals("className") || d[i].getName().equals("id") || d[i].getName().equals("version")) {
                            continue;
                        }

                        props.add(d[i]);
                    } catch (Exception ex) {
                        log.severe(ex.getMessage());
                        continue;
                    }

                }
            }

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ReportTitle", title);
            parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
            
            JasperDesign design = getJasperDesign(props);

            JasperReport jasperReport = JasperCompileManager.compileReport(design);

            JasperPrint jasperPrint = null;

            jasperPrint = JasperFillManager.fillReport(jasperReport,
                    parameters, new JRBeanCollectionDataSource(exportList));

            JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setParameter(JRExporterParameter.JASPER_PRINT,
                    jasperPrint);
            xlsExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
                    Boolean.FALSE);
            xlsExporter.setParameter(
                    JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                    Boolean.TRUE);
            xlsExporter.setParameter(
                    JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
                    Boolean.FALSE);
            xlsExporter.setParameter(
                    JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
                    Boolean.TRUE);
           
            // writeHeader(response, exportName);

            xlsExporter.exportReport();

            byte[] ret = out.toByteArray();

            out.close();

            return ret;

        } catch (Throwable t) {
            log.log(Level.INFO, t.getMessage(),t);
            return null;
        }

    }

    private static JasperDesign getJasperDesign(List<PropertyDescriptor> props)
            throws JRException {
        // JasperDesign
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("NoXmlDesignReport");
        jasperDesign.setPageWidth(595);
        jasperDesign.setPageHeight(842);
        jasperDesign.setColumnWidth(515);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setColumnCount(1);
        jasperDesign.setLeftMargin(40);
        jasperDesign.setRightMargin(40);
        jasperDesign.setTopMargin(50);
        jasperDesign.setBottomMargin(50);

        // Fonts
        String fontName = "DejaVu Sans";
        String[] fonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if( fonts.length>0 ) {
            fontName = fonts[0];
        }
        for( String s : fonts ) {
            String sl = s.toLowerCase();
            if( sl.equals("dejavu sans") || sl.equals("arial") || sl.equals("sans") || sl.equals("tahoma") || sl.equals("sansserif")) {
                fontName = s;
                break;
            }
        }

        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName(fontName);
        normalStyle.setFontSize(12);
        normalStyle.setPdfFontName("Helvetica");
        normalStyle.setPdfEncoding("Cp1252");
        normalStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(normalStyle);

        JRDesignStyle boldStyle = new JRDesignStyle();
        boldStyle.setName("Sans_Bold");
        boldStyle.setFontName(fontName);
        boldStyle.setFontSize(12);
        boldStyle.setBold(true);
        boldStyle.setPdfFontName("Helvetica-Bold");
        boldStyle.setPdfEncoding("Cp1252");
        boldStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(boldStyle);

        JRDesignStyle italicStyle = new JRDesignStyle();
        italicStyle.setName("Sans_Italic");
        italicStyle.setFontName(fontName);
        italicStyle.setFontSize(12);
        italicStyle.setItalic(true);
        italicStyle.setPdfFontName("Helvetica-Oblique");
        italicStyle.setPdfEncoding("Cp1252");
        italicStyle.setPdfEmbedded(false);
        jasperDesign.addStyle(italicStyle);

        // Parameters
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setName("ReportTitle");
        parameter.setValueClass(java.lang.String.class);
        jasperDesign.addParameter(parameter);

        // Fields
        int i = 1;
        for (PropertyDescriptor p : props) {
            JRDesignField field = new JRDesignField();
            field.setName(p.getName());
            field.setValueClass(resolveType(p));
            jasperDesign.addField(field);
            i++;
        }

        // Title
        JRDesignBand band = new JRDesignBand();
//        band.setHeight(50);
//
        JRDesignTextField textField = new JRDesignTextField();
//        textField.setBlankWhenNull(true);
//        textField.setX(0);
//        textField.setY(20);
//        textField.setWidth(510);
//        textField.setHeight(30);
//        textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
//        textField.setStyle(normalStyle);
//        textField.setFontSize(22);
        JRDesignExpression expression = new JRDesignExpression();
//        expression.setValueClass(java.lang.String.class);
//        expression.setText("$P{ReportTitle}");
//        textField.setExpression(expression);
//        band.addElement(textField);
//        jasperDesign.setTitle(band);

        // Page header
        band = new JRDesignBand();
        band.setHeight(20);
        int x = 0;
        for (PropertyDescriptor p : props) {
            JRDesignStaticText staticText = new JRDesignStaticText();
            staticText.setX(x);
            staticText.setY(0);
            staticText.setWidth(150);
            staticText.setHeight(20);
            staticText.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
            staticText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
            staticText.setStyle(boldStyle);
            staticText.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
      
            staticText.setText(p.getDisplayName());
            band.addElement(staticText);
            x += 150;
        }

        jasperDesign.setPageHeader(band);

        // Column header
        band = new JRDesignBand();
        jasperDesign.setColumnHeader(band);

        band = new JRDesignBand();
        band.setHeight(25);
        x = 0;
        for (PropertyDescriptor p : props) {
            
            Class type = resolveType(p);
            
            textField = new JRDesignTextField();
            textField.setX(x);
            textField.setY(0);
            textField.setWidth(150);
            textField.setHeight(20);
            textField.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
            textField.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
            textField.setStyle(normalStyle);
            textField.setStretchWithOverflow(true);
            textField.setStretchType(StretchTypeEnum.RELATIVE_TO_TALLEST_OBJECT);
            
            expression = new JRDesignExpression();
            expression.setValueClass(type);
            expression.setText("$F{" + p.getName() + "}");
            
            textField.setExpression(expression);
            textField.setStretchWithOverflow(true);
            band.addElement(textField);
            x += 150;
        }

        JRDesignSection detailSection = (JRDesignSection)jasperDesign.getDetailSection();
        detailSection.addBand(band);

        // Column footer
        band = new JRDesignBand();
        jasperDesign.setColumnFooter(band);

        // Page footer
        band = new JRDesignBand();
        jasperDesign.setPageFooter(band);

        // Summary
        band = new JRDesignBand();
        jasperDesign.setSummary(band);

        return jasperDesign;
    }

    private static Class resolveType(PropertyDescriptor d) {
        if (TypeResolver.isIntegerType(d.getPropertyType())) {
            return Integer.class;
        } else if (TypeResolver.isLongType(d.getPropertyType())) {
            return Long.class;
        } else if (TypeResolver.isBooleanType(d.getPropertyType())) {
            return Boolean.class;
        } else if (TypeResolver.isEnumType(d.getPropertyType())) {
            return String.class;
        } else {
            return d.getPropertyType();
        }
    }
}


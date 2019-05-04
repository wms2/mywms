/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.gui.component.gui_builder.AbstractBOChooser;
import de.linogistix.common.gui.component.other.TopComponentExt;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.mywms.facade.FacadeException;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Live help component.
 *
 * The user types some text in a JComboBox like control and gets a list of
 * matching entities in the database.
 * <p>
 * All queries are remote calls to instances of {@link BusinessObjectQueryRemote}
 * <p>
 * Different modes are available:
 * <ul>
 * <li><code>AUTO_COMPLETE</code>: Most common. Calls {@link BusinessObjectQueryRemote#autoCompletion(String)}
   <li><code>BY_IDENTITY</code>:Calls {@link BusinessObjectQueryRemote#queryByIdentity(String)}
   <li><code>INVOKE_QUERYMETHOD</code>:Most flexible. Calls an arbitrary method.
 * </ul>
 *
 * <p>
 * <strong>Example</strong> for default usage</code>:
 * <p>
 * <code>
 *      this.clientCombo = new BOAutoFilteringComboBox<Client>(Client.class);
 *      this.clientCombo.initAutoFiltering();
        this.clientCombo.setEditorLabelTitle(NbBundle.getMessage(BundleResolver.class, "Lot"));
 *      add(this.clientCombo); // add to a JComponent
 * </code>
 *
 *  <strong>Note when using Matisse</strong></code>:
 * Make sure to set property <code>boClass</code>. Otherwise the component will no be initialized correctly!
 *
 * <p>
 * <strong>Example</strong> for mode <code>INVOKE_QUERYMETHOD</code>:
 * <p>
 * <code>
 *
 *  this.lotCombo = new BOAutoFilteringComboBox(Lot.class);
        this.lotCombo.setMode(Mode.INVOKE_QUERYMETHOD);
        try {
            m = this.lotCombo.getQueryRemote().getClass().getDeclaredMethod(
                    "autoCompletionByClientAndItemData",
                    new Class[]{String.class, BODTO.class, BODTO.class});
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
            return;
        }
        this.lotCombo.setQueryMethod(m);
        this.lotCombo.setReplaceArgByTypedText(0);
        this.lotCombo.setQueryMethodArgs(new Object[]{"",null, null});
        this.lotCombo.setEditorLabelTitle(NbBundle.getMessage(BundleResolver.class, "Lot"));
        this.lotCombo.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               lotChanged(evt);
            }
        });
 *
 * </code>
 * @author trautm
 */
public class BOAutoFilteringComboBox<T extends BasicEntity> extends javax.swing.JPanel implements AutoFilteringComboBoxListener {
    private final static Logger log = Logger.getLogger(BOAutoFilteringComboBox.class.getName());

    public final static String ITEM_CHANGED = "ItemChanged";
    private boolean suppressWarnings = false;

    BODTO startBO;

    public static enum Mode {
        AUTO_COMPLETE,
        INVOKE_QUERYMETHOD,
        MODEL
    }

    private Class boClass;
    private BusinessObjectQueryRemote queryRemote;
    private Mode mode = Mode.AUTO_COMPLETE;
    private List<PropertyChangeListener> listeners = new ArrayList();
    private Method queryMethod;
    private Object[] queryMethodArgs;
    private int replaceArgByTypedText;
    private BOAutoFilteringComboBoxModel<T> myModel;
    private BODTO<T> selectedBO = null;
    private T selectedEntity = null;
    private boolean mandatory = false;

    /** Creates new form AbstractBOEditor */
    public BOAutoFilteringComboBox() {
        initComponents();
        openChooserButton.setFocusable(false);
        openChooserButton.setText(org.openide.util.NbBundle.getMessage(CommonBundleResolver.class, "AbstractBOAutoFilteringComboBox.openChooserButton.text")); // NOI18N
        getAutoFilteringComboBox().addSearchListener(this);
    }

    /** Creates new form AbstractBOEditor */
    public BOAutoFilteringComboBox(Class boClass) {
        this();
        setBoClass(boClass);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        editorLabel = new de.linogistix.common.gui.component.controls.LosLabel();
        autoFilteringComboBox = new AutoFilteringComboBox();
        openChooserButton = new javax.swing.JButton();

        setFocusable(false);
        setLayout(new java.awt.GridBagLayout());

        editorLabel.setText("-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(editorLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(autoFilteringComboBox, gridBagConstraints);

        openChooserButton.setText("hhhh"); // NOI18N
        openChooserButton.setFocusable(false);
        openChooserButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        openChooserButton.setMaximumSize(new java.awt.Dimension(24, 22));
        openChooserButton.setMinimumSize(new java.awt.Dimension(24, 22));
        openChooserButton.setPreferredSize(new java.awt.Dimension(24, 22));
        openChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openChooserButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 0);
        add(openChooserButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void openChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openChooserButtonActionPerformed
    openChooserButtonActionPerformedListener(evt);
}//GEN-LAST:event_openChooserButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JComboBox autoFilteringComboBox;
    private javax.swing.JLabel editorLabel;
    private javax.swing.JButton openChooserButton;
    // End of variables declaration//GEN-END:variables

    public AutoFilteringComboBox getAutoFilteringComboBox(){
        return (AutoFilteringComboBox) autoFilteringComboBox;
    }
    
    public LosLabel getEditorLabel(){
        return (LosLabel) editorLabel;
    }

    public javax.swing.JButton getOpenChooserButton() {
        return openChooserButton;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        autoFilteringComboBox.setEnabled(enabled);
        openChooserButton.setEnabled(enabled);
    }
    
    public void setEditable(boolean editable){
        autoFilteringComboBox.setEnabled(editable);
        openChooserButton.setEnabled(editable);
    }

    public int getColumns() {
        return getAutoFilteringComboBox().getColumns();
    }
    public void setColumns(int columns) {
        getAutoFilteringComboBox().setColumns(columns);
    }
    public boolean isEditable(){
        return autoFilteringComboBox.isEditable();
    }
 
    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        getEditorLabel().setShowMandatoryFlag(mandatory);
        this.mandatory=mandatory;
    }
  
    public boolean checkSanity(){
        if( getAutoFilteringComboBox().isEmpty() ) {
            if (isMandatory()){
                showEmptyErrorMessage();
                return false;
            }
        }
        else if( getSelectedItem() == null ){
            showDefaultErrorMessage();
            return false;
        }

        return true;
    }

    public void showDefaultErrorMessage(){
        if(!suppressWarnings){
            getEditorLabel().setText(NbBundle.getMessage(CommonBundleResolver.class, "Entry does not exist"), IconType.ERROR);
        }
    }

    public void showEmptyErrorMessage(){
        if(!suppressWarnings){
            getEditorLabel().setText(NbBundle.getMessage(CommonBundleResolver.class, "Empty field"), IconType.ERROR);
        }
    }

    public boolean isSuppressWarnings() {
        return suppressWarnings;
    }

    public void setSuppressWarnings(boolean suppressWarnings) {
        this.suppressWarnings = suppressWarnings;
    }

        /**
     *
     * @param label
     * @param errorKey The key for the Bundle.properties
     */
    public void setMatchError() {
        String errorKey = "Entry does not exist";

        // clear labels error message
        getEditorLabel().setText();

       // set error message or leave label without
        if( getAutoFilteringComboBox().isEmpty() ) {
            getEditorLabel().setText();
        }
        else if( !isValid() && !suppressWarnings ) {
            getEditorLabel().setHiddenText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            if( getSelectedAsEntity() == null ) {
                getEditorLabel().setText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            }
        }
    }

    @Override
    public void requestFocus() {
        autoFilteringComboBox.requestFocus();
    }

    @Override
    public boolean requestFocusInWindow() {
        return autoFilteringComboBox.requestFocusInWindow();
    }

    @Override
    public void transferFocus() {
        autoFilteringComboBox.transferFocus();
    }

    @Override
    public Font getFont(){
        // I don't know what swing is doing. But there is access to the component before constuctor is done
        if( autoFilteringComboBox == null ) {
            return super.getFont();
        }
        return autoFilteringComboBox.getFont();
    }

    @Override
    public void setFont(Font font){
        // I don't know what swing is doing. But there is access to the component before constuctor is done
        if( autoFilteringComboBox == null ) {
            super.setFont(font);
            return;
        }
        autoFilteringComboBox.setFont(font);
    }

//    public void setPreferredComboboxSize(Dimension preferredSize) {
//        autoFilteringComboBox.setPreferredSize(preferredSize);
//        Dimension d = openChooserButton.getPreferredSize();
//        d.height = preferredSize.height;
//        openChooserButton.setPreferredSize(d);
//    }
//
//    public Dimension getPreferredComboboxSize() {
//        return autoFilteringComboBox.getPreferredSize();
//    }

     /**
      * Make sure to call this method AFTER {@link  BOLookup} is initialized. A
      * good place is {@link TopComponentExt#componentOpened()}. Also, mkae sure to call
      * {@link #setBoClass(java.lang.Class) } before!
      */
    public void initAutofiltering() {

        if (this.getBoClass() == null){
            throw new NullPointerException("boClass must not be null! Set before adding to a panel.");
        }

        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        BO bo = (BO) l.lookup(boClass);
        if( bo == null ) {
            log.severe("Cannot lookup BOClass: " + boClass.getCanonicalName());
            return;
        }

        this.queryRemote = bo.getQueryService();
        clear();


        if (getEditorLabel() == null){
            setEditorLabelTitle(bo.getSingularDisplayName());
        }

        setEditorLabelText();
    }

    public void setEditorLabelTitle(String text) {
        getEditorLabel().setTitleText(text);
    }

    public void setEditorLabelText() {
        getEditorLabel().setText();
    }


    public void requestProposalData( String searchString ) {
        List items = null;

        if (searchString == null || searchString.length() == 0){
            getAutoFilteringComboBox().setProposalList(null);
            return;
        }

        searchString = searchString.trim();
        
        switch (mode) {
            case AUTO_COMPLETE:
                items = queryRemote.autoCompletion(searchString, false);
                break;
            case INVOKE_QUERYMETHOD:
                int i = 1;
                for(Object arg : queryMethodArgs){
                    String argStr = "NULL";
                    if(arg != null)
                        argStr = arg.toString();

                    System.out.println("----- Query Method Arg "+i+" : "+argStr);
                    i++;
                }

                try {
                    if (replaceArgByTypedText < queryMethodArgs.length ){
                        queryMethodArgs[replaceArgByTypedText] = searchString;
                        items = (List<BODTO>) queryMethod.invoke(queryRemote,queryMethodArgs );
                    } else{
                        FacadeException ex = new FacadeException("Not initialized", "BusinessException.InvalidUserArgs", null);
                        ex.setBundleResolver(CommonBundleResolver.class);
                        throw ex;
                    }
                } catch (Throwable ex) {
                    ExceptionAnnotator.annotate(ex);
                }
                break;

            case MODEL:
                items = myModel.getResults(searchString, new QueryDetail(0, 30));
        }


        List<String> itemTextList = new ArrayList();
        List<Object> itemList = new ArrayList();
        if (items != null) {
            for (Object o : items) {
                BODTO item = (BODTO)o;
                itemTextList.add(item.getName());
                itemList.add(item);
            }
        }

        getAutoFilteringComboBox().setProposalList(itemList);
    }

    public void openChooserButtonActionPerformedListener(ActionEvent evt) {
        if( getComboBoxModel() != null && getComboBoxModel().isSingleResult() ) {
            return;
        }

        BOChooser dialog;

        if(mode == Mode.MODEL && myModel != null){
            dialog = new BOChooser(getBoClass(),myModel);
        }
        else{
            dialog = new BOChooser(getBoClass());
        }

        dialog.setSelection(getAutoFilteringComboBox().getText());
        dialog.showDialog();

        if (dialog.dialogDescriptor.getValue() instanceof AbstractBOChooser.CustomButton) {
            BOChooser.CustomButton button = (BOChooser.CustomButton) dialog.dialogDescriptor.getValue();
            if( !button.getActionCommand().equals(BOChooser.OK_BUTTON)) {
                return;
            }
            BODTO dto = dialog.getValue();
            addItem(dto);
            requestFocus();
            if( isItemChanged() ) {
                fireItemChangeEvent();
            }
        }
    }


    public boolean selectionChanged() {
        fireItemChangeEvent();
        return (selectedBO!=null);
    }

    @Deprecated
    public void setSelectedItem(BODTO<T> selected){
        addItem(selected);
    }

    public void addItem(BODTO<T> o) {
        getEditorLabel().setText();
        getAutoFilteringComboBox().removeAllItems();
        getAutoFilteringComboBox().setText(o.getName());
        selectedBO = o;
        selectedEntity = null;
        setMatchError();
    }

    public void addItem(T o) {
        getEditorLabel().setText();
        getAutoFilteringComboBox().removeAllItems();
        getAutoFilteringComboBox().setText(o.toUniqueString());
        selectedBO = new BODTO(o);
        selectedEntity = o;
        setMatchError();
    }
    public void addItem(String text) {
        if( text == null || text.length()==0 ) {
            return;
        }
        getAutoFilteringComboBox().setText(text);
        fireItemChangeEvent();
    }

    public void clear() {
        getAutoFilteringComboBox().setText("");
        getAutoFilteringComboBox().removeAllItems();
        getEditorLabel().setText();
        selectedBO = null;
        selectedEntity = null;

        if(getComboBoxModel() != null){
            getComboBoxModel().clear();
        }

        if( isItemChanged() ) {
            fireItemChangeEvent();
        }
    }

    public Method getQueryMethod() {
        return queryMethod;
    }

     /**
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD.
     *
     * Use this method on remoteservice to retrieve a resultset.
      *
     * @param queryRemote
     */
    public void setQueryMethod(Method queryMethod) {
        this.queryMethod = queryMethod;
    }

    public BusinessObjectQueryRemote getQueryRemote() {
        return queryRemote;
    }

    /**
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD
     *
     * @param queryMethodArgs
     */
    public void setQueryMethodArgs(Object[] queryMethodArgs) {
        this.queryMethodArgs = queryMethodArgs;
    }

    public void setComboBoxModel(BOAutoFilteringComboBoxModel<T> model){
        mode = Mode.MODEL;
        myModel = model;
    }

    public BOAutoFilteringComboBoxModel<T> getComboBoxModel(){
        return myModel;
    }

    public int getReplaceArgByTypedText() {
        return replaceArgByTypedText;
    }
    /**
     *
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD
     *
     * The argument at <code>replaceArgByTypedText</code>
     * of the arguments set via {@link #setQueryMethodArgs} will be replaced
     * by the String the user typed in the ComboBox on the fly. This argument must
     * be of type String.
     * @see #setQueryMethodArgs
     * @see #setQueryRemote
     * @see #setQueryMethod
     * @param replaceArgByTypedText
     */
    public void setReplaceArgByTypedText(int replaceArgByTypedText) {
        this.replaceArgByTypedText = replaceArgByTypedText;
    }

    /**
     * @return the boCLass
     */
    public Class getBoClass() {
        return boClass;
    }

    /**
     * @param boCLass the boCLass to set
     */
    public void setBoClass(Class boClass) {
        this.boClass = boClass;
        initAutofiltering();
    }

     //--------------------------------------------------------------------
    public void addItemChangeListener(PropertyChangeListener l) {
        listeners.add(l);
    }

    public void removeItemChangedListener(PropertyChangeListener l) {
        listeners.remove(l);
    }

    public boolean isItemChanged() {
        BODTO endBO = getSelectedItem();

        if( (startBO==null && endBO!=null) || (startBO!=null && endBO==null) ) {
            return true;
        }
        else if( startBO!=null && endBO!=null && !startBO.equals(endBO) ) {
            return true;
        }
        return false;
    }

    public void fireItemChangeEvent() {
        BODTO bo = getSelectedItem();
        PropertyChangeEvent e = new PropertyChangeEvent(this, ITEM_CHANGED, null, bo);
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(e);
        }
        getAutoFilteringComboBox().initStartValue();
        startBO = bo;
    }

    //--------------------------------------------------------------------
    public T getSelectedAsEntity() {
        if( selectedEntity != null ) {
            return selectedEntity;
        }
        if( getSelectedItem() == null ) {
            return null;
        }
        
        try {
            selectedEntity = (T)queryRemote.queryById(selectedBO.getId());
        } catch (Throwable t) {
        }

        return selectedEntity;
    }
   

    public BODTO getSelectedItem() {
        Object o = getAutoFilteringComboBox().getObject();
        BODTO boxBo = null;
        if( o instanceof BODTO ) {
            boxBo = (BODTO)o;
        }
        if( boxBo != null ) {
            if( selectedBO != null && !selectedBO.equals(boxBo) ) {
                selectedEntity = null;
            }
            selectedBO = boxBo;
            return selectedBO;
        }

        String text = getAutoFilteringComboBox().getText();

        if( selectedBO != null && text.equals(selectedBO.getName()) ) {
            return selectedBO;
        }

        selectedBO = null;
        selectedEntity = null;

        if( mode==Mode.MODEL ) {
            if( text != null && text.length()>0 ) {
                LOSResultList<BODTO<T>> r = myModel.getResults(text, new QueryDetail(0, 30));
                for( BODTO<T> t : r ) {
                    if( text.equals(t.getName()) ) {
                        if( selectedBO != null ) {
                            // more than one matching BO
                            selectedBO = null;
                            selectedEntity = null;
                            return null;
                        }
                        selectedBO = t;
                    }
                }
            }
            else {
                System.out.println("getSelectedItem: Do no select empty request");
            }
        }

        if( text.length()<=0 ) {
            return null;
        }
        
        try {
            selectedEntity = (T) queryRemote.queryByIdentity(text);
            selectedBO = new BODTO(selectedEntity);
        } catch (Throwable t) {
            return null;
        }

        return selectedBO;
    }


    public String getSelectedAsText() {
        BODTO bo = getSelectedItem();
        if( bo != null ) {
            return bo.getName();
        }
        return null;
    }
    
}

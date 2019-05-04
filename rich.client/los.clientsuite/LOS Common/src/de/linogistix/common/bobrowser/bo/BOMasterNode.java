/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo;

import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import de.linogistix.common.bobrowser.bo.editor.BooleanEditorReadOnly;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.los.query.BODTO;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;

/**
 * BOMasterNodes are suitable for being shown in a master view. A BOMasterNode is  
 * used as a handle for BasicEntities. 
 *
 * Thus, a BOMasterNode contains less information than the BOEntityNode which itself
 * contains all information of the BasicEntity it is associated with.
 *
 * @{link getId} returns the id of the basicEntity.
 *
 * @author trautm
 */
public class BOMasterNode extends AbstractNode implements JasperExporterNode {

    private BODTO entity;
    protected Sheet.Set sheet = null;
    protected BO bo;

    public BOMasterNode(BODTO entity) {
        this(entity, null);
    }

    @Override
    public boolean canRename() {
        return false;
    }
    

    /** Creates a new instance of BOMasterNode */
    public BOMasterNode(BODTO entity, BO bo) {
        super(Children.LEAF);
        this.entity = entity;
        setName(entity.getName());
        if (bo != null) {
            setIconBaseWithExtension(bo.getIconPathWithExtension());
        } else {
            setIconBaseWithExtension(BO.ICON_PATH_DEFAULT);
        }
    }

    public Long getId() {
        return getEntity().getId();
    }

    public BODTO getEntity() {
        return entity;
    }

    public Action[] getActions(boolean b) {
        if (getBo() != null) {
            return getBo().getMasterActions(b);
        } else {
            return new Action[0];
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public SystemAction getDefaultAction() {
        if (getBo() != null) {
            return getBo().getDefaultMasterAction();
        } else {
            return null;
        }
    }

    public BO getBo() {
        return bo;
    }

    public void setBo(BO bo) {
        this.bo = bo;
    }

    @Override
    public String getDisplayName() {
        return getEntity().getName();
    }

    @Override
    public String getName() {
        return getEntity().getName();
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
        }
        return new PropertySet[]{sheet};
    }
    
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[0];

        return props;
    }

    public Object getExportBeanObject() {
        return getEntity();
    }

    public PropertySet[] getExportPropertySets() {
        return getPropertySets();
    }
    
    public static final class BOMasterNodeProperty<T> extends BOMasterNodeReadWriteProperty<T> {


         public BOMasterNodeProperty(String name, Class<T> type, String displayName, String shortDescription, T value) {
            super(name, type, displayName, shortDescription, value);
 
        }

        public BOMasterNodeProperty(String name, Class<T> type, T value, Class bundleResolver) {
            super(
                    name,
                    type,
                    value,
                    bundleResolver);
        }

        public BOMasterNodeProperty(String name, Class<T> type, T value, Class bundleResolver, boolean resolveValue) {
            super(
                    name,
                    type,
                    value,
                    bundleResolver,
                    resolveValue);
        }

        @Override
        public void setValue(T arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            // do nothing
        }
    }

    public static class BOMasterNodeReadWriteProperty<T> extends PropertySupport.ReadWrite<T> {

        T value;

        boolean resolveValue = false;

        @Override
        public PropertyEditor getPropertyEditor() {
            if (getValueType().isAssignableFrom(Boolean.class)){
                return new BooleanEditorReadOnly();
            }
            return new PlainObjectReadOnlyEditor();
        }



        public BOMasterNodeReadWriteProperty(String name, Class<T> type, String displayName, String shortDescription, T value) {
            super(name, type, displayName, shortDescription);
            this.value = value;
            // not any more because of paging
            setValue("ComparableColumnTTV", Boolean.TRUE);
            setValue("suppressCustomEditor", Boolean.TRUE);

        }

        public BOMasterNodeReadWriteProperty(String name, Class<T> type, T value, Class bundleResolver) {
            this(
                    name,
                    type,
                    BundleResolve.resolve(new Class[]{bundleResolver}, name, new Object[0]),
                    BundleResolve.resolve(new Class[]{bundleResolver}, name+"_DESCR", new Object[0]),
                    value);
        }

        public BOMasterNodeReadWriteProperty(String name, Class<T> type, T value, Class bundleResolver, boolean resolveValue) {
            this(
                    name,
                    type,
                    BundleResolve.resolve(new Class[]{bundleResolver}, name, new Object[0]),
                    BundleResolve.resolve(new Class[]{bundleResolver}, name+"_DESCR", new Object[0]),
                    value);
            this.resolveValue = resolveValue;
            if (value instanceof String && resolveValue){
                this.value = (T) BundleResolve.resolve(new Class[]{bundleResolver}, value.toString(), new Object[0]);
            }
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return this.value;
        }

        @Override
        public void setValue(T arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            this.value = arg0;
        }
    }
    
}


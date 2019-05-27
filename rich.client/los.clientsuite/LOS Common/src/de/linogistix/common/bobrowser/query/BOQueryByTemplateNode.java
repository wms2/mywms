/*
 * BOQueryByTemplateNode.java
 *
 * Created on 14. Januar 2007, 20:54
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.binding.BOBeanNodeDescriptor;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.query.gui.object.BOQueryByTemplateProperty;
import de.linogistix.common.bobrowser.crud.gui.object.BOEditNode;
import java.beans.IntrospectionException;
import java.util.logging.Logger;
import javax.persistence.Transient;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;

/**
 * Node for querying BusinessObjects by template. Has basically the same properties
 * as the corresponding {@link BOEntityNodeReadOnly} but wraps all properties in
 * BOQueryByTemplateProperty.
 * 
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryByTemplateNode extends BOEditNode {

    private static final Logger log = Logger.getLogger(BOQueryByTemplateNode.class.getName());

//  private List<BOQueryByTemplateWrapper> wrappers;
    /** Creates a new instance of BOQueryByTemplateNode */
    public BOQueryByTemplateNode(BasicEntity entity) throws IntrospectionException {
        super(entity);
    }

    /**
     * Creates and returns a Property for the given PropertyDescriptor. Returns null
     * if this PropertyDescriptor should be ignored.
     */
    @Override
    protected Property createProperty(Object bean, java.beans.PropertyDescriptor p) {
        Property orig;
        BOQueryByTemplateProperty property;
        Object attr;

       
        
        if (p.getName() == null || p.getName().equals("")) {
            return null;
        } else if (p.getName().equals("class")) {
            return null;
        } else if (p.getReadMethod() == null){
            return null;
        } else if (p.getReadMethod().getAnnotation(Transient.class) != null){
            return null;
        } 

        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        BO bo = (BO) l.lookup(bean.getClass());

        if (bo != null) {
            BOBeanNodeDescriptor descr = bo.getDescriptor();
            if (descr != null) {
                if (descr.getDescriptions() != null && descr.getDescriptions().size() > 0) {
                    PropertyDescriptorElement e = descr.getDescriptions().get(p.getName());
                    if (e != null) {
                        if (!e.isPersistentField()) {
                            System.out.println("---- No persistent field : "+p.getName());
                            return null;
                        }
                    }
                }
            }
        }

        if (p.getName().equals("lock")){
            log.info("" + p.getValue("lockStates"));           
        }
        orig = super.createProperty(bean, p);
        if (orig.getName().equals("lock")){
            log.info("" + orig.getValue("lockStates"));           
        }
        
        property = new BOQueryByTemplateProperty(orig);

        // Propagate helpID's.
        Object help = p.getValue("helpID"); // NOI18N

        if ((help != null) && (help instanceof String)) {
            property.setValue("helpID", help); // NOI18N

        }

        return property;

    }

//  protected Class propertyEditorClass(PropertyDescriptor p, Class defaultEditorClass) {
//    return BOQueryByEditor.class;
//  }
    public boolean canDestroy() {
        return false;
    }
}

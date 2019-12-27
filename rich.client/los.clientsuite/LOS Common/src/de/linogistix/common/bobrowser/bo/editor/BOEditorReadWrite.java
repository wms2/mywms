package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.los.query.BODTO;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.beans.IntrospectionException;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.mywms.model.BasicEntity;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author krane
 */
public class BOEditorReadWrite extends PropertyEditorSupport implements ExPropertyEditor {

    private Logger log = Logger.getLogger(BOEditorReadWrite.class.getName());
    private BOEntityNode boBeanNode;
    private PropertyEnv env;
    private Class typeHint;

    /**
     * Creates a new instance of BOEditor
     */
    public BOEditorReadWrite() {
    }

    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        return;
    }

    public String getAsText() {
        Object o = getValue();
        String ret = "";
        BasicEntity bo;

        if (o == null) {
//            log.info(" o == null");
            return ret;
        }

        bo = getEntity();
        if (bo != null) {
            ret = bo.toUniqueString();
        } else {
//            log.info("entity == null");
        }

        return ret;
    }

    /**
     * 
     * @param value either an instance of {@link BasicEntity} or {@link BODTO}.
     */
    public void setValue(Object value) {
        try {
            if (value != null) {
                BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
                BO bo;
                if (value instanceof BasicEntity) {
                    BasicEntity e = (BasicEntity) value;
//                    bo = (BO) l.lookup(e.getClass());
//                    value = bo.getQueryService().queryById(e.getId());
                    this.setBoBeanNode(new BOEntityNode(e));
                } else if (value instanceof BODTO) {
                    Class typeh = getTypeHint();
                    if (typeh != null) {
                        BODTO boDTO = (BODTO) value;
                        bo = (BO) l.lookup(typeh);
                        value = bo.getQueryService().queryById(boDTO.getId());
                        this.setBoBeanNode(new BOEntityNode((BasicEntity) value));
                    } else {
                        log.warning("setValue without type information");
                    }
                } else {
                    log.warning("unknown type: " + value.getClass().getName());
                }
            } else {
                log.warning("setValue to null");
            }
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object getValue() {
        return this.getEntity();
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        int w, v;
        Image img;
        ImageIcon ico;

//        log.log(Level.INFO, "paint !!!! ");

        if (getTypeHint() != null) {
            BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
            BO bo = (BO) l.lookup(getTypeHint());
            try {
                ico = new ImageIcon(bo.getClass().getResource("/" + bo.getIconPathWithExtension()));
            } catch (Throwable t) {
//                log.log(Level.WARNING, "Cannot resolve icon: " + t.getMessage());
                ico = new ImageIcon(
                        de.linogistix.common.res.icon.IconResolver.class.
                        getResource("/de/linogistix/common/res/icon/Document.png"));
            }
        } else {
            ico = new ImageIcon(
                        de.linogistix.common.res.icon.IconResolver.class.
                        getResource("/de/linogistix/common/res/icon/Document.png"));
        }


        img = ico.getImage();

        ImageObserver io = new ImageObserver() {

            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return true;
            }
        };
        gfx.drawImage(img, box.x, box.y, io);
        w = img.getWidth(io) + 10;
        v = gfx.getFontMetrics().getAscent();
//        log.info("drawing: " + getAsText());
        gfx.drawString(getAsText(), box.x + w, box.y + v);
        return;
    }

    @Override
    public String getJavaInitializationString() {
        return "???";
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        try {
            if (getEntity() == null) {
                if (env != null) {
                    env.setState(PropertyEnv.STATE_INVALID);
                }
                return new NoValueMessage();
            } else {
                BOEditorPanelReadWrite panel = new BOEditorPanelReadWrite(new BOEntityNode(getEntity()));

                return panel;
            }

        } catch (IntrospectionException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    public BOEntityNode getBoBeanNode() {
        return boBeanNode;
    }

    public BasicEntity getEntity() {
        if (boBeanNode != null) {
            return boBeanNode.getBo();
        }
        return null;
    }

    public void setBoBeanNode(BOEntityNode boBeanNode) {
//        log.info("setBOBeanNode: " + boBeanNode);
        this.boBeanNode = boBeanNode;
    }

    public void attachEnv(PropertyEnv propertyEnv) {
        Node.Property p;
        this.env = propertyEnv;
        p = (Node.Property) this.env.getFeatureDescriptor();
        Class th = (Class) this.env.getFeatureDescriptor().getValue(PropertyDescriptorElement.VALUE_TYPE_HINT);
        if (th != null) {
            this.typeHint = th;
        } else {
            this.typeHint = p.getValueType();
        }
    }

    public PropertyEnv getEnv() {
        return env;
    }

    public Class getTypeHint() {
        return typeHint;
    }
}

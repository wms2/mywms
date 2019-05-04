/*
 * BOCollectionEditor.java
 *
 * Created on 27. Februar 2007, 03:21
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOEntityNode;
import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.hibernate.LazyInitializationException;
import org.mywms.model.BasicEntity;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Property Editor for a {@link Collection}s of {@link BasicEntity}.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOCollectionEditorReadOnly extends PropertyEditorSupport
        implements ExPropertyEditor,
        ActionListener {

    private List entities;
    protected PropertyEnv env;
    protected Class typeHint;
    protected boolean editable = true;
    private static Logger log = Logger.getLogger(BOCollectionEditorReadOnly.class.getName());

    public void setAsText(String text) {
        return;
    }

    public String getAsText() {
        try {
            Object o = getValue();
            String ret = "";
            if (o == null || (!(o instanceof Collection))) {
                return ret;
            }
            return toString(o);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Exception loading attribute: "+t.getClass().getSimpleName()+", "+t.getMessage());
            return NbBundle.getMessage(CommonBundleResolver.class, "BusinessExcpetion.ValueCannotBeRetrieved");
        }

    }

    protected static String toString(Object o) {
        if (o instanceof BasicEntity) {
            return ((BasicEntity) o).toUniqueString();
        } else if (o instanceof BODTO) {
            return ((BODTO) o).getName();
        } else if (o instanceof Collection) {
            StringBuffer b = new StringBuffer();
            // might LazyInitializationException
            int i = 0;
            for (Object elem : (Collection) o) {
                if (elem instanceof BasicEntity) {
                    b.append(((BasicEntity) elem).toUniqueString());
                    b.append(";");
                } else if (o instanceof BODTO) {
                    b.append(((BODTO) elem).getName());
                    b.append(";");
                }
                i++;
                if( i>10 ) {
                    log.log(Level.WARNING, "BOCollectionEditorReadOnly: too much entities (>10). Will not display");
                    b.append("...");
                    return new String(b);
                }
            }
            return new String(b);
        } else {
            return o.toString();
        }
    }

    public void setValue(Object value) {
        try {
            if (value != null) {
                if (!(value instanceof List)) {
                    this.setEntities(new ArrayList((Collection) value));
                } else {
                    this.setEntities((List) value);
                }
                firePropertyChange();
            } else {
                return;
            }
        } catch (LazyInitializationException lex) {
            log.log(Level.INFO, "setValue, LazyInitializationException: "+lex.getMessage());
            throw new RuntimeException(NbBundle.getMessage(CommonBundleResolver.class, "BusinessExcpetion.ValueCannotBeRetrievedLazily"));
        } catch (Throwable ex) {
            log.log(Level.INFO, "setValue, Exception: "+ex.getClass().getSimpleName()+", "+ex.getMessage());
            throw new RuntimeException(NbBundle.getMessage(CommonBundleResolver.class, "BusinessExcpetion.ValueCannotBeRetrieved"));
        }
    }

    public Object getValue() {
        return this.getEntities();
    }

    public boolean isPaintable() {
        return true;
    }

    public void paintValue(Graphics gfx, Rectangle box) {
        int w, v;
        Image img;
        ImageIcon ico;

        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        try {
            if (typeHint != null) {
                BO bo = (BO) l.lookup(typeHint);
                ico = new ImageIcon(bo.getClass().getResource("/" + bo.getIconPathWithExtension()));
            } else {
                ico = new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/Document.png"));
            }
        } catch (Throwable t) {
//            log.log(Level.WARNING, "no icon can be resolved:" + t.getMessage());
            ico = new ImageIcon(getClass().getResource("/de/linogistix/common/res/icon/Document.png"));
        }

        img = ico.getImage();

        ImageObserver io = new ImageObserver() {

            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return true;
            }
        };
        Graphics g = gfx.create(box.x, box.y, box.width, box.height);
        g.drawImage(img, box.x, box.y, io);
        w = img.getWidth(io) + 10;
        v = gfx.getFontMetrics().getAscent();
        g.drawString(getAsText(), box.x + w, box.y + v);
        g.dispose();
        return;
    }

    public String getJavaInitializationString() {
        return null;
    }

    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        if (getEntities() == null || getEntities().size() == 0) {
            if (env != null) {
                env.setState(PropertyEnv.STATE_INVALID);
            }
            return new NoValueMessage();
        } else {
            try {
                this.typeHint = resolveType(getEntities());
            } catch (IllegalArgumentException iax) {
                log.warning("Could not resolve Type: " + iax.toString());
            }
            return new BOCollectionEditorReadOnlyPanel(this);
        }
    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public void attachEnv(PropertyEnv propertyEnv) {
        this.env = propertyEnv;
        Node.Property p = (Node.Property) this.env.getFeatureDescriptor();
        try {
            this.typeHint = resolveType(getEntities());
        } catch (IllegalArgumentException ex) {
            return;
        }
    }

    public PropertyEnv getEnv() {
        return env;
    }

    public List<BasicEntity> getEntities() {
        return entities;
    }

    public void setEntities(List entities) {
        List tmp = new ArrayList();
        try {
            for (Object o : entities) {
                if (o instanceof BODTO) {
                    BODTO boTDO = (BODTO) o;
                    BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
                    try {
                        if (typeHint != null) {
                            BO bo = (BO) l.lookup(typeHint);
                            BasicEntity e = bo.getQueryService().queryById(boTDO.getId());
                            tmp.add(e);
                        } else if (boTDO.getClassName() != null) {
                            Class c = Class.forName(boTDO.getClassName());
                            BO bo = (BO) l.lookup(c);
                            BasicEntity e = bo.getQueryService().queryById(boTDO.getId());
                            tmp.add(e);
                        }
                    } catch (Exception ex) {
                        log.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                } else if (o instanceof BasicEntity) {
                    tmp.add(o);
                } else {
                    log.log(Level.SEVERE, "wrong type: " + o.getClass());
                }
            }
            this.entities = tmp;
        // To display a text in the not loadable field do not catch this exception
        } catch (LazyInitializationException lex) {
//            log.log(Level.INFO, lex.getMessage(), lex);
            log.log(Level.INFO, "Cannot read uninitialized list");
        }
    }

    /**
     * @throws IllegalArgumentException if type of elements can't be resolved
     */
    Class resolveType(Collection collection) throws IllegalArgumentException {

        Class c = null;

        if (collection != null && collection.size() != 0) {
            // retrieve type from element
            for (Object elem : collection) {
                c = elem.getClass();
                break;
            }
        } else {

            if (getEnv() == null) {
                return null;
            }

            Object o = getEnv().getFeatureDescriptor().getValue(PropertyDescriptorElement.VALUE_TYPE_HINT);

            if (o == null || (!(o instanceof Class))) {
                //log.info("*** " + "no ValueHint information");
                throw new IllegalArgumentException("cannot resolve types");
            } else {
                //log.info("*** " + "found ValueHint information " + o.toString());
                c = (Class) o;
            }
        }
        return c;
    }

    public void actionPerformed(ActionEvent e) {
        //
    }

    public Class getTypeHint() {
        return typeHint;
    }

    BOEntityNode getBOBeanNode() {
        Object[] beans = getEnv().getBeans();
        if (beans == null || beans.length < 1) {
            return null;
        } else {
            return (BOEntityNode) beans[0];
        }
    }
}
/*
 * BOCollectionEditor.java
 * 
 *
 * Created on 27. Februar 2007, 03:21
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.hibernate.LazyInitializationException;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A Property Editor for a {@link Collection}s of {@link BasicEntity}.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOCollectionEditor extends BOCollectionEditorReadOnly
        implements ExPropertyEditor,
        ActionListener {

        
    private Logger log = Logger.getLogger(BOCollectionEditor.class.getName());

    private InplaceEditor inplaceEditor = null;


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
        Graphics g = gfx.create(box.x, box.y,box.width, box.height);
        g.drawImage(img, box.x, box.y, io);
        w = img.getWidth(io) + 10;
        v = gfx.getFontMetrics().getAscent();
        g.drawString(getAsText(), box.x + w, box.y + v);
        g.dispose();
        return;
    }


    public Component getCustomEditor() {
        NotifyDescriptor d;

        try {
            try {
                this.typeHint = resolveType(getEntities());
            } catch (IllegalArgumentException iax) {
                //
            } finally {
                if (getTypeHint() == null) {
                    BOEditorTypeChooser ch = new BOEditorTypeChooser();
                    d = new NotifyDescriptor(
                            ch,
                            NbBundle.getMessage(CommonBundleResolver.class, "BOCollectionTypeChooser.title"),
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.WARNING_MESSAGE,
                            null,
                            NotifyDescriptor.OK_OPTION);

                    DialogDisplayer.getDefault().notify(d);
                    this.typeHint = ch.type;
                }
            }
            if (getTypeHint() == null) {
                ExceptionAnnotator.annotate(new BOEditorTypeException());
            } else {
                Property p = null;

                if (env.getFeatureDescriptor() instanceof Property){
                    p = (Property)env.getFeatureDescriptor();
                }

                if (p != null && p.canWrite()){
                    log.info("can write!");
                    return new BOCollectionEditorPanel(this);
                } else if (p != null && ! p.canWrite()){
                    log.info("cannot write!");
                    return new BOCollectionEditorReadOnlyPanel(this);
                } else{
                    log.info("can write???");
                    return new BOCollectionEditorPanel(this);
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return null;
    }

    public boolean supportsCustomEditor() {
        return true;
    }

   public void attachEnv(PropertyEnv propertyEnv) {
        super.attachEnv(propertyEnv);
    }
}

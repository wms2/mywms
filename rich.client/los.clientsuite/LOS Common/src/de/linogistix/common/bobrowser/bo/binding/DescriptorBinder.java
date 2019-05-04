/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.binding;

import de.linogistix.common.preferences.AppPreferences;
import de.linogistix.common.res.CommonBundleResolver;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;

/**
 *
 * @author trautm
 */
public final class DescriptorBinder {
  
  private static final Logger log = Logger.getLogger(DescriptorBinder.class.getName());
  
  public static String configFolder(){
    String ret = AppPreferences.getConfigFolder("bobrowser").getPath();
    return ret;
  }
  
  public static FileObject getDescriptorFile(Class clazz){
    FileObject d;
    FileObject xml;
    String xmlName;
    
    try {
      xmlName = clazz.getSimpleName() + "Descriptor";
      d = AppPreferences.getConfigFolder("bobrowser");
      if (d == null){
        log.warning("no config folder bobrowser found");
        return null;
      }
      xml = d.getFileObject(xmlName,"xml");
      
      if (xml == null) {
        log.log(Level.INFO,"no file found: " + xmlName);
        return null;
      }
      
      return xml;
      
    } catch (Exception ex) {
      log.log(Level.SEVERE,ex.getMessage(),ex);
      // TODO file can not be created , do something about it
      //log.log(Level.WARNING,ex.getMessage(),ex);
      //ExceptionAnnotator.annotate(new InternalErrorException(ex));
      return null;
    }
    
  }

    public static InputStream getDescriptorStream(Class clazz){
    FileObject d;
    FileObject xml;
    String xmlName;

    try {
      xmlName = "de/linogistix/common/res/descriptor/"+clazz.getSimpleName() + "Descriptor.xml";
      InputStream is = CommonBundleResolver.class.getClassLoader().getResourceAsStream(xmlName);
      return is;

    } catch (Exception ex) {
      log.log(Level.SEVERE,ex.getMessage(),ex);
      // TODO file can not be created , do something about it
      //log.log(Level.WARNING,ex.getMessage(),ex);
      //ExceptionAnnotator.annotate(new InternalErrorException(ex));
      return null;
    }

  }

  public static BOBeanNodeDescriptor getDescriptor(Class clazz){
    FileObject xml;
    FileLock lock;
    BOBeanNodeDescriptor desc;


    try {

      JAXBContext context = JAXBContext.newInstance( BOBeanNodeDescriptor.class );
      Unmarshaller u = context.createUnmarshaller();
      InputStream in = null;

      xml = getDescriptorFile(clazz);
      if (xml != null){
        in = xml.getInputStream();
      }
      else {
        in = getDescriptorStream(clazz);
      }
      if( in == null ) {
          return null;
      }

      desc = (BOBeanNodeDescriptor)u.unmarshal(in);
      in.close();
      log.log(Level.INFO,"Binding OK for Class " + clazz );
      return desc;

    } catch (Exception ex) {
      log.log(Level.SEVERE,ex.getMessage(),ex);
      // TODO file can not be created , do something about it
      //log.log(Level.WARNING,ex.getMessage(),ex);
      //ExceptionAnnotator.annotate(new InternalErrorException(ex));
      return null;
    }
  }
  
}

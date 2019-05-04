/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.binding;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.mywms.model.BasicEntity;

/**
 * Just an example to generate XML from this Java code.
 *
 * @author trautm
 */
public class DescriptionTree {
  
  /** Creates a new instance of DescriptionTree */
  public DescriptionTree() {
    
  }
  
  public void create() throws JAXBException{
    BOBeanNodeDescriptor b = new BOBeanNodeDescriptor();
    PropertyGroupElement g = new PropertyGroupElement();
    PropertyGroupElement g2 = new PropertyGroupElement();
    PropertyDescriptorElement e;
    PropertyDescriptorElement e2;
    
    g.setHidden(false);
    g.setName("basic");
    g.setGroupPosition(1);
    
    g2.setHidden(false);
    g2.setName("other");
    g2.setGroupPosition(2);
    
    e = new PropertyDescriptorElement();
    e.setHidden(true);
    e.setPosition(100);
    e.setGroup(g);
    e.setTypeHint(Integer.class);
    
    e2 = new PropertyDescriptorElement();
    e2.setHidden(true);
    e2.setPosition(200);
    e2.setGroup(g2);
    e2.setTypeHint(Boolean.class);
    
    System.out.println("### " + e.getPosition());
    
    Map<String,PropertyDescriptorElement> map = new HashMap();
    map.put("lock", e);
    map.put("locked", e);
    
    b.setForClass(BasicEntity.class);
    b.setGroups(new PropertyGroupElement[]{g,g2});
    b.setDescriptions(map);
   
    JAXBContext context = JAXBContext.newInstance( BOBeanNodeDescriptor.class );
    Marshaller m = context.createMarshaller();
    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
    System.out.println("--BEGIN------------------------------------");
    m.marshal( b, System.out );
    System.out.println("--END------------------------------------");
    StringWriter w = new StringWriter();
    m.marshal(b, w);
    
    String xml = w.toString();
    Unmarshaller u = context.createUnmarshaller();
    StringReader r = new StringReader(xml);
    BOBeanNodeDescriptor d = (BOBeanNodeDescriptor)u.unmarshal(r);
    
  }
  
  public static void main(String[] args){
    try {
      new DescriptionTree().create();
    } catch (JAXBException ex) {
      ex.printStackTrace();
    }
  }
  
}

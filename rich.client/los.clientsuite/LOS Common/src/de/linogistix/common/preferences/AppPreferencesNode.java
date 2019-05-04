/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.preferences;

import de.linogistix.common.res.CommonBundleResolver;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class AppPreferencesNode extends AbstractNode{
    
    AppPreferences prefs;
    
    AppPreferencesSet set;
    
    Node.PropertySet[] pSets;
    
    public AppPreferencesNode(AppPreferences prefs, String name){
        super(Children.LEAF);
        this.prefs = prefs;
        this.set = new AppPreferencesSet(prefs.getProperties());
        this.pSets = new Node.PropertySet[]{set};
        setName(name);
    }
    
    public Node.PropertySet[] getPropertySets() {
        return pSets;
    }
    
    
    class AppPreferencesSet extends PropertySet{
        
        Properties properties;
        
        Node.Property[] nodeProperties;
        
        /** Creates a new instance of AppPreferencesSet */
        public AppPreferencesSet(Properties properties) {
            if (properties ==  null){
                throw new NullPointerException();
            }
            this.properties = properties;
            setName("AppPreferencesSet");
            setDisplayName(NbBundle.getMessage(CommonBundleResolver.class,"options"));
            setExpert(false);
            setPreferred(true);
        }
        
        
        public Node.Property[] getProperties() {
            AppPreferencesProperty [] ret;
            
            if (nodeProperties != null){
                return nodeProperties;
            }
            
            List<AppPreferencesProperty> lap = new ArrayList<AppPreferencesProperty>();
            
            Enumeration e = properties.keys();
            
            while(e.hasMoreElements()){
                String key = (String)e.nextElement();
                if (key == null){
                    continue;
                }
                String value = properties.getProperty(key);
                AppPreferencesProperty p = new AppPreferencesProperty(key,this);
                lap.add(p);
            }
            
            ret = lap.toArray(new AppPreferencesProperty[0]);
            this.nodeProperties = ret;
            return ret;
        }        
        
        
        
    }
    
    class AppPreferencesProperty extends Node.Property{
        
        AppPreferencesSet propertySet;
        String key;
        String value;
        
        public AppPreferencesProperty(String key, AppPreferencesSet propertySet){
            super(String.class);
            this.key = key;
            this.propertySet = propertySet;
            setName(key);
            setDisplayName(key);
        }
        
        public boolean canRead() {
            return true;
        }
        
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return propertySet.properties.getProperty(key);
        }
        
        public boolean canWrite() {
            return true;
        }
        
        public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            propertySet.properties.setProperty(key,(String)object);
        }
        
    }
}

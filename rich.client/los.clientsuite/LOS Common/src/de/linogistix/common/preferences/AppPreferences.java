/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.preferences;

import de.linogistix.common.exception.InternalErrorException;
import de.linogistix.common.util.ExceptionAnnotator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 * Load Properties from config file and store them back.
 *
 * Inspired by Geertjan's Weblog <a href="http://blogs.sun.com/geertjan/entry/s"/>
 *  
 */
public class AppPreferences {
  
  private static final Logger log = Logger.getLogger(AppPreferences.class.getName());
  
  private String propertiesFile;
  private static final String PROPERTIES_EXTENSION =  "properties";
  private FileObject settingsFolder;
  private FileObject settingsFile;
  private FileLock lock;
  private Properties settings;
  private String[] noStoreProps = new String[0];
  
  /** There can only be one!*/
  private static Map<String, AppPreferences> INSTANCES = new HashMap<String, AppPreferences>();
  
  private final static FileObject CONFIG_ROOT = FileUtil.getConfigRoot();
  
  private AppPreferences(String folder, String propertiesFileBaseName, Properties defaultProperties) {
    
    this.propertiesFile = propertiesFileBaseName;
    
    settings = new Properties();
    settingsFolder= AppPreferences.getConfigFolder(folder);
    
    if (settingsFolder==null) {
      try {
        settingsFolder=AppPreferences.getConfigRoot().createFolder(folder);
// Do not store everything per default. You cannot get upgrades
//        store();
      } catch (IOException ex) {
        ex.printStackTrace();
        ExceptionAnnotator.annotate(new InternalErrorException(ex));
      }
    } else {
      load(defaultProperties);
    }
  }
  
  public static AppPreferences getSettings(String propertiesFileBaseName, Properties defaultSettings) {
    return AppPreferences.getSettings("Settings",propertiesFileBaseName, defaultSettings);
  }
  
  public static String getSettingsPath(){
    return AppPreferences.getConfigFolder("Settings").getPath();
  }
  
  /**
   *
   */
  public static AppPreferences getSettings(String folder, String propertiesFileBaseName, Properties defaultSettings) {
    AppPreferences ret;
    
    ret = INSTANCES.get(propertiesFileBaseName);
    if ( ret == null){
      ret = new AppPreferences(folder,propertiesFileBaseName, defaultSettings);
      INSTANCES.put(propertiesFileBaseName,ret);
    }
    return ret;
  }
  
  public static FileObject getConfigRoot(){
    return CONFIG_ROOT;
  }
  
  public static FileObject getConfigFolder(String folder){
    return getConfigRoot().getFileObject(folder);
  }
  
  public static Properties loadFromClasspath(String resourcePath){
    Properties p = new Properties();
    
    InputStream is = AppPreferences.class.getClassLoader().getResourceAsStream(resourcePath);
    if (is == null){
      throw new NullPointerException();
    }
    
    try {
      //Just Fallback for default settings
      p.load(is);
      // Retreieve from file or new with default settings
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return p;
  }
  
  public void store() {
    try {
      settingsFile = settingsFolder.getFileObject(propertiesFile,PROPERTIES_EXTENSION);
      if (settingsFile==null) {
        settingsFile = settingsFolder.createData(propertiesFile,PROPERTIES_EXTENSION);
      }
      
      lock = settingsFile.lock();
      OutputStream out = settingsFile.getOutputStream(lock);
      Properties tmp = (Properties)settings.clone();
      for (String key : getNoStoreProps()){
        tmp.remove(key);
      }
      tmp.store(out,"Configuration File " + propertiesFile);
      out.close();
      lock.releaseLock();
    } catch (IOException ex) {
      // TODO file can not be created , do something about it
      ex.printStackTrace();
      ExceptionAnnotator.annotate(new InternalErrorException(ex));
    }
  }
  /**
   * Tries first to load from config file as set in parameters of {@link #getSettings()}.
   * If this failes (i.e. file not found) it uses default properties.
   *
   * @param defaultProperties fall back if no properties file is found
   */
  public void load(Properties defaultProperties) {
    settingsFile = settingsFolder.getFileObject(propertiesFile, PROPERTIES_EXTENSION);
    if (settingsFile != null) {
      try {
        InputStream in = settingsFile.getInputStream();
        settings.load(in);
        in.close();
      } catch (IOException ex) {
        ex.printStackTrace();
        settings = defaultProperties;
      }
    } else {
      log.warning("No config file <" + propertiesFile + "> found!");
    }
  }
  
  public String getValue(String key) {
    return settings.getProperty(key);
  }
  
  public void setValue(String key, String value) {
    settings.setProperty(key, value.trim());
  }
  
  public Properties getProperties(){
    return settings;
  }
  
  boolean valid() {
    // TODO check whether form is consistent and complete
    return true;
  }

    public String[] getNoStoreProps() {
        return noStoreProps;
    }

    public void setNoStoreProps(String[] noStoreProps) {
        this.noStoreProps = noStoreProps;
    }
  
  
  
}
/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 * Will be initiated in the LOS installer.
 * @author artur
 */
public class Menubar {

    private static Menubar instance = null;
    private List<String> menubarList = new ArrayList<String>();
    private final String MENU_PATH = System.getProperty("netbeans.user") + File.separator + "config" + File.separator + "Menu" + File.separator;

    /**
     * prevent instantation
     */
    private Menubar() {
    }

    public synchronized static Menubar getInstance() {
        if (instance == null) {
            instance = new Menubar();
            instance.init();
        }
        return instance;
    }

    /**
     * Init the menu
     */
    private void init() {
        FileObject fileObj = FileUtil.getConfigRoot().getFileObject("Menu");
        for (FileObject file : fileObj.getChildren()) {
            if (file.isFolder()) {
                menubarList.add(new String(file.getName()));
            }
        }
    }

    /**
     * Menubar items e.g. "View, Tools, Windows..."
     * @return get the items of the menubar back
     */
    public List<String> getMenubarItems() {
        return menubarList;
    }

    public void setMenubarItems(List<String> list) {
        this.menubarList = list;
    }

    /**
     * Hide the menu for e.g. "Files,View...."
     */
    public void hideMenubar() {
        for (String file : menubarList) {
            hideMenuItem(file);
        }
    }

    private String[] getHiddenFiles() {
        String path = MENU_PATH;
        File file = new File(path);
        String[] fileArray = file.list();
        List result = new ArrayList();
        if (fileArray != null) {
            for (String filename : fileArray) {
                if (filename.endsWith("_hidden")) {
                    result.add(filename);
                }
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    /**
     * Show the menu for e.g. "Files,View...."
     */
    public void showMenubar() {
        deleteHiddenFiles(Arrays.asList(getHiddenFiles()));
    }

    /**
     * If Menu is visible
     * @return if visible yes else no
     */
    private boolean isMenubarVisible() {
        if (getHiddenFiles().length != 0) {
            return false;
        }
        return true;
    }

    /**
     * create a menu item for e.g. "Tools"
     * @param name Menu item to create
     */
    public void createMenuItem(String name) {
//        listItems(name);
        try {
            FileObject fo = FileUtil.getConfigRoot().getFileObject("Menu");
            FileLock cfo2 = fo.lock();
            FileObject fo2 = fo.createFolder(name);
            cfo2.releaseLock();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public List<String> getItems(String path) {
        List<String> itemList = new ArrayList<String>();
        FileObject fileObj =
                FileUtil.getConfigRoot().getFileObject(path);
        for (FileObject file : fileObj.getChildren()) {
            if (file.isFolder()) {
                itemList.add(new String(file.getName()));
            }
        }
        return itemList;
    }

    /**
     * hide a menu item for e.g. "Tools"
     * @param name Menu item to hide
     */
    private void hideItem(String path, String name) {
        try {
            FileObject fo = FileUtil.getConfigRoot().getFileObject(path);
            FileObject fo2 = fo.getFileObject(name);
            if (fo2 != null) {
                FileLock cfo2 = fo2.lock();
                fo2.rename(cfo2, name + "_hidden", "");
                cfo2.releaseLock();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
    }

    /**
     * hide a menu item for e.g. "Tools"
     * @param name Menu item to hide
     */
    public void hideMenuItem(String name) {
        try {
            FileObject fo = FileUtil.getConfigRoot().getFileObject("Menu");
            FileObject fo2 = fo.getFileObject(name);
            if (fo2 != null) {
                FileLock cfo2 = fo2.lock();
                fo2.rename(cfo2, name + "_hidden", "");
                cfo2.releaseLock();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
    }

    private void deleteHiddenFiles(List<String> layerContent) {
        for (String filename : layerContent) {
            String path = MENU_PATH + filename;
            File tmpFile = new File(path);
            if (!tmpFile.exists()) {
                continue;
            }
            FileObject hidden = FileUtil.toFileObject(tmpFile);
            try {
                hidden.delete();
            } catch (Exception ex) {
            }
        }
        //This make sure that after deleting the hidden files on the disk
        //the menubar will be refreshed.
//        createMenuItem("dummy");
//        hideMenuItem("dummy");
       FileUtil.getConfigRoot().refresh(true);
       FileUtil.getConfigRoot().refresh(false);
    }
    /*  public void moveToTemp(String name) {
    FileLock cfo2 = null;
    try {            
    FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Menu");
    //            FileObject temp = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Temp");
    FileObject temp = Repository.getDefault().getDefaultFileSystem().getRoot();            
    FileObject fo2 = fo.getFileObject(name);
    System.out.println("moveToTemp " + fo2.getName());
    //            FileObject fo3 = temp.getFileObject(name);            
    //FileUtil.moveFile(fo2, temp, fo2.getName());
    cfo2 = fo2.lock();
    fo2.move(cfo2, temp, fo2.getName(),"");
    } catch (Throwable ex) {
    Exceptions.printStackTrace(ex);
    } finally {
    if (cfo2 != null) {
    cfo2.releaseLock();
    }                        
    }
    }*/
    /*    public void moveToTemp(String name) {
    FileLock cfo2 = null;
    try {            
    FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Menu");
    //            FileObject temp = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Temp");
    FileObject temp = Repository.getDefault().getDefaultFileSystem().getRoot();            
    FileObject fo2 = fo.getFileObject(name);
    System.out.println("moveToTemp " + fo2.getName());
    //            FileObject fo3 = temp.getFileObject(name);            
    //FileUtil.moveFile(fo2, temp, fo2.getName());
    
    fo2.copy(temp, fo2.getName(),"" );
    
    //            fo2.move(cfo2, temp, fo2.getName(),"");
    } catch (Throwable ex) {
    Exceptions.printStackTrace(ex);
    } finally {
    }
    }*/
    /*        private void  interessant() {
    
    FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Menu");
    //          DataFolder folder = DataFolder.findFolder(fo);
    FileObject fo2 = fo.getFileObject("File");
    DataFolder folder = DataFolder.findFolder(fo2);
    
    
    org.openide.awt.MenuBar bar = new org.openide.awt.MenuBar(folder);
    bar.setVisible(false);
    
    }    */
    /*    private void  interessant() {
    try {
    
    FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("Menu");
    //            FileObject fo2 = fo.getFileObject("File");
    DataFolder folder = DataFolder.findFolder(fo2);
    //            folder.getPrimaryFile().delete();
    folder.getPrimaryFile().rename(fo.lock(), "File", "File_hidden");
    
    //            DataFolder folder = DataFolder.findFolder(fo);            
    //            folder.getPrimaryFile().createFolder("File");
    
    //                    Node node = folder.getNodeDelegate();
    //            node.setHidden(true);
    } catch (IOException ex) {
    Exceptions.printStackTrace(ex);
    }
    //        Node node = folder.getNodeDelegate();
    //        node.setHidden(true);
    
    }    */
}

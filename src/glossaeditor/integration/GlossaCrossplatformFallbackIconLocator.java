/*
 *  Copyright 2009 Georgios "cyberpython" Migdos <cyberpython@gmail.com>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package glossaeditor.integration;

import glossaeditor.Slang;
import glossaeditor.integration.iconlocator.FallbackIconLocator;
import glossaeditor.integration.iconlocator.IconLocator;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;



/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class GlossaCrossplatformFallbackIconLocator implements IconLocator, FallbackIconLocator {

    private FallbackIconLocator fbil;
    private Vector<String> availableIcons;

    /**
     * Deafult constructor - Creates a DefaultIconLocator without a
     * FallbackIconLocator
     */
    public GlossaCrossplatformFallbackIconLocator(){
        this.fbil = null;
        initAvailableIcons();
    }

    private void initAvailableIcons(){
        this.availableIcons = new Vector<String>();
        this.availableIcons.add("document:24");
        this.availableIcons.add("folder:24");
        this.availableIcons.add("go-home:24");
        this.availableIcons.add("go-up:24");
        this.availableIcons.add("view-refresh:24");
        this.availableIcons.add("locked:24");
        this.availableIcons.add("computer:24");

        this.availableIcons.add("document-new:24");
        this.availableIcons.add("document-open:24");
        this.availableIcons.add("document-save:24");
        this.availableIcons.add("document-print:24");

        this.availableIcons.add("edit-undo:24");
        this.availableIcons.add("edit-redo:24");
        this.availableIcons.add("edit-cut:24");
        this.availableIcons.add("edit-copy:24");
        this.availableIcons.add("edit-paste:24");
        this.availableIcons.add("edit-find:24");
        this.availableIcons.add("edit-find-replace:24");

        this.availableIcons.add("help-browser:24");

        this.availableIcons.add("compile:24");
        this.availableIcons.add("run:24");

        this.availableIcons.add("window-close:24");

        this.availableIcons.add("document-new:16");
        this.availableIcons.add("document-open:16");
        this.availableIcons.add("document-save:16");
        this.availableIcons.add("document-save-as:16");
        this.availableIcons.add("document-print:16");

        this.availableIcons.add("edit-undo:16");
        this.availableIcons.add("edit-redo:16");
        this.availableIcons.add("edit-cut:16");
        this.availableIcons.add("edit-copy:16");
        this.availableIcons.add("edit-paste:16");
        this.availableIcons.add("edit-delete:16");
        this.availableIcons.add("edit-select-all:16");
        this.availableIcons.add("edit-find:16");
        this.availableIcons.add("edit-find-replace:16");

        this.availableIcons.add("compile:16");
        this.availableIcons.add("run:16");

        this.availableIcons.add("help-browser:16");
        this.availableIcons.add("help-about:16");

        this.availableIcons.add("application-exit:16");
    }

    /**
     * Set's the FallbackIconLocator. A FallbackIconLocator is normally used to provide icons when
     * the IconLocator fails to find the requested icon, but in the case of DefaultIconLocator
     * the FallbackIconLocator is always used (no search for icons is performed).<br>
     * Can be null.
     * @param fbil The FallbackIconLocator to be used.
     */
    public void setFallbackIconLocator(FallbackIconLocator fbil){
        this.fbil = fbil;
    }

    public URL findIcon(String iconName, int size){

        String iconEntry = iconName +":"+ String.valueOf(size);        
        if(this.availableIcons.contains(iconEntry)){            
            String path = "/artwork/icons/png/crossplatform/s" + String.valueOf(size) + "x" + String.valueOf(size) + "/" + iconName + ".png";
            java.net.URL iconURL = Slang.class.getResource(path);
            return iconURL;
        }

        if(fbil!=null){
            return fbil.LookupFallbackIcon(iconName, size);
        }

        return null;
    }

    public URL LookupFallbackIcon (String iconname, int size){
        return findIcon(iconname, size);
    }

    public Hashtable<String, ImageIcon> getAvailableIconsHashTable(){
        Hashtable<String, ImageIcon> result = new Hashtable<String, ImageIcon>();
        for(int i=0; i<this.availableIcons.size(); i++){
            String iconEntry = this.availableIcons.get(i);
            int sepIndex = iconEntry.lastIndexOf(":");
            String iconName = iconEntry.substring(0, sepIndex);
            int iconSize = Integer.valueOf(iconEntry.substring(sepIndex+1));
            URL iconURL = findIcon(iconName, iconSize);
            if(iconURL!=null){
                ImageIcon value = new ImageIcon(iconURL);
                String key = new String(iconName+":"+String.valueOf(iconSize));
                result.put(key, value);
            }
        }

        return result;

    }

}

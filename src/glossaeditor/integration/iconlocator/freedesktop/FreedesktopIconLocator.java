/*
 *  Copyright 2010 Georgios Migdos <cyberpython@gmail.com>.
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
package glossaeditor.integration.iconlocator.freedesktop;

import glossaeditor.integration.freedesktop.DesktopEnvironmentInfo;
import glossaeditor.integration.iconlocator.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class FreedesktopIconLocator implements IconLocator {

    private FallbackIconLocator fbil;
    //TODO: FIXME
    private Vector<File> baseDirs;
    private IconThemeDescription themeIndex;
    private String[] extensions;
    private IconThemeLister iconThemes;    

    public FreedesktopIconLocator(DesktopEnvironmentInfo deInfo) {
        this.fbil = null;

        this.baseDirs = getBaseDirs();

        this.extensions = new String[2];
        extensions[0] = "png";
        extensions[1] = "svg";

        this.iconThemes = new IconThemeLister(baseDirs);

        this.themeIndex = getCurrentIconTheme(deInfo);

    }

    private IconThemeDescription getCurrentIconTheme(DesktopEnvironmentInfo deInfo) {

        IconThemeDescription result = null;
                
        String themeName = deInfo.getIconThemeName();
        
        
        if (themeName == null) {
            return null;
        }
        
        if(themeName.toLowerCase().equals("unknown")){
            return null;
        }        


        File themeFile = iconThemes.getThemes().get(themeName);

        if (themeFile != null) {
            //System.out.println(themeFile);
            result = new IconThemeDescription(themeFile);
        }

        return result;

    }

    private Vector<File> getBaseDirs() {
        Vector<File> dirs = new Vector<File>();
        
        File userIconsDir = new File(System.getProperty("user.home") + File.separator + ".icons");
        File kde4UserIconsDir1 = new File(System.getProperty("user.home") + File.separator + ".kde" + File.separator + "share" + File.separator + "icons");
        File kde4UserIconsDir2 = new File(System.getProperty("user.home") + File.separator + ".kde4" + File.separator + "share" + File.separator + "icons");
        
        dirs.add(userIconsDir);
        dirs.add(kde4UserIconsDir1);
        dirs.add(kde4UserIconsDir2);
        
        String XDG_DATA_DIRS = System.getenv("XDG_DATA_DIRS");
        String[] systemDirs;
        
        if(XDG_DATA_DIRS==null){
            systemDirs = new String[2];
            systemDirs[0] = "/usr/local/share";
            systemDirs[1] = "/usr/share";
        }
        else if(XDG_DATA_DIRS.trim().equals("")){
            systemDirs = new String[2];
            systemDirs[0] = "/usr/local/share";
            systemDirs[1] = "/usr/share";
        }
        else{
            systemDirs = XDG_DATA_DIRS.split(":");
        }
        

        for (int i = 0; i < systemDirs.length; i++) {
            File sharedIconsDir = new File(systemDirs[i] + File.separator + "icons");
            dirs.add(sharedIconsDir);
        }

        File pixmapsDir = new File("/usr/share/pixmaps");
        dirs.add(pixmapsDir);

        return dirs;
    }
    
    public void setFallbackIconLocator(FallbackIconLocator fbil){
        this.fbil = fbil;
    }

    public URL findIcon(String iconName, int size) {

        if (System.getProperty("os.name").toLowerCase().equals("linux")) {            
            if (themeIndex != null) {                
                File icon = FindIconHelper(iconName, size, themeIndex);
                if (icon != null) {
                    try{
                        URL url = new URL("file://"+icon.getAbsolutePath());                        
                        return url;
                    }
                    catch(MalformedURLException urle){
                        return null;
                    }
                }                
            }
        }

        if (fbil != null) {
            return fbil.LookupFallbackIcon(iconName, size);
        }

        return null;

    }

    private File LookupClosestIcon(String iconname, int size, IconThemeDescription theme) {

        String[] subdirList = theme.getSubDirsList();
        String filename = "";

        int minimal_size = Integer.MAX_VALUE;
        File closestFile = null;

        for (int i = 0; i < subdirList.length; i++) { //for each subdir in $(theme subdir list)

            for (int j = 0; j < baseDirs.size(); j++) {  //for each directory in $(basename list)

                for (int k = 0; k < extensions.length; k++) {

                    filename = baseDirs.get(j) + File.separator + theme.getInternalName() + File.separator + subdirList[i] + File.separator + iconname + "." + extensions[k];

                    File f = new File(filename);
                    int sizeDistance = theme.DirectorySizeDistance(subdirList[i], size);
                    if (f.exists() && (sizeDistance < minimal_size)) {
                        closestFile = f;
                        minimal_size = sizeDistance;
                    }


                }

            }

        }

        if (closestFile != null) {
            return closestFile;
        }

        return null;

    }

    private File LookupIcon(String iconname, int size, IconThemeDescription theme) {

        String[] subdirList = theme.getSubDirsList();
        String filename = "";

        for (int i = 0; i < subdirList.length; i++) {            

            for (int j = 0; j < baseDirs.size(); j++) {                

                for (int k = 0; k < extensions.length; k++) {

                    if (theme.DirectoryMatchesSize(subdirList[i], size)) {
                        filename = baseDirs.get(j) + File.separator + theme.getInternalName() + File.separator + subdirList[i] + File.separator + iconname + "." + extensions[k];
                        File f = new File(filename);                        
                        if (f.exists()) {
                            return f;
                        }
                    }
                }

            }

        }
        
        
        
        int minimal_size = Integer.MAX_VALUE;
        File closestFile = null;

        for (int i = 0; i < subdirList.length; i++) { //for each subdir in $(theme subdir list)

            for (int j = 0; j < baseDirs.size(); j++) {  //for each directory in $(basename list)

                for (int k = 0; k < extensions.length; k++) {

                    filename = baseDirs.get(j) + File.separator + theme.getInternalName() + File.separator + subdirList[i] + File.separator + iconname + "." + extensions[k];

                    File f = new File(filename);                    
                    int sizeDistance = theme.DirectorySizeDistance(subdirList[i], size);                                        
                    if (f.exists() && (sizeDistance < minimal_size)) {
                        closestFile = f;
                        minimal_size = sizeDistance;
                    }


                }

            }

        }

        if (closestFile != null) {            
            return closestFile;
        }
        

        return null;

    }

    private File FindIconHelper(String iconname, int size, IconThemeDescription theme) {

        File f = LookupIcon(iconname, size, theme);

        if (f != null) {
            return f;
        }
        
        if (theme.getName().toLowerCase().equals("hicolor")) {
            return null;
        }

        String[] parents = theme.getParentsList();
        if (parents.length <= 0) {            
            IconThemeDescription parentTheme = new IconThemeDescription(new File("/usr/share/icons/hicolor/index.theme"));
            f = FindIconHelper(iconname, size, parentTheme);

            if (f != null) {
                return f;
            }

        } else {

            String parent;
            for (int i = 0; i < parents.length; i++) {

                parent = parents[i];
                File themeFile = iconThemes.getThemes().get(parent);

                if (themeFile != null) {
                    IconThemeDescription parentTheme = new IconThemeDescription(themeFile);

                    f = FindIconHelper(iconname, size, parentTheme);

                    if (f != null) {
                        return f;
                    }

                }

            }
        }
        return null;
    }
    
    
    private void printDirs(Vector<File> dirs) {

        for (int i = 0; i < dirs.size(); i++) {
            File d = dirs.get(i);
            if (d != null) {
                System.out.println(d.getAbsolutePath());
            }
        }

    }

   
}
        
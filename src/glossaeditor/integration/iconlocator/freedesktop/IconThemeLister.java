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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IconThemeLister {

    private HashMap<String, File> themes;

    public IconThemeLister(List<File> baseDirs) {

        themes = new HashMap<String, File>();

        listAllIndexFiles(baseDirs);
    }

    public HashMap<String, File> getThemes() {
        return this.themes;
    }

    private List<File> listFiles(File directory, FilenameFilter filter) {
                
        if(directory==null){
            return null;
        }
        
        if(directory.isDirectory()){
            
            File[] dirsToCheck = directory.listFiles();
            
            if(dirsToCheck!=null){
                
                List<File> themeFiles = new ArrayList<File>();
                
                for(int i=0; i<dirsToCheck.length; i++){
                    if(dirsToCheck[i].isDirectory()){
                        File[] themefiles = dirsToCheck[i].listFiles(filter);
                        
                        themeFiles.addAll(Arrays.asList(themefiles));
                        
                    }
                }
                return themeFiles;
                
            }
            
        }
        
        return null;
        
        
    }

      
    
    private void listAllIndexFiles(List<File> baseDirs) {

        FilenameFilter ff = new FilenameFilter() {

            public boolean accept(File arg0, String arg1) {
                if (arg1.toLowerCase().equals("index.theme")) {
                    return true;
                }
                return false;
            }
        };


        for (int i = 0; i < baseDirs.size(); i++) {
            List<File> files = listFiles(baseDirs.get(i), ff);
            
            if(files!=null){

                for (int j = 0; j < files.size(); j++) {                
                    themes.put(files.get(j).getParentFile().getName(), files.get(j));
                }
            }

        }

    }
    
    /*private static List<File> getBaseDirs() {
        List<File> dirs = new ArrayList<File>();

        File userIconsDir = new File(System.getProperty("user.home") + File.separator + ".icons");
        dirs.add(userIconsDir);

        String XDG_DATA_DIRS = System.getenv("XDG_DATA_DIRS");
        String[] systemDirs = XDG_DATA_DIRS.split(":");

        for (int i = 0; i < systemDirs.length; i++) {
            File sharedIconsDir = new File(systemDirs[i] + File.separator + "icons");
            dirs.add(sharedIconsDir);
        }

        File pixmapsDir = new File("/usr/share/pixmaps");
        dirs.add(pixmapsDir);

        return dirs;
    }
    
    
    private void printThemes(){
        Iterator<File> iter = themes.values().iterator();
        while(iter.hasNext()){
            System.out.println(iter.next().getAbsolutePath() );
        }
    }*/
    
}
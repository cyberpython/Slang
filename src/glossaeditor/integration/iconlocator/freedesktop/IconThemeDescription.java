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
import java.util.Arrays;
import java.util.HashMap;

public class IconThemeDescription {
    
    public final int FIXED = 0;
    public final int SCALABLE = 1;
    public final int THRESHOLD = 2;
    
    

    private class SubDirPropertiesItem {

        int type;
        int size;
        int minSize;
        int maxSize;
        int threshold;

        public SubDirPropertiesItem(int type, int size, int minSize, int maxSize, int threshold) {
            this.type = type;
            this.size = size;
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.threshold = threshold;
        }
    }
    
    private String[] subdirList;
    private String[] parentsList;
    private File rootDir;
    private String name;
    private String internalName;
    private HashMap<String, SubDirPropertiesItem> subdirsProps;
    private String filename;
    
        

    public IconThemeDescription(File f) {

        filename = f.getAbsolutePath();

        rootDir = new File(filename).getParentFile();
        
        internalName = rootDir.getName();

        subdirsProps = new HashMap<String, SubDirPropertiesItem>();


        try {
            IconThemeIndex indexfile = new IconThemeIndex();
            indexfile.load(filename);

            name = indexfile.getValue("Icon Theme", "Name");


            String subdirs = indexfile.getValue("Icon Theme", "Directories");
            if (subdirs != null) {
                subdirList = subdirs.split(",");
                Arrays.sort(subdirList);
            } else {
                subdirList = new String[0];
            }

            String parents = indexfile.getValue("Icon Theme", "Inherits");
            if (parents != null) {
                parentsList = parents.split(",");
                Arrays.sort(parentsList);
            } else {
                parentsList = new String[0];
            }



            for (int i = 0; i < subdirList.length; i++) {

                int size = getSize(indexfile, subdirList[i]);

                SubDirPropertiesItem item = new SubDirPropertiesItem(
                        getType(indexfile, subdirList[i]),
                        size,
                        getMinSize(indexfile, subdirList[i], size),
                        getMaxSize(indexfile, subdirList[i], size),
                        getThreshold(indexfile, subdirList[i]));

                subdirsProps.put(subdirList[i], item);

            }


        } catch (Exception e) {
            //TODO: remove printStackTrace
            e.printStackTrace();
        }


    }
    
    
    
    
    public String[] getSubDirsList(){
        return this.subdirList;
    }
    
    public String[] getParentsList(){
        return this.parentsList;
    }
    
    public File getRootDir(){
        return this.rootDir;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getInternalName(){
        return this.internalName;
    }
    
    public  String getFilename(){
        return this.filename;
    }
    
    
    
    

    private int getType(IconThemeIndex indexfile, String subdir) {



        String type = indexfile.getValue(subdir, "Type");

        if (type == null) {
            return THRESHOLD;
        }

        type = type.toLowerCase();

        if (type.equals("fixed")) {
            return FIXED;
        } else if (type.equals("scalable")) {
            return SCALABLE;
        } else {
            return THRESHOLD;
        }


    }

    private int getSize(IconThemeIndex indexfile, String subdir) {


        String size = indexfile.getValue(subdir, "Size");

        if (size == null) {
            return -1;
        }

        return Integer.parseInt(size);


    }

    private int getMinSize(IconThemeIndex indexfile, String subdir, int size) {


        String minSize = indexfile.getValue(subdir, "MinSize");

        if (minSize == null) {
            return size;
        }

        return Integer.parseInt(minSize);


    }

    private int getMaxSize(IconThemeIndex indexfile, String subdir, int size) {


        String maxSize = indexfile.getValue(subdir, "MaxSize");

        if (maxSize == null) {
            return size;
        }

        return Integer.parseInt(maxSize);


    }

    private int getThreshold(IconThemeIndex indexfile, String subdir) {


        String threshold = indexfile.getValue(subdir, "Threshold");

        if (threshold == null) {
            return 2;
        }

        return Integer.parseInt(threshold);


    }

    public boolean DirectoryMatchesSize(String subdir, int iconsize) {



        if (Arrays.binarySearch(subdirList, subdir) < 0) {
            return false;
        }

        SubDirPropertiesItem item = subdirsProps.get(subdir);

        int type = item.type;
        int size = item.size;
        int minSize = item.minSize;
        int maxSize = item.maxSize;
        int threshold = item.threshold;


        if (type == FIXED) {
            return (size == iconsize);
        } else if (type == SCALABLE) {
            return ((minSize <= iconsize) && (iconsize <= maxSize));
        } else if (type == THRESHOLD) {
            return (((size - threshold) <= iconsize) && (iconsize <= (size + threshold)));
        } else {
            return false;
        }
    }

    public int DirectorySizeDistance(String subdir, int iconsize) {

        if (Arrays.binarySearch(subdirList, subdir) < 0) {
            return -1;
        }

        SubDirPropertiesItem item = subdirsProps.get(subdir);

        int type = item.type;
        int size = item.size;
        int minSize = item.minSize;
        int maxSize = item.maxSize;
        int threshold = item.threshold;

        if (type == FIXED) {
            return Math.abs(size - iconsize);
        } else if (type == SCALABLE) {
            if (iconsize < minSize) {
                return minSize - iconsize;
            } else if (iconsize > maxSize) {
                return iconsize - maxSize;
            } else {
                return 0;
            }
        } else if (type == THRESHOLD) {
            if (iconsize < (size - threshold)) {
                return minSize - iconsize;
            } else if (iconsize > (size + threshold)) {
                return iconsize - maxSize;
            } else {
                return 0;
            }
        } else {
            return 0;
        }

    }

}
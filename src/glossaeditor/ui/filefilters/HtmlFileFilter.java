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

package glossaeditor.ui.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class HtmlFileFilter extends FileFilter {
    public boolean accept(File f){
        if(f.isDirectory()){
            return true;
        }
        String name = f.getName();
        String extension = (name.substring((name.lastIndexOf('.')+1), name.length())).toLowerCase();
        if( extension.equals("htm") || extension.equals("html")){
            return true;
        }
        return false;
    }

    public String getDescription(){
        return "Αρχεία HTML";
    }

    @Override
    public String toString(){
        return "Αρχεία HTML";
    }
}

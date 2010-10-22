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
package glossaeditor.integration.iconlocator;

/**
 * Just a utility class to be used as keys for the IconManager's hashtable.
 * @author cyberpython
 */
public class IconSearchKey{ 
    private String name;
    private int size;    
    
    
    public IconSearchKey(String name, int size){
        this.name = name;
        this.size = size;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getSize(){
        return this.size;
    }
    
    @Override
    public boolean equals(Object arg0){
        
        if (arg0 instanceof IconSearchKey){
            IconSearchKey obj = (IconSearchKey) arg0;
            
            if(obj.getName().equals(this.name)    && (obj.getSize()==this.size) ){
                return true;
            }
            
        }                
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + this.size;
        return hash;
    }
    
    
    
    
}
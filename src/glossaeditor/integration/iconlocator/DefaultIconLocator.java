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

import java.net.URL;

/**
 * DefaultIconLocator is a simple class implementing the IconLocator interface
 * but it returns always the icon of the supplied FallbackIconLocator (no search for icons is performed).
 * If no FallbackIconLocator is supplied then the findIcon method always return's null.
 * @author cyberpython
 */
public class DefaultIconLocator implements IconLocator{
    
    private FallbackIconLocator fbil;
    
    /**
     * Deafult constructor - Creates a DefaultIconLocator without a 
     * FallbackIconLocator
     */
    public DefaultIconLocator(){
        this.fbil = null;
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
    
    /**
     * Return's the File specifying an icon (described by iconName and iconSize) returned by the FallbackIconLocator.
     * If no FallbackIconLocator is set, then this method always returns null.
     * @param iconName The name of the icon to search for.
     * @param size The size of the icon to search for.
     * @return A File object specifying an icon file that fits the search criteria or null if no suitable icon was found.
     */
    public URL findIcon(String iconName, int size){
        if(fbil!=null){
            return fbil.LookupFallbackIcon(iconName, size);
        }
        
        return null;
    }
    
}
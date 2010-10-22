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
 * FallbackIconLocator implementing classes should provide
 * fallback icons for IconLoaders to be used in case the IconLoader could not 
 * find a suitable icon.
 * @author cyberpython
 */
public interface FallbackIconLocator{
    
    /**
     * 
     * @param iconname The name of the icon to be returned.
     * @param size The size of the icon to be returned
     * @return A File denoting a suitable icon or null.
     */
    public URL LookupFallbackIcon (String iconname, int size);
    
}
/*
 *  Copyright 2010 cyberpython.
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

package glossaeditor.ui.components.charsetselectpane;

import java.nio.charset.Charset;

/**
 *
 * @author Migdos Georgios <cyberpython@gmail.com>
 */
public class CharsetWrapper {

    private Charset charset;
    private String name;

    public CharsetWrapper(Charset c){
        this.charset = c;
        if(c.equals(Charset.forName("UTF-8"))){
            this.name = "UTF-8";
        }else if(c.equals(Charset.forName("windows-1253"))){
            this.name = "Windows 1253 (Greek)";
        }else{
            this.name = charset.displayName();
        }
    }

    @Override
    public String toString(){
        return this.name;

    }

    public Charset getCharset(){
        return this.charset;
    }

}

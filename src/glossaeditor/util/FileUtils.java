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

package glossaeditor.util;

import java.io.File;
import java.nio.charset.Charset;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class FileUtils {
    /***
     *
     * @param f
     * @return The name of the file without the path or the extension
     */
    public static String stripExtension(File f){
        String result = f.getName();
        int endIndex = result.lastIndexOf('.');
        result = result.substring(0, endIndex);
        if(endIndex!=-1){
            result = result.substring(0, endIndex);
        }
        return result;
    }

    /***
     *
     * @param filename
     * @return The filename without extension
     */
    public static String stripExtension(String filename){
        String result = filename;
        int endIndex = result.lastIndexOf('.');
        if(endIndex!=-1){
            result = result.substring(0, endIndex);
        }
        return result;
    }


    public static Charset detectCharset(File f) {
        if (f != null) {
            CharsetDetector cd = new CharsetDetector();
            String[] charsetsToBeTested = {"UTF-8", "windows-1253"};
            Charset charset = cd.detectCharset(f, charsetsToBeTested);
            if (charset == null) {
                if (Charset.isSupported("UTF-8")) {
                    return Charset.forName("UTF-8");
                } else {
                    return Charset.defaultCharset();
                }
            } else {
                return charset;
            }
        }
        return Charset.defaultCharset();
    }

}

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

import java.io.PrintStream;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class SystemInfo {

    private String osName;
    private String osVersion;

    public SystemInfo(){
        this.osName = System.getProperty("os.name").toLowerCase();
        this.osVersion = System.getProperty("os.version").toLowerCase();
    }

    public void setOSName(String osName){
        this.osName = osName;
    }

    public void setOSVersion(String osVersion){
        this.osVersion = osVersion;
    }

    public String getOSName(){
        return this.osName;
    }

    public String getOSVersion(){
        return this.osVersion;
    }

    public void printSystemInfo(PrintStream out){
        out.println("Operating system name: "+this.osName);
        out.println("Operating system version: "+this.osVersion);
    }

    public static void main(String[] args){
        SystemInfo inf = new SystemInfo();
        inf.printSystemInfo(System.out);
    }

}

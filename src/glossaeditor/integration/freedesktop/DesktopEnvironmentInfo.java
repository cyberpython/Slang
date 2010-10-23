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
package glossaeditor.integration.freedesktop;

import glossaeditor.integration.SystemInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DesktopEnvironmentInfo {

    private final String UNIX_PRINT_PROC_CMD = "ps -A";
    private String desktopName;
    private String windowManager;
    private String widgetTheme;
    private String iconTheme;
    private HashMap<String, String> wmList;
    private HashMap<String, String> deList;

    /**
     * Default constructor
     */
    public DesktopEnvironmentInfo(String osName) {
        osName = osName.toLowerCase();
        if (osName.equals("linux")) {
            initialize();
            List<String> runningProcesses = listRunningProcessesOnUnix();
            this.desktopName = findDesktopName(runningProcesses);
            this.windowManager = findWMName(runningProcesses);
            findThemeAndIconTheme(this.desktopName);
        } else if (osName.contains("windows")) {
            this.desktopName = "Windows";
            this.windowManager = "Windows WM";
            this.iconTheme = "Not available";
            this.widgetTheme = "Not available";
        } else if (osName.equals("mac")) {
            this.desktopName = "Mac";
            this.windowManager = "Mac WM";
            this.iconTheme = "Not available";
            this.widgetTheme = "Not available";
        } else {
            this.desktopName = "unknown";
            this.windowManager = "unknown";
            this.iconTheme = "Not available";
            this.widgetTheme = "Not available";
        }
    }

    public final void refreshThemeAndIconThemeName(String osName){
        findThemeAndIconTheme(this.desktopName);
    }

    /**
     * 
     * @return The name of the current Desktop Environment : <br>
     *          <ul>
     *          <b>gnome</b> - for GNOME
     *          <b>kde</b> - for KDE
     *          <b>xfce4</b> - for XFCE
     *          <b>unknown</b> - for other desktop environments
     *          </ul>
     */
    public String getDesktopEnvironmentName() {
        return this.desktopName;
    }

    /**
     * 
     * @return The name of the current Window Manager : <br>
     *          <ul>
     *          <b>beryl</b> - for Beryl
     *          <b>compiz</b> - for Compiz
     *          <b>emerald</b> - for Emerald
     *          <b>fluxbox</b> - for Fluxbox
     *          <b>openbox</b> - for Openbox
     *          <b>blackbox</b> - for Blackbox
     *          <b>xfwm</b> - for Xfwm
     *          <b>metacity</b> - for Metacity
     *          <b>kwin</b> - for Kwin
     *          <b>fvwm</b> - for FVWM
     *          <b>enlightenment</b> - for Enlightenment
     *          <b>icewm</b> - for IceWM
     *          <b>wmaker</b> - for Window Maker
     *          <b>pekwm</b> - for PekWM
     *          <b>unknown</b> - for other Window Managers
     *          </ul>
     */
    public String getWindowManagerName() {
        return this.windowManager;
    }

    /**
     * 
     * @return The name of the current icon widgetTheme.<br>
     *         If the dekstop environment is not Gnome/KDE/XFCE or the 
     *         icon theme could not be determined then "unknown" is returned.<br>
     *          ***WARNING*** : On KDE if the user has not changed the default
     *          icon theme then the result will be "default.kde".
     */
    public String getIconThemeName() {
        return this.iconTheme;
    }

    /**
     * 
     * @return The name of the current widgetTheme.<br>
     *         If the dekstop environment is not Gnome/KDE/XFCE or the 
     *         widget theme could not be determined then "unknown" is returned.<br>
     *          ***WARNING*** : On KDE if the user has not changed the default
     *          widget theme then the result will be "default".
     */
    public String getWidgetThemeName() {
        return this.widgetTheme;
    }

    private void initialize() {

        this.wmList = new HashMap<String, String>();
        this.wmList.put("beryl", "Beryl");
        this.wmList.put("compiz", "Compiz");
        this.wmList.put("emerald", "Emerald");
        this.wmList.put("fluxbox", "Fluxbox");
        this.wmList.put("openbox", "Openbox");
        this.wmList.put("blackbox", "Blackbox");
        this.wmList.put("xfwm4", "Xfwm4");
        this.wmList.put("metacity", "Metacity");
        this.wmList.put("kwin", "Kwin");
        this.wmList.put("fvwm", "FVWM");
        this.wmList.put("enlightenment", "Enlightenment");
        this.wmList.put("icewm", "IceWM");
        this.wmList.put("wmaker", "Window Maker");
        this.wmList.put("pekwm", "PekWM");


        this.deList = new HashMap<String, String>();
        this.deList.put("xfce-mcs-manage", "xfce4");
        this.deList.put("ksmserver", "kde");
    }

    /**
     * Determines the name of the current desktop environment.
     * For GNOME it uses the JAVA VM System property sun.desktop
     * For KDE and XFCE checks the running processes to find out
     * which DE is currently active.
     * @return The name of the current Desktop Environment : <br>
     *          <ul>
     *          <b>gnome</b> - for GNOME
     *          <b>kde</b> - for KDE
     *          <b>xfce4</b> - for XFCE
     *          <b>unknown</b> - for other desktop environments
     *          </ul>
     */
    private String findDesktopName(List<String> processes) {
        String name = System.getProperty("sun.desktop");
        if (name != null) {
            name = name.toLowerCase();
            if (name.equals("gnome")) {
                return "gnome";
            }
        }

        Iterator<String> iter = deList.keySet().iterator();

        while (iter.hasNext()) {
            String deProc = iter.next();
            String deName = deList.get(deProc);


            if (processes.contains(deProc)) {
                return deName;
            }
        }

        return "unknown";
    }

    /**
     * Determines the name of the current window manager by
     * checking the running processes.
     * @return The name of the current Window Manager : <br>
     *          <ul>
     *          <b>beryl</b> - for Beryl
     *          <b>compiz</b> - for Compiz
     *          <b>emerald</b> - for Emerald
     *          <b>fluxbox</b> - for Fluxbox
     *          <b>openbox</b> - for Openbox
     *          <b>blackbox</b> - for Blackbox
     *          <b>xfwm</b> - for Xfwm
     *          <b>metacity</b> - for Metacity
     *          <b>kwin</b> - for Kwin
     *          <b>fvwm</b> - for FVWM
     *          <b>enlightenment</b> - for Enlightenment
     *          <b>icewm</b> - for IceWM
     *          <b>wmaker</b> - for Window Maker
     *          <b>pekwm</b> - for PekWM
     *          <b>unknown</b> - for other Window Managers
     *          </ul>
     */
    private String findWMName(List<String> processes) {

        Iterator<String> iter = wmList.keySet().iterator();

        while (iter.hasNext()) {
            String wmProc = iter.next();
            String wmName = wmList.get(wmProc);


            if (processes.contains(wmProc)) {
                return wmName;
            }
        }

        return "unknown";

    }

    private void findThemeAndIconTheme(String deName) {

        this.widgetTheme = "unknown";
        this.iconTheme = "unknown";

        if (deName != null) {
            deName = deName.toLowerCase();

            if (deName.equals("gnome")) {

                final String GET_GTK_THEME_CMD = "gconftool-2 -g /desktop/gnome/interface/gtk_theme";
                List<String> cmdResults = execCommand(GET_GTK_THEME_CMD);
                if (cmdResults.size() > 0) {
                    this.widgetTheme = cmdResults.get(0);
                }

                final String GET_ICON_THEME_CMD = "gconftool-2 -g /desktop/gnome/interface/icon_theme";
                cmdResults = execCommand(GET_ICON_THEME_CMD);
                if (cmdResults.size() > 0) {
                    this.iconTheme = cmdResults.get(0);
                }

            }//Done with Gnome            
            else if (deName.equals("kde")) {//KDE4
                this.widgetTheme = "default";
                this.iconTheme = "default.kde4";
                String userHome = System.getProperty("user.home");
                if (userHome != null) {
                    File kdeglobals = new File(userHome + "/.kde4/share/config/kdeglobals");
                    if (kdeglobals.exists() == false) {
                        kdeglobals = new File(userHome + "/.kde/share/config/kdeglobals");
                    }

                    try {

                        BufferedReader br = new BufferedReader(new FileReader(kdeglobals));
                        String line;

                        boolean f1 = false;
                        boolean f2 = false;
                        boolean finished = false;
                        line = br.readLine();
                        while ((line != null) && (finished == false)) {
                            line = line.trim();
                            if (line != null) {
                                if (line.equals("[General]")) {
                                    boolean done = false;
                                    try {
                                        line = br.readLine();
                                        while ((line != null) && (done == false) && (!line.equals("[Icons]"))) {
                                            line = line.trim();
                                            if (line.startsWith("widgetStyle")) {
                                                int start = line.indexOf("=");
                                                this.widgetTheme = line.substring(start + 1);
                                                done = true;
                                            }
                                            line = br.readLine();
                                        }
                                    } catch (IOException ioe) {
                                        //TODO: remove printStackTrace
                                        ioe.printStackTrace();
                                        this.widgetTheme = "default";
                                    }
                                    f1 = true;
                                }
                                if (line.equals("[Icons]")) {
                                    boolean done = false;
                                    line = br.readLine();
                                    try {
                                        while ((line != null) && (done == false) && (!line.equals("[General]"))) {
                                            line = line.trim();
                                            if (line.startsWith("Theme")) {
                                                int start = line.indexOf("=");
                                                this.iconTheme = line.substring(start + 1);
                                                done = true;
                                            }
                                            line = br.readLine();
                                        }
                                    } catch (IOException ioe) {
                                        //TODO: remove printStackTrace
                                        ioe.printStackTrace();
                                        this.iconTheme = "default.kde4";
                                    }
                                    f2 = true;
                                }
                            }
                            finished = f1 && f2;
                            if (line == null) {
                                break;
                            }


                            line = br.readLine();
                        }


                    } catch (IOException ioe) {
                        //TODO: remove printStackTrace
                        ioe.printStackTrace();
                    }
                }
            }//Done with KDE4
            else if (deName.equals("xfce4")) {

                String userHome = System.getProperty("user.home");

                if (userHome != null) {
                    File xfceGTKSettings = new File(userHome + "/.config/xfce4/mcs_settings/gtk.xml");

                    try {

                        BufferedReader br = new BufferedReader(new FileReader(xfceGTKSettings));
                        String line;

                        boolean f1 = false;
                        boolean f2 = false;
                        boolean finished = false;

                        while (((line = br.readLine()) != null) && (finished == false)) {
                            line = line.trim();

                            if (line.startsWith("<option name=\"Net/ThemeName\"")) {
                                int start_index = line.lastIndexOf("=") + 2;
                                int end_index = line.lastIndexOf("\"");
                                this.widgetTheme = line.substring(start_index, end_index);
                                f1 = true;
                            }
                            if (line.startsWith("<option name=\"Net/IconThemeName\"")) {
                                int start_index = line.lastIndexOf("=") + 2;
                                int end_index = line.lastIndexOf("\"");
                                this.iconTheme = line.substring(start_index, end_index);
                                f2 = true;
                            }

                            finished = f1 && f2;
                            if (line == null) {
                                break;
                            }
                        }

                    } catch (Exception e) {
                    }

                }
            }//Done with XFCE4

        }

    }

    /**
     * Helper function to execute external processes
     * @param cmd The command to be executed
     * @return A Vector of strings representing the commands output
     */
    private List<String> execCommand(String cmd) {

        List<String> result = new ArrayList<String>();


        Runtime rt = Runtime.getRuntime();
        try {
            Process p = rt.exec(cmd);
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = new String();
            while ((line = input.readLine()) != null) {
                result.add(line);
            }
            input.close();
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }

        return result;
    }

    /**
     * Lists the running processes on a Unix machine by invoking the 'ps -A' command
     * @return A Vector of strings representing the names of the running processes
     */
    private List<String> listRunningProcessesOnUnix() {
        List<String> v = execCommand(UNIX_PRINT_PROC_CMD);

        List<String> result = new ArrayList<String>(v.size());
        Iterator<String> e = v.iterator();

        while (e.hasNext()) {
            String[] s = e.next().split("\\s");
            result.add(s[s.length - 1]);
        }

        return result;


    }

    public void printDesktopEnvrionmentInfo(PrintStream out) {
        out.println("Desktop Environment: " + this.getDesktopEnvironmentName());
        out.println("Window manager: " + this.getWindowManagerName());
        out.println("Widgets Theme: " + this.getWidgetThemeName());
        out.println("Icon theme: " + this.getIconThemeName());
    }

    public static void main(String[] args) {

        DesktopEnvironmentInfo de = new DesktopEnvironmentInfo(new SystemInfo().getOSName());
        de.printDesktopEnvrionmentInfo(System.out);

    }
}

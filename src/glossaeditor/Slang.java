/*
 * Copyright 2009 Georgios "cyberpython" Migdos cyberpython@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Slang.java
 */
package glossaeditor;

import glossaeditor.ui.GlossaEditorView;
import glossaeditor.ui.dialogs.JOpenOrInsertDialog;
import glossaeditor.ui.dialogs.JFindReplaceDialog;
import glossaeditor.ui.dialogs.JPreferencesDialog;
import glossaeditor.integration.SystemInfo;
import glossaeditor.integration.iconlocator.IconManager;
import glossaeditor.integration.GlossaCrossplatformFallbackIconLocator;
import glossaeditor.integration.GlossaEditorIconLoader;
import glossaeditor.preferences.ApplicationPreferences;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class Slang extends SingleFrameApplication{

    private String arg;
    private ApplicationPreferences appPrefs;
    private SystemInfo sysInfo;
    private GlossaEditorIconLoader iconLoader;

    private GlossaEditorView mainWindow;
    private JOpenOrInsertDialog ooid;
    private JFindReplaceDialog findReplaceDialog;
    private JPreferencesDialog prefsDlg;

    public void allIconsLoaded(){
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        long time1 = new java.util.Date().getTime();

        this.sysInfo = new SystemInfo();
        sysInfo.printSystemInfo(System.out);


        this.appPrefs = new ApplicationPreferences();

        IconManager iconManager = new IconManager(this.sysInfo, new GlossaCrossplatformFallbackIconLocator());

        this.iconLoader = new GlossaEditorIconLoader(iconManager, appPrefs);

        createWindowAndDialogs();

        iconLoader.loadIcons();

        show(mainWindow);
        long time2 = new java.util.Date().getTime();
        System.out.println("Time: " + (time2 - time1));
    }

    public SystemInfo getSystemInfo(){
        return this.sysInfo;
    }

    public ApplicationPreferences getAppPreferences(){
        return this.appPrefs;
    }

    public GlossaEditorIconLoader getIconLoader(){
        return this.iconLoader;
    }

    public void restartApp(){
        if(Slang.getApplication().closeAllWindows()){
            String[] args  = new String[0];
            Slang.launch(Slang.class, args);
        }
    }

    public boolean closeAllWindows(){

        if(mainWindow.queryCloseApp()){
            this.mainWindow.getFrame().setVisible(false);
            this.mainWindow.getFrame().dispose();
            this.ooid.setVisible(false);
            this.ooid.dispose();
            this.findReplaceDialog.setVisible(false);
            this.findReplaceDialog.dispose();
            this.prefsDlg.setVisible(false);
            this.prefsDlg.dispose();
            return true;
        }
        return false;
    }

    public void createWindowAndDialogs(){
        mainWindow = new GlossaEditorView(this, this.arg, this.appPrefs);


        ooid = new JOpenOrInsertDialog(mainWindow.getFrame());
        mainWindow.setOpenOrInsertDialog(ooid);

        findReplaceDialog = new JFindReplaceDialog(mainWindow.getFrame(), false);
        mainWindow.setFindReplaceDialog(findReplaceDialog);

        prefsDlg = new JPreferencesDialog(mainWindow.getFrame(), this.appPrefs);
        mainWindow.setPreferencesDialog(prefsDlg);

        this.appPrefs.addListener(mainWindow);
        this.appPrefs.addListener(prefsDlg);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    @Override
    protected void initialize(String[] arg0) {
        if (arg0.length > 0) {
            this.arg = arg0[0];
        } else {
            this.arg = null;
        }
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Slang
     */
    public static Slang getApplication() {
        return Application.getInstance(Slang.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(Slang.class, args);
    }
}

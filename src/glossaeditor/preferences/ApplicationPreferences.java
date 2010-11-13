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
package glossaeditor.preferences;

import glossaeditor.Slang;
import glossaeditor.integration.SystemInfo;
import glossaeditor.util.MiscUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class ApplicationPreferences {

    private final String PROFILES_MANAGER_CHANGED_EVT_NAME = "profilesManagerChanged";
    private final String HIGHLIGHTER_PROFILE_CHANGED_EVT_NAME = "highlighterProfileChanged";
    private final String HIGHLIGHTER_PROFILE_COLORS_CHANGED_EVT_NAME = "highlighterProfileColorsChanged";
    private final String EDITOR_FONT_CHANGED_EVT_NAME = "editorFontChanged";
    private HighlighterProfilesManager profilesManager;
    private HighlighterProfile highlighterProfile;
    private Font editorFont;
    private HashSet<ApplicationPreferencesListener> listeners;

    public ApplicationPreferences() {

        this.editorFont = this.loadDefaultFont();

        this.highlighterProfile = new HighlighterProfile();
        this.highlighterProfile.setTitle(this.highlighterProfile.CURRENT_TITLE);

        this.profilesManager = new HighlighterProfilesManager(this.highlighterProfile);

        this.listeners = new HashSet<ApplicationPreferencesListener>();


        this.loadPreferences();

        this.initLAF();

    }

    public void setProfilesManager(HighlighterProfilesManager profilesManager) {
        this.profilesManager = profilesManager;
        this.notifyListeners(this.PROFILES_MANAGER_CHANGED_EVT_NAME);
    }

    public HighlighterProfilesManager getProfilesManager() {
        return this.profilesManager;
    }

    public void setHighlighterProfile(HighlighterProfile profile) {
        this.highlighterProfile = profile;
        this.notifyListeners(this.HIGHLIGHTER_PROFILE_CHANGED_EVT_NAME);
    }

    public HighlighterProfile getHighlighterProfile() {
        return this.highlighterProfile;
    }

    public void setHighlighterProfileColors(String[] colors) {
        this.highlighterProfile.setColors(colors);
        Preferences pref = Preferences.userNodeForPackage(Slang.class);
        String[] editorColors = this.highlighterProfile.getColorsAsStrings();
        pref.put("KeywordsColor", editorColors[0]);
        pref.put("NumbersColor", editorColors[1]);
        pref.put("StringsColor", editorColors[2]);
        pref.put("OperatorsColor", editorColors[3]);
        pref.put("CommentsColor", editorColors[4]);
        pref.put("TypesColor", editorColors[5]);
        pref.put("IdentifiersColor", editorColors[6]);
        pref.put("DefaultColor", editorColors[7]);
        pref.put("BgColor", editorColors[8]);
        this.notifyListeners(this.HIGHLIGHTER_PROFILE_COLORS_CHANGED_EVT_NAME);
    }

    public void setHighlighterProfileColors(Color[] colors) {
        this.highlighterProfile.setColors(colors);
        Preferences pref = Preferences.userNodeForPackage(Slang.class);
        String[] editorColors = this.highlighterProfile.getColorsAsStrings();
        pref.put("KeywordsColor", editorColors[0]);
        pref.put("NumbersColor", editorColors[1]);
        pref.put("StringsColor", editorColors[2]);
        pref.put("OperatorsColor", editorColors[3]);
        pref.put("CommentsColor", editorColors[4]);
        pref.put("TypesColor", editorColors[5]);
        pref.put("IdentifiersColor", editorColors[6]);
        pref.put("DefaultColor", editorColors[7]);
        pref.put("BgColor", editorColors[8]);
        this.notifyListeners(this.HIGHLIGHTER_PROFILE_COLORS_CHANGED_EVT_NAME);
    }

    public void setEditorFont(Font f) {
        this.editorFont = f;
        Preferences pref = Preferences.userNodeForPackage(Slang.class);
        pref.put("EditorFontFamily", f.getFamily());
        pref.putInt("EditorFontSize", f.getSize());
        this.notifyListeners(this.EDITOR_FONT_CHANGED_EVT_NAME);
    }

    public Font getEditorFont() {
        return this.editorFont;
    }

    public final void loadPreferences() {
        loadEditorFont();
        loadEditorColors();
    }

    private void loadEditorFont() {

        Font defaultFont = loadDefaultFont();
        Preferences pref = Preferences.userNodeForPackage(Slang.class);
        String fontFamily = pref.get("EditorFontFamily", defaultFont.getFamily());
        int fontSize = pref.getInt("EditorFontSize", defaultFont.getSize());
        this.editorFont = new Font(fontFamily, Font.PLAIN, fontSize);
    }

    private void loadEditorColors() {

        Preferences pref = Preferences.userNodeForPackage(Slang.class);
        String[] editorColors = new String[9];

        editorColors[0] = pref.get("KeywordsColor", this.highlighterProfile.KEYWORDS_DEFAULT);
        editorColors[1] = pref.get("NumbersColor", this.highlighterProfile.NUMBERS_DEFAULT);
        editorColors[2] = pref.get("StringsColor", this.highlighterProfile.STRINGS_DEFAULT);
        editorColors[3] = pref.get("OperatorsColor", this.highlighterProfile.OPERATORS_DEFAULT);
        editorColors[4] = pref.get("CommentsColor", this.highlighterProfile.COMMENTS_DEFAULT);
        editorColors[5] = pref.get("TypesColor", this.highlighterProfile.TYPES_DEFAULT);
        editorColors[6] = pref.get("IdentifiersColor", this.highlighterProfile.IDENTIFIERS_DEFAULT);
        editorColors[7] = pref.get("DefaultColor", this.highlighterProfile.DEFAULT_TEXT_DEFAULT);
        editorColors[8] = pref.get("BgColor", this.highlighterProfile.BG_DEFAULT);

        this.setHighlighterProfileColors(editorColors);
    }

    private Font loadDefaultFont() {

        final int DEFAULT_SIZE = 14;

        String[] fontsList = {"Liberation Mono", "DejaVu Sans Mono", "Courier New"};

        try {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] familynames = env.getAvailableFontFamilyNames();
            Arrays.sort(familynames);

            int searchResult = 0;
            int index = 0;
            int length = fontsList.length;
            do {
                searchResult = Arrays.binarySearch(familynames, fontsList[index]);
                index++;

            } while ((searchResult < 0) && (index < length));

            if (searchResult >= 0) {
                Font f = new Font(fontsList[index - 1], Font.PLAIN, DEFAULT_SIZE);
                return f;
            } else {
                return new Font("monospaced", Font.PLAIN, DEFAULT_SIZE);
            }

        } catch (Exception e) {
            return new Font("monospaced", Font.PLAIN, DEFAULT_SIZE);
        }

    }

    public void resetToDefaultPreferences() {
        String[] editorColors = new String[9];
        editorColors[0] = this.highlighterProfile.KEYWORDS_DEFAULT;
        editorColors[1] = this.highlighterProfile.NUMBERS_DEFAULT;
        editorColors[2] = this.highlighterProfile.STRINGS_DEFAULT;
        editorColors[3] = this.highlighterProfile.OPERATORS_DEFAULT;
        editorColors[4] = this.highlighterProfile.COMMENTS_DEFAULT;
        editorColors[5] = this.highlighterProfile.TYPES_DEFAULT;
        editorColors[6] = this.highlighterProfile.IDENTIFIERS_DEFAULT;
        editorColors[7] = this.highlighterProfile.DEFAULT_TEXT_DEFAULT;
        editorColors[8] = this.highlighterProfile.BG_DEFAULT;
        this.setHighlighterProfileColors(editorColors);

        Font f = loadDefaultFont();
        this.setEditorFont(f);

    }

    public final void initLAF() {
        setSystemLAF();
    }

    private void setSystemLAF() {
        try {

            String systemLAFClassName = UIManager.getSystemLookAndFeelClassName();

            SystemInfo sysInfo = Slang.getApplication().getSystemInfo();
            String osName = sysInfo.getOSName().toLowerCase();
            if (osName.toLowerCase().equals("linux")) {
                systemLAFClassName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                UIManager.setLookAndFeel(systemLAFClassName);
            } else {
                if (MiscUtils.runningOnWindows() || systemLAFClassName.equals(UIManager.getCrossPlatformLookAndFeelClassName())  ) {
                    setNimbusLAF(systemLAFClassName);
                } else {
                    UIManager.setLookAndFeel(systemLAFClassName);
                }
            }
        } catch (Exception e1) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e2) {
            }
        }
    }

    private void setNimbusLAF(String systemLAFClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnsupportedLookAndFeelException {
        boolean lafSet = false;
        for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(laf.getName())) {
                UIManager.setLookAndFeel(laf.getClassName());
                lafSet = true;
            }
        }
        if (!lafSet) {
            UIManager.setLookAndFeel(systemLAFClassName);
        }
    }

    public void addListener(ApplicationPreferencesListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ApplicationPreferencesListener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        this.listeners.clear();
    }

    private void notifyListeners(String eventName) {
        Iterator<ApplicationPreferencesListener> iter = listeners.iterator();
        ApplicationPreferencesListener listener = null;
        if (eventName.equals(this.PROFILES_MANAGER_CHANGED_EVT_NAME)) {
            while (iter.hasNext()) {
                listener = iter.next();
                listener.profilesManagerChanged();
            }
        } else if (eventName.equals(this.HIGHLIGHTER_PROFILE_CHANGED_EVT_NAME)) {
            while (iter.hasNext()) {
                listener = iter.next();
                listener.highlighterProfileChanged();
            }
        } else if (eventName.equals(this.HIGHLIGHTER_PROFILE_COLORS_CHANGED_EVT_NAME)) {
            while (iter.hasNext()) {
                listener = iter.next();
                listener.highlighterProfileColorsChanged();
            }
        } else if (eventName.equals(this.EDITOR_FONT_CHANGED_EVT_NAME)) {
            while (iter.hasNext()) {
                listener = iter.next();
                listener.editorFontChangedEvent();
            }
        }
    }
}

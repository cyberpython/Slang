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

import glossaeditor.integration.SystemInfo;
import glossaeditor.integration.freedesktop.DesktopEnvironmentInfo;
import glossaeditor.integration.iconlocator.freedesktop.FreedesktopIconLocator;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Applications should use an IconManager to handle icon-loading.
 * IconManager, every time a search is performed,
 * maps the icon name and size to the corresponding filename and
 * then stores them in a hashmap, so that the next time we look for this icon
 * we do not have to scan the filesystem again.
 * In order to use an IconManager, simply make an instance: <br>
 * <b>IconManager iconManager = new IconManager(null);</b> //No FallbackIconLocator is used here<br>
 *
 * Then, you can call the getIcon() method to load Icons.<br>
 * <b>iconManager.getIcon("document-new", 24);</b>
 *
 * @author cyberpython
 */
public class IconManager {

    private HashMap<IconSearchKey, URL> systemIconURLs;
    private HashMap<IconSearchKey, URL> crossplatformIconURLs;
    private HashMap<IconSearchKey, Icon> systemIcons;
    private HashMap<IconSearchKey, Icon> crossplatformIcons;
    private IconLocator systemLocator;
    private IconLocator crossplatformLocator;
    private FallbackIconLocator fbil;

    /**
     * Default constructor
     * @param fbil The FallbackIconLocator to be used when searching for iconURLs.
     *          Can be null.
     */
    public IconManager(SystemInfo sysInfo, FallbackIconLocator fbil, boolean loadSystemIcons) {

        this.systemIconURLs = new HashMap<IconSearchKey, URL>();
        this.crossplatformIconURLs = new HashMap<IconSearchKey, URL>();

        this.systemIcons = new HashMap<IconSearchKey, Icon>();
        this.crossplatformIcons = new HashMap<IconSearchKey, Icon>();

        String osName = sysInfo.getOSName().toLowerCase();

        this.fbil = fbil;
        crossplatformLocator = new DefaultIconLocator();
        crossplatformLocator.setFallbackIconLocator(fbil);

        if (osName.equals("linux") && loadSystemIcons) {
            DesktopEnvironmentInfo deInfo = sysInfo.getDesktopEnvironmentInfo();
            systemLocator = new FreedesktopIconLocator(deInfo);
            systemLocator.setFallbackIconLocator(fbil);
        } else {
            systemLocator = crossplatformLocator;
        }

    }

    public void updateIcons(SystemInfo sysInfo, boolean loadSystemIcons){
        String osName = sysInfo.getOSName().toLowerCase();
        if (osName.equals("linux") && loadSystemIcons && (systemLocator==crossplatformLocator)) {
            this.systemIconURLs = new HashMap<IconSearchKey, URL>();
            this.systemIcons = new HashMap<IconSearchKey, Icon>();
            DesktopEnvironmentInfo deInfo = sysInfo.getDesktopEnvironmentInfo();
            deInfo.refreshThemeAndIconThemeName(osName);
            systemLocator = new FreedesktopIconLocator(deInfo);
            systemLocator.setFallbackIconLocator(this.fbil);
        } else {
            systemLocator = crossplatformLocator;
        }
    }

    private String getExtension(URL url) {
        String result = url.toExternalForm();
        result = result.substring(result.lastIndexOf(".") + 1);
        return result;
    }

    public Icon getSystemIcon(IconSearchKey key) {
        return getIcon(key, systemLocator, systemIconURLs, systemIcons);
    }

    public Icon getCrossplatformIcon(IconSearchKey key) {
        return getIcon(key, crossplatformLocator, crossplatformIconURLs, crossplatformIcons);
    }

    private Icon getIcon(IconSearchKey key, IconLocator locator, HashMap<IconSearchKey, URL> storage, HashMap<IconSearchKey, Icon> iconStorage) {
        Icon result = iconStorage.get(key);
        if (result == null) {
            URL foundAt = locateIcon(locator, storage, key);
            if (foundAt != null) {
                String extension = getExtension(foundAt);
                if (extension.toLowerCase().equals("png")) {
                    result = new ImageIcon(foundAt);
                    if (result != null) {
                        iconStorage.put(key, result);
                    }
                } else if (extension.toLowerCase().equals("svg")) {
                    int d = key.getSize();
                    result = SVGTranscoder.SVGToImageIcon(foundAt, d, d);
                    if (result != null) {
                        iconStorage.put(key, result);
                    }
                }
            }
        }
        return result;
    }

    public URL locateSystemIcon(IconSearchKey key) {
        return this.locateIcon(systemLocator, systemIconURLs, key);
    }

    public void locateSystemIcons(List<String> iconNames) {
        this.locateIcons(systemLocator, systemIconURLs, iconNames);
    }

    public URL locateCrossplatformIcon(IconSearchKey key) {
        return this.locateIcon(crossplatformLocator, crossplatformIconURLs, key);
    }

    public void locateCrossplatformIcons(List<String> iconNames) {
        this.locateIcons(crossplatformLocator, crossplatformIconURLs, iconNames);
    }

    private URL locateIcon(IconLocator locator, HashMap<IconSearchKey, URL> storage, IconSearchKey key) {
        URL iconURL = storage.get(key);
        //System.out.println("Searching:  "+key.getName()+":"+key.getSize());
        if (iconURL == null) //The icon is not yet in the hashtable
        {
            if (locator != null) {


                iconURL = locator.findIcon(key.getName(), key.getSize());
                if (iconURL != null) {//Ok, we found it
                    //System.out.println(key.getName()+":"+key.getSize()+" -> Found url : "+iconURL.toExternalForm());
                    storage.put(key, iconURL);
                    return iconURL;
                }
            }
        }
        return null;
    }

    private void locateIcons(IconLocator locator, HashMap<IconSearchKey, URL> storage, List<String> iconNames) {

        for (int i = 0; i < iconNames.size(); i++) {

            String iconEntry = iconNames.get(i);
            int sepIndex = iconEntry.lastIndexOf(":");
            String name = iconEntry.substring(0, sepIndex);
            int size = Integer.valueOf(iconEntry.substring(sepIndex + 1));

            IconSearchKey key = new IconSearchKey(name, size);
            //System.out.println("Looking for: "+iconEntry);
            this.locateIcon(locator, storage, key);
        }
    }

    /*public ImageIcon getIcon(String name, int size) {
    String key = (name + ":" + String.valueOf(size));
    return icons.get(key);
    }

    public ImageIcon getDefaultIcon(
    String name, int size) {
    if (this.defaultIcons != null) {
    String key = (name + ":" + String.valueOf(size));
    return defaultIcons.get(key);
    }

    return null;
    }*/
}

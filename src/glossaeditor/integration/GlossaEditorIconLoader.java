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
package glossaeditor.integration;

import glossaeditor.ui.components.misc.JSearchTextField;
import glossaeditor.integration.iconlocator.IconManager;
import glossaeditor.integration.iconlocator.IconSearchKey;
import glossaeditor.preferences.ApplicationPreferences;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class GlossaEditorIconLoader {

    private IconManager iconManager;
    private ApplicationPreferences appPrefs;
    private Hashtable<Object, IconSearchKey> items;

    public GlossaEditorIconLoader(IconManager iconManager, ApplicationPreferences appPrefs) {
        this.iconManager = iconManager;
        this.appPrefs = appPrefs;
        this.items = new Hashtable<Object, IconSearchKey>();
    }

    public void addItem(Object o, IconSearchKey key) {
        this.items.put(o, key);
    }

    public void loadIcons() {
        boolean useSystemIcons = true;
        if (iconManager != null) {
            Enumeration keys = items.keys();
            while (keys.hasMoreElements()) {
                Object object = keys.nextElement();

                IconSearchKey value = items.get(object);
                Icon icon = loadIcon(iconManager, value.getName(), value.getSize(), useSystemIcons);
                setIcon(object, icon);
            }
        }
    }

    private Icon loadIcon(IconManager iconManager, String iconName, int iconSize, boolean useSystemIcons) {
        if (useSystemIcons) {
            return iconManager.getSystemIcon(new IconSearchKey(iconName, iconSize));
        } else {
            return iconManager.getCrossplatformIcon(new IconSearchKey(iconName, iconSize));
        }
    }

    private void setIcon(Object o, Icon icon) {
        if ((o != null) && (icon != null)) {
            if (o instanceof JButton) {
                JButton tmp = (JButton) o;
                tmp.setIcon(icon);
            } else if (o instanceof JToggleButton) {
                JToggleButton tmp = (JToggleButton) o;
                tmp.setIcon(icon);
            } else if (o instanceof JMenuItem) {
                JMenuItem tmp = (JMenuItem) o;
                tmp.setIcon(icon);
            } else if (o instanceof JMenu) {
                JMenu tmp = (JMenu) o;
                tmp.setIcon(icon);
            } else if (o instanceof JLabel) {
                JLabel tmp = (JLabel) o;
                tmp.setIcon(icon);
            } else if (o instanceof JSearchTextField) {
                JSearchTextField tmp = (JSearchTextField) o;
                tmp.setIcon(icon);
            }
        }
    }
}

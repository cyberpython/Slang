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
package glossaeditor.ui.components.misc;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JHtmlLabel extends JLabel {

    private String innerText;

    public JHtmlLabel() {
        super();
        this.innerText = "";
    }

    public JHtmlLabel(Icon image) {
        super(image);
        this.innerText = "";
    }

    public JHtmlLabel(String text) {
        super();
        this.innerText = text;
        super.setText(getHtmlStringForText());
    }

    public JHtmlLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        this.innerText = "";
    }

    public JHtmlLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        this.innerText = text;
        super.setText(getHtmlStringForText());
    }

    public JHtmlLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.innerText = text;
        super.setText(getHtmlStringForText());
    }

    private String getHtmlStringForText() {
        return "<html><span style='" + this.getHtmlStyle() + "'>" + this.innerText + "</span></html>";
    }

    @Override
    public void setText(String text) {
        this.innerText = text;
        super.setText(getHtmlStringForText());
    }

    @Override
    public String getText() {
        return this.innerText;
    }

    public String getHtmlStyle() {
        return "background-color:" + colorToHTML(this.getBackground()) + ";color:" + colorToHTML(this.getForeground()) + ";font-weight:" + this.fontWeightToHTML(this.getFont()) + ";";
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        super.setText(getHtmlStringForText());
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        super.setText(getHtmlStringForText());
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        super.setText(getHtmlStringForText());
    }

    private String fontWeightToHTML(Font f) {
        if (f != null) {
            int fontStyle = f.getStyle();
            if ((fontStyle == Font.BOLD) || (fontStyle == Font.ITALIC + Font.BOLD)) {
                return "bold";
            }
        }
        return "normal";
    }

    private String fontStyleToHTML(Font f) {
        if (f != null) {
            int fontStyle = f.getStyle();
            if ((fontStyle == Font.ITALIC) || (fontStyle == Font.ITALIC + Font.BOLD)) {
                return "italic";
            }
        }
        return "normal";
    }

    private String colorToHTML(Color c) {
        if (c == null) {
            return "#000000";
        }
        return "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
    }
}

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
package glossaeditor.ui.components.misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cyberpython
 */
public class JTextPreviewPanel extends javax.swing.JPanel {

    private File src;
    private boolean modified;
    private int maxLinesToBeShown;
    private Charset charset;
    List<String> lines;
    private final int MARGIN_LEFT = 1;
    private final int MARGIN_TOP = 1;
    private final int MARGIN_RIGHT = 2;
    private final int MARGIN_BOTTOM = 2;
    private final int TEXT_MARGIN_LEFT = 10;
    private final int TEXT_MARGIN_TOP = 10;
    private final int TEXT_MARGIN_RIGHT = 10;
    private final int TEXT_MARGIN_BOTTOM = 10;
    private final int TEXT_LINE_GAP = 2;

    public JTextPreviewPanel() {
        this.src = null;
        this.modified = false;
        this.charset = Charset.defaultCharset();
        this.lines = new ArrayList<String>();
        this.maxLinesToBeShown = 15;
    }

    public void setSrc(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                f = null;
            } else {
                if (!f.equals(this.src)) {
                    this.src = f;
                    modified = true;
                }
            }
        }
        this.src = f;
        modified = true;
        this.repaint();
    }

    public File getSrc() {
        return this.src;
    }

    public void setCharset(Charset c) {
        this.charset = c;
        this.modified = true;
        this.repaint();
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setMaxLinesToBeShown(int max) {
        this.maxLinesToBeShown = max;
        this.modified = true;
        this.repaint();
    }

    public int getMaxLinesToBeShown() {
        return this.maxLinesToBeShown;
    }

    @Override
    protected void paintComponent(Graphics arg0) {
        super.paintComponent(arg0);
        this.drawPreview(arg0);
    }

    private void drawPreview(Graphics g) {

        if (modified) {
            this.getText(this.maxLinesToBeShown);
            modified = false;
        }

        if (this.src != null) {

            int rectWidth = this.getWidth();
            int rectHeight = this.getHeight();

            Shape clipRect = new Rectangle2D.Float(TEXT_MARGIN_LEFT,
                    TEXT_MARGIN_TOP,
                    this.getWidth() - TEXT_MARGIN_LEFT - TEXT_MARGIN_RIGHT,
                    this.getHeight() - TEXT_MARGIN_TOP - TEXT_MARGIN_BOTTOM);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, rectWidth, rectHeight);

            g2d.setPaint(Color.black);
            g2d.drawRect(0, 0, rectWidth - 1, rectHeight - 1);

            RenderingHints hints = g2d.getRenderingHints();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setPaint(new Color(45, 95, 150));
            Font f = g2d.getFont();
            g2d.setFont(f.deriveFont(this.calcFontSize((float) clipRect.getBounds2D().getHeight(), TEXT_LINE_GAP, g, f)));

            g2d.setClip(clipRect);

            int h = g.getFontMetrics().getHeight();
            int firstCharBottom = MARGIN_TOP + TEXT_MARGIN_TOP + h;
            int y = firstCharBottom;
            int maxY = this.getHeight() - MARGIN_BOTTOM - TEXT_MARGIN_BOTTOM;
            int i = 0;
            int x = MARGIN_LEFT + TEXT_MARGIN_LEFT;
            while ((y < maxY) && (i < this.lines.size())) {
                g2d.drawString(this.lines.get(i), x, y);
                y = y + h + TEXT_LINE_GAP;
                i++;
            }


            g2d.setRenderingHints(hints);

            g2d.setFont(f);

        }
    }

    private float calcFontSize(float height, float gap, Graphics g, Font f) {
        float fH = 0;
        int linesCount = lines.size();
        float toPoints = Toolkit.getDefaultToolkit().getScreenResolution() / 72.0f;
        /*float fW =0;
        int maxLineLength = 0;
        for (Iterator<String> it = lines.iterator(); it.hasNext();) {
        String line = it.next();
        maxLineLength = Math.max(maxLineLength, line.length());
        }
        fW = width / maxLineLength;
        fW = fW * toPoints;*/

        fH = ((height) - linesCount * gap * 2) / linesCount;
        fH = fH * toPoints;

        int h = (g.getFontMetrics(f.deriveFont(fH)).getHeight()) * linesCount + ((int) gap * linesCount);
        while (h > height) {
            fH--;
            h = (g.getFontMetrics(f.deriveFont(fH)).getHeight()) * linesCount + ((int) gap * linesCount);
        }

        //float result = Math.min(fW, fH);
        float result = fH;
        return result;
    }

    private void getText(int maxLength) {
        if (this.src == null) {
            return;
        }
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(this.src), this.charset));
            this.lines.clear();
            int i = 0;
            String line = "";
            while (((line = r.readLine()) != null) && (i < maxLength)) {
                this.lines.add(line);
                i++;
            }
            r.close();
            if (line != null) {
                this.lines.add("...");
            } else {
                while (this.lines.size() <= maxLength) {
                    this.lines.add(" ");
                }
            }

        } catch (IOException ioe) {
            this.lines.clear();
        }
    }
}

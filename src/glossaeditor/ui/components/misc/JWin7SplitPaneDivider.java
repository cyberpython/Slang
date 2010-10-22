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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JWin7SplitPaneDivider extends BasicSplitPaneDivider {

    private Color lineColor;

    public JWin7SplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
        lineColor = new Color(214, 229, 245);
    }

    private void drawHorizontal(Graphics2D g2d, int width, int height) {
        int y = height / 2;
        g2d.drawLine(0, y, width, y);
    }

    private void drawVertical(Graphics2D g2d, int width, int height) {
        int x = width / 2;
        g2d.drawLine(x, 0, x, height);

    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(lineColor);

        BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setStroke(stroke);

        int width = this.getWidth();
        int height = this.getHeight();

        if (this.orientation == JSplitPane.HORIZONTAL_SPLIT) {
            drawVertical(g2d, width, height);
        } else {
            drawHorizontal(g2d, width, height);
        }

        g2d.dispose();

    }
}

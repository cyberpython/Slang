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
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class JDropDownButton extends JButton {

    private ImageIcon iconWithSeparator;
    private ImageIcon iconWithoutSeparator;
    private int hotMinX;
    //private int hotMaxX;
    private JPopupMenu popupMenu;

    public JDropDownButton() {
        super();
        this.iconWithSeparator = null;
        this.iconWithoutSeparator = null;

        this.popupMenu = null;

        this.hotMinX = 0;
        //this.hotMaxX = this.getWidth();
        
        MouseInputListener mouseListener = new MouseInputListener() {

            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                //if ((x >= this.hotMinX) && (x <= this.hotMaxX)) {
                if (x >= hotMinX) {
                    setIcon(iconWithSeparator);
                } else {
                    setIcon(iconWithoutSeparator);
                }
            }

            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                setIcon(iconWithoutSeparator);
            }

            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                //if ((x >= this.hotMinX) && (x <= this.hotMaxX)) {
                if (x >= hotMinX) {
                    if (popupMenu != null) {
                        popupMenu.show(JDropDownButton.this, 0, getHeight());
                    }
                }

            }

            public void mouseReleased(MouseEvent e) {
            }
        };

        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public JPopupMenu getPopupMenu() {
        return this.popupMenu;
    }

    @Override
    public void setIcon(Icon icon) {
        int width = icon.getIconWidth() + 12;
        int height = icon.getIconHeight();
        Color fg = this.getForeground();
        BufferedImage img1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img1.createGraphics();
        int x1 = width - 7;
        int y1 = height / 2 - 2;
        int x2 = width - 3;
        int y2 = y1;
        int x3 = width - 5;
        int y3 = height / 2 + 2;
        Polygon p = new Polygon();
        p.addPoint(x1, y1);
        p.addPoint(x2, y2);
        p.addPoint(x3, y3);
        RenderingHints renderHints =
                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(renderHints);
        g2d.setColor(fg);
        g2d.drawPolygon(p);
        g2d.fillPolygon(p);

        icon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        this.iconWithoutSeparator = new ImageIcon(img1);

        BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img2.createGraphics();
        g2d.setRenderingHints(renderHints);
        g2d.setColor(fg);
        g2d.drawImage(img1, null, 0, 0);

        int x4 = width - 11;
        g2d.drawLine(x4, 0, x4, height);
        g2d.dispose();

        this.iconWithSeparator = new ImageIcon(img2);

        this.hotMinX = this.getMargin().left + x4;
        //this.hotMaxX = this.getMargin().left + width;
        super.setIcon(this.iconWithoutSeparator);
    }
}

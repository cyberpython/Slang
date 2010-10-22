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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JWin7Menu extends JMenu implements MouseListener, MenuListener {

    private Color hoverGrad1Color1;
    private Color hoverGrad1Color2;
    private Color hoverGrad2Color1;
    private Color hoverGrad2Color2;
    private Color hoverLineColor1;
    private Color hoverLineColor2;
    private Color hoverLineColor3;
    private Color hoverLineColor4;
    private Color hoverCornersColor;
    private Color pressedGrad1Color1;
    private Color pressedGrad1Color2;
    private Color pressedGrad2Color1;
    private Color pressedGrad2Color2;
    private Color pressedLineColor1;
    private Color pressedLineColor2;
    private Color pressedLineColor3;
    private Color pressedLineColor4;
    private Color pressedLineColor2a;
    private Color pressedLineColor2b;
    private Color pressedCornersColor;
    private boolean mouseOver;

    public JWin7Menu() {
        super();
        init();
    }

    public JWin7Menu(String text) {
        super(text);
        init();
    }

    public void copyMenuItems(JMenu src) {
        Component[] menuComponents = src.getMenuComponents();
        for (Component component : menuComponents) {
            this.add(component);
        }
    }

    public void menuSelected(MenuEvent e) {
         this.getParent().repaint();
    }

    public void menuDeselected(MenuEvent e) {
         this.getParent().repaint();
    }

    public void menuCanceled(MenuEvent e) {
         this.getParent().repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        
    }

    public void mouseReleased(MouseEvent e) {
        
    }

    public void mouseEntered(MouseEvent e) {
        this.mouseOver = true;
        this.getParent().repaint();
    }

    public void mouseExited(MouseEvent e) {
        this.mouseOver = false;
        this.getParent().repaint();
    }

    private void init() {
        hoverGrad1Color1 = new Color(248, 251, 254, 255);
        hoverGrad1Color2 = new Color(237, 242, 250, 255);

        hoverGrad2Color1 = new Color(215, 228, 244, 255);
        hoverGrad2Color2 = new Color(193, 210, 232, 255);

        hoverLineColor1 = new Color(187, 202, 219, 255);
        hoverLineColor2 = new Color(253, 254, 255, 255);
        hoverLineColor3 = new Color(239, 244, 249, 255);
        hoverLineColor4 = new Color(170, 188, 213, 255);

        hoverCornersColor = new Color(215, 225, 236, 255);


        pressedGrad1Color1 = new Color(225, 235, 245, 255);
        pressedGrad1Color2 = new Color(216, 228, 241, 255);

        pressedGrad2Color1 = new Color(207, 219, 236, 255);
        pressedGrad2Color2 = new Color(207, 220, 237, 255);

        pressedLineColor1 = new Color(187, 202, 219, 255);
        pressedLineColor2 = new Color(201, 212, 228, 255);
        pressedLineColor3 = new Color(207, 220, 237, 255);
        pressedLineColor4 = new Color(170, 188, 213, 255);

        pressedLineColor2a = new Color(212, 222, 234, 255);
        pressedLineColor2b = new Color(221, 232, 241, 255);

        pressedCornersColor = new Color(215, 225, 236, 255);

        mouseOver = false;

        this.addMouseListener(this);
        this.addMenuListener(this);
    }

    private void paintContent(Graphics2D g2d, int width, int height) {

        Icon icon = this.getIcon();

        int textOffsetX = this.getInsets().left;
        int iconWidth = 0;
        if (icon != null) {
            iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();
            int x = textOffsetX;
            textOffsetX += iconWidth + this.getIconTextGap();
            int y = (height - iconHeight) / 2;
            icon.paintIcon(this, g2d, x, y);
        }

        String txt = this.getText();
        Font f = this.getFont();
        g2d.setPaint(this.getForeground());
        g2d.setFont(f);
        FontMetrics fm = g2d.getFontMetrics();

        int x = textOffsetX;
        if (icon == null) {
            x = (width - fm.stringWidth(txt)) / 2;
        }
        int y = (height + fm.getAscent()) / 2;
        g2d.drawString(txt, x, y);
    }

    private void paintBorder(Graphics2D g2d, int width, int height, Color line1Color, Color line2Color, Color line3Color, Color line4Color, Color cornersColor) {
        int horLineX1 = 2;
        int horLineX2 = width - 3;

        int vertLine1X = 0;
        int vertLine2X = 1;
        int vertLine3X = width - 2;
        int vertLine4X = width - 1;

        int vertLineY1 = 2;
        int vertLineY2 = height - 3;

        int horLine1Y = 0;
        int horLine2Y = 1;
        int horLine3Y = height - 2;
        int horLine4Y = height - 1;

        g2d.setPaint(line1Color);
        g2d.drawLine(horLineX1, horLine1Y, horLineX2, horLine1Y);
        g2d.drawLine(vertLine1X, vertLineY1, vertLine1X, vertLineY2);

        g2d.setPaint(line2Color);
        g2d.drawLine(horLineX1, horLine2Y, horLineX2, horLine2Y);
        g2d.drawLine(vertLine2X, vertLineY1, vertLine2X, vertLineY2);

        g2d.setPaint(line3Color);
        g2d.drawLine(horLineX1, horLine3Y, horLineX2, horLine3Y);
        g2d.drawLine(vertLine3X, vertLineY1, vertLine3X, vertLineY2);

        g2d.setPaint(line4Color);
        g2d.drawLine(horLineX1, horLine4Y, horLineX2, horLine4Y);
        g2d.drawLine(vertLine4X, vertLineY1, vertLine4X, vertLineY2);

        g2d.setColor(cornersColor);
        int x = 1;
        int y = 0;
        g2d.fillRect(x, y, 1, 1);
        y = height - 1;
        g2d.fillRect(x, y, 1, 1);
        x = width - 2;
        g2d.fillRect(x, y, 1, 1);
        y = 0;
        g2d.fillRect(x, y, 1, 1);

        x = 0;
        y = 1;
        g2d.fillRect(x, y, 1, 1);
        y = height - 2;
        g2d.fillRect(x, y, 1, 1);
        x = width - 1;
        g2d.fillRect(x, y, 1, 1);
        y = 1;
        g2d.fillRect(x, y, 1, 1);

        x = 1;
        y = 1;
        g2d.fillRect(x, y, 1, 1);
        x = width - 2;
        g2d.fillRect(x, y, 1, 1);
        y = height - 2;
        g2d.fillRect(x, y, 1, 1);
        x = 1;
        g2d.fillRect(x, y, 1, 1);
    }

    private void paintMouseOver(Graphics2D g2d, int width, int height) {
        int midHeight = height / 2;
        int grad1Bottom = midHeight - 1;
        int grad2Bottom = height - 3;
        int grad2Height = height - midHeight - 2;


        int horLineX1 = 2;
        int horLineX2 = width - 3;

        GradientPaint grad1 = new GradientPaint(0, 2, hoverGrad1Color1, 0, grad1Bottom, hoverGrad1Color2);
        GradientPaint grad2 = new GradientPaint(0, midHeight, hoverGrad2Color1, 0, grad2Bottom, hoverGrad2Color2);

        g2d.setPaint(grad1);
        g2d.fillRect(horLineX1, 2, horLineX2, midHeight);
        g2d.setPaint(grad2);
        g2d.fillRect(horLineX1, midHeight, horLineX2, grad2Height);

        paintBorder(g2d, width, height, hoverLineColor1, hoverLineColor2, hoverLineColor3, hoverLineColor4, hoverCornersColor);
    }

    private void paintPressed(Graphics2D g2d, int width, int height) {
        int midHeight = height / 2;
        int grad1Bottom = midHeight - 1;
        int grad2Bottom = height - 3;
        int grad2Height = height - midHeight - 2;

        int horLineX1 = 2;
        int horLineX2 = width - 3;

        GradientPaint grad1 = new GradientPaint(0, 4, pressedGrad1Color1, 0, grad1Bottom, pressedGrad1Color2);
        GradientPaint grad2 = new GradientPaint(0, midHeight, pressedGrad2Color1, 0, grad2Bottom, pressedGrad2Color2);

        g2d.setPaint(grad1);
        g2d.fillRect(horLineX1, 4, horLineX2, midHeight);
        g2d.setPaint(grad2);
        g2d.fillRect(horLineX1, midHeight, horLineX2, grad2Height);

        paintBorder(g2d, width, height, pressedLineColor1, pressedLineColor2, pressedLineColor3, pressedLineColor4, pressedCornersColor);

        g2d.setPaint(pressedLineColor2a);
        g2d.fillRect(1, 2, width - 2, 1);
        g2d.setPaint(pressedLineColor2b);
        g2d.fillRect(1, 3, width - 2, 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = this.getWidth();
        int height = this.getHeight();

        if (isSelected()) {
            paintPressed(g2d, width, height);
        } else {
            if (mouseOver) {
                paintMouseOver(g2d, width, height);
            }
        }

        boolean opaque = this.isOpaque();
        this.setOpaque(false);

        Composite prevComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        paintContent(g2d, width, height);

        g2d.setComposite(prevComposite);

        this.setOpaque(opaque);

        g2d.dispose();
    }
}

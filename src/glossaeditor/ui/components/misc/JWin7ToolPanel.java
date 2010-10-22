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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JWin7ToolPanel extends JPanel {

    private Color grad1Color1;
    private Color grad1Color2;
    private Color grad2Color1;
    private Color grad2Color2;
    private Color lineColor1;
    private Color lineColor2;
    private Color lineColor3;

    public JWin7ToolPanel() {

        this.setBackground(new Color(0,0,0,1));

        grad1Color1 = new Color(250, 252, 253, 255);
        grad1Color2 = new Color(226, 236, 245, 255);

        grad2Color1 = new Color(218, 228, 242, 255);
        grad2Color2 = new Color(218, 230, 244, 255);

        lineColor1 = new Color(228, 239, 251, 255);
        lineColor2 = new Color(205, 218, 234, 255);
        lineColor3 = new Color(160, 175, 195, 255);

    }

    @Override
    protected void paintComponent(Graphics g) {
        
        int width = this.getWidth();
        int height = this.getHeight();
        int midHeight = height / 2;
        int grad1Height = midHeight - 1;
        int grad2Height = (height - midHeight - 2);



        Graphics2D g2d = (Graphics2D) g;

        GradientPaint grad1 = new GradientPaint(0, 0, grad1Color1, 0, grad1Height, grad1Color2);
        GradientPaint grad2 = new GradientPaint(0, midHeight, grad2Color1, 0, height-3, grad2Color2);

        g2d.setPaint(grad1);
        g2d.fillRect(0, 0, width, midHeight);
        g2d.setPaint(grad2);
        g2d.fillRect(0, midHeight, width, grad2Height);

        g2d.setPaint(lineColor1);
        int y = height - 3;
        g2d.drawLine(0, y, width, y);
        g2d.setPaint(lineColor2);
        y = height - 2;
        g2d.drawLine(0, y, width, y);
        g2d.setPaint(lineColor3);
        y = height-1;
        g2d.drawLine(0, y, width, y);

        this.paintComponents(g);

        g2d.dispose();
    }
}

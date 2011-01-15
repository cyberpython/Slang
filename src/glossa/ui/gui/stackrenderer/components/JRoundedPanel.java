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

package glossa.ui.gui.stackrenderer.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JRoundedPanel extends JPanel{

    private float cornderRadius;

    public JRoundedPanel() {
        super();
        setOpaque(true);
        cornderRadius = 10.0f;
    }

    @Override
    protected void paintBorder(Graphics g) {

    }



    @Override
    protected void paintComponent(Graphics g) {
        paintBg((Graphics2D)g);
    }

    private void paintBg(Graphics2D g){
        g.setPaint(getBackground());
        g.fill(new RoundRectangle2D.Float(0,0,getWidth()-1, getHeight()-1, cornderRadius, cornderRadius));
    }

}

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
package glossaeditor.ui.dialogs;

import glossaeditor.Slang;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class SplashDialog extends JDialog {

    private Graphics2D graphics;
    private BufferedImage bg;

    public SplashDialog() {
        super();
        setUndecorated(true);
        setIconImage(new ImageIcon(Slang.class.getResource("/glossaeditor/resources/icon.png")).getImage());
        JPanel container = new JPanel();
        try {
            bg = ImageIO.read(Slang.class.getResourceAsStream("/artwork/png/splash.png"));
        } catch (IOException ioe) {
        }
        JLabel imgLabel = new JLabel(new ImageIcon(bg));
        container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
        container.add(imgLabel);
        this.getContentPane().add(container);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        graphics = (Graphics2D) imgLabel.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font("Dialog", Font.BOLD, 12));
        graphics.setPaint(Color.BLUE);
    }

    public void close() {
        this.dispose();
    }

    public void setStatus(String status) {
        graphics.drawImage(bg, null, null);
        graphics.drawString(status, 10, 350);
        this.repaint();
    }
}

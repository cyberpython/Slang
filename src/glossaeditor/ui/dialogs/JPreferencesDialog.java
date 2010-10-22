/*
 * Copyright 2009 Georgios "cyberpython" Migdos cyberpython@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * JPreferencesDialog.java
 *
 * Created on 1 Μαρ 2009, 6:42:54 μμ
 */
package glossaeditor.ui.dialogs;

import glossaeditor.ui.components.misc.JWin7SplitPaneUI;
import glossaeditor.preferences.*;
import glossaeditor.util.MiscUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author cyberpython
 */
public class JPreferencesDialog extends javax.swing.JDialog implements ApplicationPreferencesListener {

    private final int DEFAULT_SIZE = 14;
    private boolean locationHasBeenSet;
    private Font editorFont;
    private ApplicationPreferences appPrefs;
    private int size;
    //TODO: FIXME
    Vector<Font> availableFonts;
    private HighlighterProfilesManager profilesManager;
    private static final String TEST_STR = "_+-*/,.:<>=!&abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZαβγδεζηθικλμνξοπρστυφχψωάέήίύόώϊϋΐΰΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩΆΈΉΊΎΌΏΪΫ";

    /** Creates new form JPreferencesDialog */
    public JPreferencesDialog(java.awt.Frame parent, ApplicationPreferences appPrefs) {
        super(parent, false);

        initComponents();

        this.editorFont = null;
        this.appPrefs = appPrefs;
        this.profilesManager = this.appPrefs.getProfilesManager();

        this.availableFonts = getFixedWidthFonts();

        this.jList1.setCellRenderer(new FontListCellRenderer());
        this.jComboBox1.setModel(this.profilesManager);

        this.locationHasBeenSet = false;

        this.jColorButton1.setListeningLabel(jLabel4);
        this.jColorButton2.setListeningLabel(jLabel5);
        this.jColorButton3.setListeningLabel(jLabel6);
        this.jColorButton4.setListeningLabel(jLabel7);
        this.jColorButton5.setListeningLabel(jLabel8);
        this.jColorButton7.setListeningPanel(jPanel4);

        this.applyWin7UIEnhancements();
    }

    private void applyWin7UIEnhancements() {
        if (MiscUtils.runningOnWindowsVistaOrLater()) {
            jSplitPane1.setUI(new JWin7SplitPaneUI());
            this.jSplitPane1.setBorder(null);
            Color bgColor = new Color(251, 251, 251);
            this.jSplitPane1.setBackground(bgColor);
            this.jPanel1.setBackground(bgColor);
            this.jPanel2.setBackground(bgColor);
            this.jPanel3.setBackground(bgColor);
            this.jPanel5.setBackground(bgColor);
            this.jPanel6.setBackground(bgColor);
            this.jPanel7.setBackground(bgColor);
            this.jPanel10.setBackground(bgColor);
            this.jTabbedPane1.setBackground(bgColor);
            this.jComboBox1.setBackground(bgColor);
        }
    }

    /*private void setWindowBordersFromLAF(){
    LookAndFeel laf = UIManager.getLookAndFeel();
    if (laf.getSupportsWindowDecorations()) {
    this.setUndecorated(true);
    this.setDefaultLookAndFeelDecorated(false);
    this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
    }
    }*/
    public void showDialog() {
        if (!locationHasBeenSet) {
            centerWindowOnFrame();
        }
        this.setEditorFont(this.appPrefs.getEditorFont());
        this.setColors(this.appPrefs.getHighlighterProfile().getColors());
        this.setVisible(true);
    }

    private void centerWindowOnFrame() {
        Frame parent = (Frame) this.getParent();
        Rectangle abounds = getBounds();
        int x = (parent.getWidth() - abounds.width) / 2;
        int y = (parent.getHeight() - abounds.height) / 2;
        setLocation(x, y);
        setLocationRelativeTo(parent);
        locationHasBeenSet = true;
        requestFocus();
    }

    public void setEditorFont(Font f) {
        this.editorFont = f;
    }

    public void setColors(Color[] colors) {
        jColorButton1.setColor(colors[0]);
        jColorButton2.setColor(colors[1]);
        jColorButton3.setColor(colors[2]);
        jColorButton4.setColor(colors[3]);
        jColorButton5.setColor(colors[4]);
        jColorButton6.setColor(colors[6]);
        jColorButton7.setColor(colors[8]);

        jLabel4.setForeground(colors[0]);
        jLabel5.setForeground(colors[1]);
        jLabel6.setForeground(colors[2]);
        jLabel7.setForeground(colors[3]);
        jLabel8.setForeground(colors[4]);

        jPanel4.setBackground(colors[8]);
    }

    public final Vector<Font> getFixedWidthFonts() {

        Vector<Font> result = new Vector<Font>();

        int fontSize = 14;
        int pwWidth = -1;
        int piWidth = -1;
        int bwWidth = -1;
        int biWidth = -1;
        int iwWidth = -1;
        int iiWidth = -1;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        BufferedImage tmp = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = tmp.getGraphics();

        String[] fontNames = ge.getAvailableFontFamilyNames();
        Font f = null;
        Font fb = null;
        Font fi = null;
        FontMetrics fm = null;
        FontMetrics fmb = null;
        FontMetrics fmi = null;

        for (int i = 0; i < fontNames.length; i++) {

            f = new Font(fontNames[i], Font.PLAIN, fontSize);
            fb = new Font(fontNames[i], Font.BOLD, fontSize);
            fi = new Font(fontNames[i], Font.ITALIC, fontSize);
            fm = g.getFontMetrics(f);
            fmb = g.getFontMetrics(fb);
            fmi = g.getFontMetrics(fi);
            pwWidth = fm.charWidth('W');
            piWidth = fm.charWidth('i');
            bwWidth = fmb.charWidth('W');
            biWidth = fmb.charWidth('i');
            iwWidth = fmi.charWidth('W');
            iiWidth = fmi.charWidth('i');

            if ((f.canDisplayUpTo(JPreferencesDialog.TEST_STR) == -1) && (pwWidth != 0) && (pwWidth == piWidth) && (pwWidth == bwWidth) && (bwWidth == biWidth) && (pwWidth == iwWidth) && (iwWidth == iiWidth)) {
                result.add(f);
            }
        }

        return result;
    }

    private void initEditorTab() {

        this.editorFont = this.appPrefs.getEditorFont();
        if (this.editorFont == null) {
            this.editorFont = new Font("monospaced", Font.PLAIN, this.DEFAULT_SIZE);
        }



        int fontIndex = -1;

        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < availableFonts.size(); i++) {
            Font f = availableFonts.get(i);
            model.addElement(f);
            if (editorFont.getFamily().equals(f.getFamily())) {
                fontIndex = i;
            }
        }
        if (fontIndex == -1) {
            model.addElement(this.editorFont);
            fontIndex = model.getSize() - 1;
        }
        jList1.setModel(model);
        jList1.setSelectedIndex(fontIndex);

        this.size = editorFont.getSize();
        jSpinner1.getModel().setValue(new Integer(this.size));

        this.profilesManager.setSelectedItem(this.profilesManager.getCurrentProfile());

        updatePreview(editorFont, (float) this.size);

    }

    private void colorProfileChanged(HighlighterProfile profile) {
        Color colors[] = profile.getColors();
        this.jColorButton1.setColor(colors[0]);
        this.jColorButton2.setColor(colors[1]);
        this.jColorButton3.setColor(colors[2]);
        this.jColorButton4.setColor(colors[3]);
        this.jColorButton5.setColor(colors[4]);
        this.jColorButton6.setColor(colors[6]);
        this.jColorButton7.setColor(colors[8]);

        this.jColorButton1.repaint();
        this.jColorButton2.repaint();
        this.jColorButton3.repaint();
        this.jColorButton4.repaint();
        this.jColorButton5.repaint();
        this.jColorButton6.repaint();
        this.jColorButton7.repaint();
    }

    private void updatePreview(Font f, float size) {
        jLabel4.setFont(f.deriveFont(Font.BOLD, size));
        jLabel5.setFont(f.deriveFont(Font.PLAIN, size));
        jLabel6.setFont(f.deriveFont(Font.PLAIN, size));
        jLabel7.setFont(f.deriveFont(Font.BOLD, size));
        jLabel8.setFont(f.deriveFont(Font.ITALIC, size));
    }

    private void updatePreview(Font f) {
        float fontSize = (float) jLabel4.getFont().getSize();
        jLabel4.setFont(f.deriveFont(Font.BOLD, fontSize));
        jLabel5.setFont(f.deriveFont(Font.PLAIN, fontSize));
        jLabel6.setFont(f.deriveFont(Font.PLAIN, fontSize));
        jLabel7.setFont(f.deriveFont(Font.BOLD, fontSize));
        jLabel8.setFont(f.deriveFont(Font.ITALIC, fontSize));
    }

    private void updatePreview(float size) {
        Font f = jLabel4.getFont();
        jLabel4.setFont(f.deriveFont(Font.BOLD, size));
        jLabel5.setFont(f.deriveFont(Font.PLAIN, size));
        jLabel6.setFont(f.deriveFont(Font.PLAIN, size));
        jLabel7.setFont(f.deriveFont(Font.BOLD, size));
        jLabel8.setFont(f.deriveFont(Font.ITALIC, size));
    }

    private void applyFontChanges() {

        int index = jList1.getSelectedIndex();
        ListModel model = jList1.getModel();
        if ((index >= 0) && (index < model.getSize())) {
            Font f = (Font) model.getElementAt(index);
            int fontSize = ((Integer) jSpinner1.getModel().getValue()).intValue();
            this.appPrefs.setEditorFont(f.deriveFont(Font.PLAIN, (float) fontSize));
        }
    }

    private void applyColorHighlightingChanges() {
        Color[] colors = new Color[9];
        colors[0] = jColorButton1.getColor();
        colors[1] = jColorButton2.getColor();
        colors[2] = jColorButton3.getColor();
        colors[3] = jColorButton4.getColor();
        colors[4] = jColorButton5.getColor();
        colors[5] = jColorButton1.getColor();
        colors[6] = jColorButton6.getColor();
        colors[7] = jColorButton6.getColor();
        colors[8] = jColorButton7.getColor();
        this.appPrefs.setHighlighterProfileColors(colors);
    }

    private void applyAllChanges() {
        applyFontChanges();
        applyColorHighlightingChanges();
    }

    private void applyResetAll() {
        if (jButton4.isEnabled()) {
            this.appPrefs.resetToDefaultPreferences();
            initEditorTab();
            jColorButton1.repaint();
            jColorButton2.repaint();
            jColorButton3.repaint();
            jColorButton4.repaint();
            jColorButton5.repaint();
            jColorButton6.repaint();
            jColorButton7.repaint();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="ApplicationPreferencesListener Implementation">
    public void profilesManagerChanged() {
        this.profilesManager = this.appPrefs.getProfilesManager();
    }

    public void HighlighterProfileChanged() {
        this.setColors(this.appPrefs.getHighlighterProfile().getColors());
    }

    public void HighlighterProfileColorsChanged() {
        this.setColors(this.appPrefs.getHighlighterProfile().getColors());
    }

    public void EditorFontChangedEvent() {
        this.setEditorFont(this.appPrefs.getEditorFont());
    }

    /*public void useRibbonChangedEvent(){
    this.jRadioButton11.setSelected(this.appPrefs.getUseRibbon());
    }*/
    // </editor-fold>
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jColorButton1 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel11 = new javax.swing.JLabel();
        jColorButton2 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel12 = new javax.swing.JLabel();
        jColorButton3 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel13 = new javax.swing.JLabel();
        jColorButton4 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel14 = new javax.swing.JLabel();
        jColorButton5 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel16 = new javax.swing.JLabel();
        jColorButton6 = new glossaeditor.ui.components.misc.JColorButton();
        jLabel15 = new javax.swing.JLabel();
        jColorButton7 = new glossaeditor.ui.components.misc.JColorButton();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel7 = new javax.swing.JPanel();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(JPreferencesDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setDoubleBuffered(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setBackground(resourceMap.getColor("jList1.background")); // NOI18N
        jList1.setForeground(resourceMap.getColor("jList1.foreground")); // NOI18N
        jList1.setName("jList1"); // NOI18N
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        jSpinner1.setName("jSpinner1"); // NOI18N
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel5);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jColorButton1.setText(resourceMap.getString("jColorButton1.text")); // NOI18N
        jColorButton1.setColor(resourceMap.getColor("jColorButton1.color")); // NOI18N
        jColorButton1.setName("jColorButton1"); // NOI18N
        jColorButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jColorButton1StateChanged(evt);
            }
        });
        jColorButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton1ActionPerformed(evt);
            }
        });

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jColorButton2.setText(resourceMap.getString("jColorButton2.text")); // NOI18N
        jColorButton2.setColor(resourceMap.getColor("jColorButton2.color")); // NOI18N
        jColorButton2.setName("jColorButton2"); // NOI18N
        jColorButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton2ActionPerformed(evt);
            }
        });

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jColorButton3.setText(resourceMap.getString("jColorButton3.text")); // NOI18N
        jColorButton3.setColor(resourceMap.getColor("jColorButton3.color")); // NOI18N
        jColorButton3.setName("jColorButton3"); // NOI18N
        jColorButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton3ActionPerformed(evt);
            }
        });

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jColorButton4.setText(resourceMap.getString("jColorButton4.text")); // NOI18N
        jColorButton4.setColor(resourceMap.getColor("jColorButton4.color")); // NOI18N
        jColorButton4.setName("jColorButton4"); // NOI18N
        jColorButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton4ActionPerformed(evt);
            }
        });

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jColorButton5.setText(resourceMap.getString("jColorButton5.text")); // NOI18N
        jColorButton5.setColor(resourceMap.getColor("jColorButton5.color")); // NOI18N
        jColorButton5.setName("jColorButton5"); // NOI18N
        jColorButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton5ActionPerformed(evt);
            }
        });

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jColorButton6.setText(resourceMap.getString("jColorButton6.text")); // NOI18N
        jColorButton6.setColor(resourceMap.getColor("jColorButton6.color")); // NOI18N
        jColorButton6.setName("jColorButton6"); // NOI18N
        jColorButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jColorButton6ActionPerformed(evt);
            }
        });

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jColorButton7.setText(resourceMap.getString("jColorButton7.text")); // NOI18N
        jColorButton7.setName("jColorButton7"); // NOI18N

        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel16)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jColorButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jColorButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jColorButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jColorButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jComboBox1, 0, 173, Short.MAX_VALUE))
                            .addComponent(jLabel17)))
                    .addComponent(jColorButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jColorButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jColorButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jColorButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jColorButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jColorButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jColorButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jColorButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jColorButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jColorButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jPanel4.setBackground(resourceMap.getColor("jPanel4.background")); // NOI18N
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setName("jPanel4"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setForeground(resourceMap.getColor("jLabel5.foreground")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setForeground(resourceMap.getColor("jLabel6.foreground")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setForeground(resourceMap.getColor("jLabel7.foreground")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setForeground(resourceMap.getColor("jLabel8.foreground")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addContainerGap(276, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel4);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel6);

        jTabbedPane1.addTab(resourceMap.getString("jSplitPane1.TabConstraints.tabTitle"), jSplitPane1); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridLayout(1, 5, 5, 0));

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton3);

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton4);

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton2);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton1);

        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel7.setName("jPanel7"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 820, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

        initEditorTab();
    }//GEN-LAST:event_formComponentShown

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged

        int index = jList1.getSelectedIndex();
        ListModel model = jList1.getModel();
        if ((index >= 0) && (index < model.getSize())) {
            updatePreview((Font) model.getElementAt(index));
        }
    }//GEN-LAST:event_jList1ValueChanged

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged

        updatePreview((float) ((Integer) jSpinner1.getModel().getValue()).intValue());
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jColorButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jColorButton1StateChanged
    }//GEN-LAST:event_jColorButton1StateChanged

    private void jColorButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton1ActionPerformed
    }//GEN-LAST:event_jColorButton1ActionPerformed

    private void jColorButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton2ActionPerformed
    }//GEN-LAST:event_jColorButton2ActionPerformed

    private void jColorButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton3ActionPerformed
    }//GEN-LAST:event_jColorButton3ActionPerformed

    private void jColorButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton4ActionPerformed
    }//GEN-LAST:event_jColorButton4ActionPerformed

    private void jColorButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton5ActionPerformed
    }//GEN-LAST:event_jColorButton5ActionPerformed

    private void jColorButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jColorButton6ActionPerformed
    }//GEN-LAST:event_jColorButton6ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

        this.colorProfileChanged((HighlighterProfile) this.profilesManager.getSelectedItem());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        applyAllChanges();
        this.setVisible(false);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        applyResetAll();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        applyAllChanges();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JPreferencesDialog dialog = new JPreferencesDialog(new javax.swing.JFrame(), null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private glossaeditor.ui.components.misc.JColorButton jColorButton1;
    private glossaeditor.ui.components.misc.JColorButton jColorButton2;
    private glossaeditor.ui.components.misc.JColorButton jColorButton3;
    private glossaeditor.ui.components.misc.JColorButton jColorButton4;
    private glossaeditor.ui.components.misc.JColorButton jColorButton5;
    private glossaeditor.ui.components.misc.JColorButton jColorButton6;
    private glossaeditor.ui.components.misc.JColorButton jColorButton7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

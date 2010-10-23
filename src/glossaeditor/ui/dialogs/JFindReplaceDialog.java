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
 * JFindReplaceDialog.java
 *
 * Created on 16 Ιανουάριος 2009, 4:01 μμ
 */
package glossaeditor.ui.dialogs;

import glossaeditor.Slang;
import glossaeditor.ui.components.editor.EditorView;
import glossaeditor.integration.GlossaEditorIconLoader;
import glossaeditor.integration.iconlocator.IconSearchKey;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author  cyberpython
 */
public class JFindReplaceDialog extends javax.swing.JDialog implements CaretListener, IconDisplayingDialog{

    private Frame parent;
    private boolean locationHasBeenSet;
    private EditorView editorView;

    /** Creates new form JFindReplaceDialog */
    public JFindReplaceDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.locationHasBeenSet = false;

        this.editorView = null;

        this.addWindowListener(new WindowListener() {

            public void windowClosing(WindowEvent e) {
                close();
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        this.requestIcons();
    }

    public void iconsChangedEvent() {
        this.requestIcons();
    }

    // <editor-fold defaultstate="collapsed" desc="CaretListener implementation">
    public void caretUpdate(CaretEvent e) {
        if (this.editorView != null) {
            String s = editorView.getEditorPane().getSelectedText();
            if ((s != null) && (s.length() > 0)) {
                this.jButton2.setEnabled(true);
                this.jButton3.setEnabled(true);
            } else {
                this.jButton2.setEnabled(false);
                this.jButton3.setEnabled(false);
            }
        }

    }

    /* ----------------------------------------------------- */
    // </editor-fold>


    public void close() {
        this.setVisible(false);
        this.editorView.clearSearchHighlights();
    }

    public void centerWindowOnFrame(Frame parent) {
        Rectangle abounds = getBounds();
        int x = (parent.getWidth() - abounds.width) / 2;
        int y = (parent.getHeight() - abounds.height) / 2;
        setLocation(x, y);
        setLocationRelativeTo(parent);
        locationHasBeenSet = true;
        requestFocus();
    }

    public void showDialog() {
        if (!locationHasBeenSet) {
            this.centerWindowOnFrame(parent);
        }
        this.setVisible(true);
    }

    public void showDialog(String selection) {
        if (!locationHasBeenSet) {
            this.centerWindowOnFrame(parent);
        }
        this.setVisible(true);
        this.jComboBox1.setSelectedItem(selection);
    }

    public void setEditorView(EditorView editorView) {
        this.editorView = editorView;
        editorView.getEditorPane().addCaretListener(this);
    }

    private void requestIcons() {
        GlossaEditorIconLoader iconLoader = Slang.getApplication().getIconLoader();
        iconLoader.addItem(this.jButton1, new IconSearchKey("window-close", 24));
        iconLoader.addItem(this.jButton3, new IconSearchKey("edit-find-replace", 24));
        iconLoader.addItem(this.jButton4, new IconSearchKey("edit-find", 24));
    }

    public void setIcon(ImageIcon icon, JButton button) {
        if (icon != null) {
            button.setIcon(icon);
        }
    }

    public void setIcons(ImageIcon closeIcon, ImageIcon replaceIcon, ImageIcon searchIcon) {
        setIcon(closeIcon, jButton1);
        setIcon(replaceIcon, jButton3);
        setIcon(searchIcon, jButton4);
    }

    public long doSearch() {
        String pattern = (String) jComboBox1.getSelectedItem();
        if ((pattern == null) || (pattern.trim().equals(""))) {
            return 0;
        }

        if (this.editorView.putSearchedForItem(pattern, pattern) == null) {
            jComboBox1.addItem(pattern);
        }
        return this.editorView.search(pattern, jCheckBox3.isSelected(), jCheckBox1.isSelected());
    }

    public long doReplace() {

        String pattern = (String) jComboBox1.getSelectedItem();
        String target = (String) jComboBox2.getSelectedItem();

        if ((pattern == null) || (pattern.trim().equals("")) || (target == null) || (target.trim().equals(""))) {
            return 0;
        }

        if (this.editorView.putSearchedForItem(pattern, pattern) == null) {
            jComboBox1.addItem(pattern);
        }


        if (this.editorView.putReplacedWithItem(target, target) == null) {
            jComboBox2.addItem(target);
        }

        this.editorView.getEditorPane().replaceSelection(target);

        return this.editorView.search(pattern, jCheckBox3.isSelected(), jCheckBox1.isSelected());
    }

    public void doReplaceAll() {

        this.editorView.clearSearchHighlights();
        this.editorView.clearSearchResults();

        String pattern = (String) jComboBox1.getSelectedItem();
        String target = (String) jComboBox2.getSelectedItem();

        if ((pattern == null) || (pattern.trim().equals("")) || (target == null) || (target.trim().equals(""))) {
            return;
        }

        if (this.editorView.putSearchedForItem(pattern, pattern) == null) {
            jComboBox1.addItem(pattern);
        }

        if (this.editorView.putReplacedWithItem(target, target) == null) {
            jComboBox2.addItem(target);
        }

        long remaining = 0;
        do {
            this.editorView.getEditorPane().replaceSelection(target);
            remaining = this.editorView.search(pattern, jCheckBox3.isSelected(), jCheckBox1.isSelected());
        } while (remaining > 0);

        this.editorView.clearSearchHighlights();
        this.editorView.clearSearchResults();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(JFindReplaceDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jComboBox1.setEditable(true);
        jComboBox1.setName("jComboBox1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jComboBox2.setEditable(true);
        jComboBox2.setName("jComboBox2"); // NOI18N

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        jCheckBox3.setSelected(true);
        jCheckBox3.setText(resourceMap.getString("jCheckBox3.text")); // NOI18N
        jCheckBox3.setToolTipText(resourceMap.getString("jCheckBox3.toolTipText")); // NOI18N
        jCheckBox3.setName("jCheckBox3"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(181, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, 548, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, 0, 527, Short.MAX_VALUE))
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox3))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addGap(34, 34, 34)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    if (jButton4.isEnabled()) {
        doSearch();
    }
}//GEN-LAST:event_jButton4ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    close();
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    if (jButton3.isEnabled()) {
        doReplace();
    }
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    if (jButton2.isEnabled()) {
        doReplaceAll();
    }
}//GEN-LAST:event_jButton2ActionPerformed
    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {

    public void run() {
    JFindReplaceDialog dialog = new JFindReplaceDialog(new javax.swing.JFrame(), true);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

    public void windowClosing(java.awt.event.WindowEvent e) {
    System.exit(0);
    }
    });
    dialog.setVisible(true);
    }
    });
    }*/
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}

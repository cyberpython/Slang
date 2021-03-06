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

/*
 * jCharsetSelectPane.java
 *
 * Created on Jun 4, 2010, 11:41:05 PM
 */
package glossaeditor.ui.components.charsetselectpane;

import glossaeditor.util.FileUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

/**
 *
 * @author cyberpython
 */
public class JCharsetSelectPane extends javax.swing.JPanel implements PropertyChangeListener {

    private DefaultComboBoxModel m1;
    private File f;
    private Charset charset;

    /** Creates new form jCharsetSelectPane */
    public JCharsetSelectPane() {
        initComponents();
        f = null;
        loadCharsets();
        updatePreview();
    }

    public Charset getSelectedCharset() {
        return this.charset;
    }

    private void updatePreview() {
        this.jTextPreviewPanel1.setSrc(f);
        if (autoDetectCharset()) {
            this.charset = FileUtils.detectCharset(this.f);

        } else {
            this.charset = ((CharsetWrapper) m1.getSelectedItem()).getCharset();
        }
        this.jTextPreviewPanel1.setCharset(this.charset);
    }

    private boolean autoDetectCharset() {
        return this.jRadioButton1.isSelected();
    }

    private void loadCharsets() {
        m1 = new DefaultComboBoxModel();


        SortedMap<String, Charset> cm = Charset.availableCharsets();
        Set<Entry<String, Charset>> keyset = cm.entrySet();
        for (Iterator<Entry<String, Charset>> it = keyset.iterator(); it.hasNext();) {
            Entry<String, Charset> entry = it.next();
            CharsetWrapper cw = new CharsetWrapper(entry.getValue());
            m1.addElement(cw);
        }

        if (Charset.isSupported("windows-1253")) {
            CharsetWrapper win1253 = new CharsetWrapper(Charset.forName("windows-1253"));
            m1.setSelectedItem(win1253);
        } else if (Charset.isSupported("UTF-8")) {
            CharsetWrapper UTF8 = new CharsetWrapper(Charset.forName("UTF-8"));
            m1.setSelectedItem(UTF8);
        }

        this.jComboBox1.setModel(m1);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            this.f = null;
            

        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            File selectedFile = (File) e.getNewValue();
            //if (selectedFile.getName().toLowerCase().endsWith(".gls")) {
                this.f = selectedFile;
            //} else {
            //    this.f = null;
            //}
        }

        this.updatePreview();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTextPreviewPanel1 = new glossaeditor.ui.components.misc.JTextPreviewPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox();

        setName("Form"); // NOI18N

        jTextPreviewPanel1.setName("jTextPreviewPanel1"); // NOI18N

        javax.swing.GroupLayout jTextPreviewPanel1Layout = new javax.swing.GroupLayout(jTextPreviewPanel1);
        jTextPreviewPanel1.setLayout(jTextPreviewPanel1Layout);
        jTextPreviewPanel1Layout.setHorizontalGroup(
            jTextPreviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );
        jTextPreviewPanel1Layout.setVerticalGroup(
            jTextPreviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 187, Short.MAX_VALUE)
        );

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(JCharsetSelectPane.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText(resourceMap.getString("jRadioButton2.text")); // NOI18N
        jRadioButton2.setName("jRadioButton2"); // NOI18N
        jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton2StateChanged(evt);
            }
        });
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jComboBox1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jComboBox1PropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 186, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton2)
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton1)
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextPreviewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTextPreviewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jComboBox1PropertyChange
    }//GEN-LAST:event_jComboBox1PropertyChange

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        updatePreview();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jRadioButton2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton2StateChanged
    }//GEN-LAST:event_jRadioButton2StateChanged

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        updatePreview();
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        updatePreview();
    }//GEN-LAST:event_jRadioButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private glossaeditor.ui.components.misc.JTextPreviewPanel jTextPreviewPanel1;
    // End of variables declaration//GEN-END:variables
}

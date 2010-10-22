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
 * JCommandsListPanel.java
 *
 * Created on Jul 11, 2010, 1:34:19 PM
 */

package glossaeditor.ui.components.commandslist;

import glossaeditor.ui.components.editor.EditorView;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;

/**
 *
 * @author cyberpython
 */
public class JCommandsListPanel extends javax.swing.JPanel {

    private EditorView editorView1;

    /** Creates new form JCommandsListPanel */
    public JCommandsListPanel() {
        initComponents();

        DefaultListModel model = new DefaultListModel();
        this.populateCommandsList(model);

        this.jList2.setCellRenderer(new CommandsListCellRenderer());
        this.jList2.setModel(model);

    }

    public void setListFont(Font f){
        this.jList2.setFont(f);
    }

    public void setListColors(Color fg, Color bg){
        this.jList2.setForeground(fg);
        this.jList2.setBackground(bg);
    }
    
    public void setEditorView(EditorView v){
        this.editorView1 = v;
    }

    private void populateCommandsList(DefaultListModel list) {

        list.addElement("");
        list.addElement(new CommandsListItem("    div", "div"));
        list.addElement(new CommandsListItem("    mod", "mod"));
        list.addElement("");
        list.addElement(new CommandsListItem("    Ή", "Ή"));
        list.addElement(new CommandsListItem("    ΚΑΙ", "ΚΑΙ"));
        list.addElement(new CommandsListItem("    ΌΧΙ", "ΌΧΙ"));

        list.addElement("Separator");

        list.addElement(new CommandsListItem("  Πρόγραμμα", "Πρόγραμμα"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Σταθερές", "Σταθερές"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Μεταβλητές", "Μεταβλητές"));
        list.addElement(new CommandsListItem("        ακέραιες:", "ακέραιες:"));
        list.addElement(new CommandsListItem("        πραγματικές:", "πραγματικές:"));
        list.addElement(new CommandsListItem("        χαρακτήρες:", "χαρακτήρες:"));
        list.addElement(new CommandsListItem("        λογικές:", "λογικές:"));

        list.addElement("");

        list.addElement(new CommandsListItem("  Αρχή", "Αρχή"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Γράψε", "Γράψε"));
        list.addElement(new CommandsListItem("    Διάβασε", "Διάβασε"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Αν .. Τότε", "Αν    Τότε"));
        list.addElement(new CommandsListItem("    Αλλιώς_Αν .. Τότε", "Αλλιώς_Αν    Τότε"));
        list.addElement(new CommandsListItem("    Αλλιώς", "Αλλιώς"));
        list.addElement(new CommandsListItem("    Τέλος_Αν", "Τέλος_Αν"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Επίλεξε", "Επίλεξε"));
        list.addElement(new CommandsListItem("        Περίπτωση", "Περίπτωση"));
        list.addElement(new CommandsListItem("        Περίπτωση Αλλιώς", "Περίπτωση Αλλιώς"));
        list.addElement(new CommandsListItem("    Τέλος_Επιλογών", "Τέλος_Επιλογών"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Για .. Από .. Μέχρι", "Για    Από    Μέχρι"));
        list.addElement(new CommandsListItem("    Για .. Από .. Μέχρι .. Με Βήμα", "Για    Από    Μέχρι    Με Βήμα"));
        list.addElement(new CommandsListItem("    Τέλος_Επανάληψης", "Τέλος_Επανάληψης"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Όσο .. Επανάλαβε", "Όσο    Επανάλαβε"));
        list.addElement(new CommandsListItem("    Τέλος_Επανάληψης", "Τέλος_Επανάληψης"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Αρχή_Επανάληψης", "Αρχή_Επανάληψης"));
        list.addElement(new CommandsListItem("    Μέχρις_Ότου", "Μέχρις_Ότου"));

        list.addElement("");

        list.addElement(new CommandsListItem("    Κάλεσε", "Κάλεσε"));

        list.addElement("");

        list.addElement(new CommandsListItem("  Τέλος_Προγράμματος", "Τέλος_Προγράμματος"));

        list.addElement("Separator");

        list.addElement(new CommandsListItem("  Διαδικασία", "Διαδικασία"));
        list.addElement(new CommandsListItem("  Τέλος_Διαδικασίας", "Τέλος_Διαδικασίας"));

        list.addElement("");

        list.addElement(new CommandsListItem("  Συνάρτηση", "Συνάρτηση"));
        list.addElement(new CommandsListItem("    : ακέραια", ": ακέραια"));
        list.addElement(new CommandsListItem("    : πραγματική", ": πραγματική"));
        list.addElement(new CommandsListItem("    : χαρακτήρας", ": χαρακτήρας"));
        list.addElement(new CommandsListItem("    : λογική", ": λογική"));
        list.addElement(new CommandsListItem("  Τέλος_Συνάρτησης", "Τέλος_Συνάρτησης"));
        list.addElement("");

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();

        setName("Form"); // NOI18N

        jScrollPane2.setDoubleBuffered(true);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(24, 0));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(JCommandsListPanel.class);
        jList2.setBackground(resourceMap.getColor("jList2.background")); // NOI18N
        jList2.setFont(resourceMap.getFont("jList2.font")); // NOI18N
        jList2.setForeground(resourceMap.getColor("jList2.foreground")); // NOI18N
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList2.setDoubleBuffered(true);
        jList2.setName("jList2"); // NOI18N
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jList2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jList2KeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked

        if ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 2)) {
            Object selectedValue = jList2.getSelectedValue();
            if (selectedValue != null && editorView1!=null) {
                if (selectedValue instanceof CommandsListItem) {
                    editorView1.insertText(((CommandsListItem) selectedValue).getCommand());
                    editorView1.requestFocusOnEditor();
                }
            }
        }
}//GEN-LAST:event_jList2MouseClicked

    private void jList2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList2KeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Object selectedValue = jList2.getSelectedValue();
            if (selectedValue != null && editorView1!=null) {
                if (selectedValue instanceof CommandsListItem) {
                    editorView1.insertText(((CommandsListItem) selectedValue).getCommand());
                    editorView1.requestFocusOnEditor();
                }
            }
        }
}//GEN-LAST:event_jList2KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}

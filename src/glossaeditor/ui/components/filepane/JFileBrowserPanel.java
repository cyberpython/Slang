/*
 *  The MIT License
 * 
 *  Copyright 2010 Georgios Migdos <cyberpython@gmail.com>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

/*
 * JFileBrowserPanel.java
 *
 * Created on Mar 26, 2010, 5:31:16 PM
 */
package glossaeditor.ui.components.filepane;

import documentcontainer.DocumentContainer;
import glossaeditor.Slang;
import glossaeditor.ui.filefilters.AllFilesFilter;
import glossaeditor.ui.filefilters.GlossaFileFilter;
import glossaeditor.integration.GlossaEditorIconLoader;
import glossaeditor.integration.iconlocator.IconSearchKey;
import glossaeditor.util.FileUtils;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JFileBrowserPanel extends javax.swing.JPanel {

    private class RootDir{
        private final String COMPUTER_LABEL = "Ο Υπολογιστής Μου";
        private String path;

        public RootDir() {
            this.path = this.COMPUTER_LABEL;
        }

        public String getPath(){
            return this.path;
        }
    }

    private final String FS_LABEL = "Σύστημα αρχείων";

    private JListViewListCellRenderer filesRenderer;
    private DefaultListModel filesModel;

    private AllFilesFilter allFilesFilter;
    private GlossaFileFilter glossaFileFilter;

    private File currentPath;
    private RootDir rootDir;
    //private GlossaEditorView editorView;
    private DocumentContainer documentContainer;

    private JDummyLabel folderIconDummyLabel;
    private JDummyLabel fileIconDummyLabel;

    /** Creates new form JFileBrowserPanel */
    public JFileBrowserPanel() {
        initComponents();

        this.folderIconDummyLabel = new JDummyLabel(this.jList1);
        this.fileIconDummyLabel = new JDummyLabel(this.jList1);

        this.filesRenderer = new JListViewListCellRenderer();

        this.filesModel = new DefaultListModel();
        this.jList1.setCellRenderer(this.filesRenderer);
        this.jList1.setModel(this.filesModel);

        this.allFilesFilter = new AllFilesFilter();
        this.glossaFileFilter = new GlossaFileFilter();

        this.rootDir = new RootDir();
        this.currentPath = new File(System.getProperty("user.home"));
        this.documentContainer = null;

        String path = "resources/file-icon.png";
        java.net.URL iconURL = Slang.class.getResource(path);
        this.filesRenderer.setIconForExtension("gls", new ImageIcon(iconURL));

        path = "/artwork/icons/png/crossplatform/s24x24/document.png";
        iconURL = Slang.class.getResource(path);
        this.fileIconDummyLabel.setIcon(new ImageIcon(iconURL));

        path = "/artwork/icons/png/crossplatform/s24x24/folder.png";
        iconURL = Slang.class.getResource(path);
        this.folderIconDummyLabel.setIcon(new ImageIcon(iconURL));

        this.filesRenderer.setFileIconLabel(this.fileIconDummyLabel);
        this.filesRenderer.setFolderIconLabel(this.folderIconDummyLabel);

        this.requestIcons();
    }

   
    public void setBackgrounds(Color bg){
        this.setBackground(bg);
        this.jToolBar1.setBackground(bg);
        this.jButton1.setBackground(bg);
        this.jButton2.setBackground(bg);
        this.jButton3.setBackground(bg);
        this.jButton4.setBackground(bg);
        this.jToggleButton1.setBackground(bg);
        this.jSeparator1.setBackground(bg);
        this.jSeparator2.setBackground(bg);
        this.jRadioButton1.setBackground(bg);
        this.jRadioButton2.setBackground(bg);
    }

    public void setDocumentContainer(DocumentContainer container) {
        this.documentContainer = container;
    }

    private void requestIcons(){
        GlossaEditorIconLoader iconLoader = Slang.getApplication().getIconLoader();
        iconLoader.addItem(this.jButton1, new IconSearchKey("go-home", 24));
        iconLoader.addItem(this.jButton2, new IconSearchKey("computer", 24));
        iconLoader.addItem(this.jButton3, new IconSearchKey("go-up", 24));
        iconLoader.addItem(this.jButton4, new IconSearchKey("view-refresh", 24));
        iconLoader.addItem(this.jToggleButton1, new IconSearchKey("locked", 24));
        iconLoader.addItem(this.jLabel1, new IconSearchKey("folder", 24));
        iconLoader.addItem(this.folderIconDummyLabel, new IconSearchKey("folder", 24));
        iconLoader.addItem(this.fileIconDummyLabel, new IconSearchKey("document", 24));
    }

    public void setIcons(ImageIcon folderIcon, ImageIcon fileIcon) {
        this.folderIconDummyLabel.setIcon(folderIcon);
        this.fileIconDummyLabel.setIcon(fileIcon);
        this.jLabel1.setIcon(folderIcon);
    }

    public void setUIIcons(ImageIcon computerIcon, ImageIcon homeIcon, ImageIcon upIcon, ImageIcon refreshIcon, ImageIcon showHiddenIcon) {
        if(computerIcon!=null){
            this.jButton2.setIcon(computerIcon);
        }
        if(homeIcon!=null){
            this.jButton1.setIcon(homeIcon);
        }
        if(upIcon!=null){
            this.jButton3.setIcon(upIcon);
        }
        if(refreshIcon!=null){
            this.jButton4.setIcon(refreshIcon);
        }
        if(showHiddenIcon!=null){
            this.jToggleButton1.setIcon(showHiddenIcon);
        }
    }

    private void setPath(File root, FileFilter filter, boolean showHidden) {
        if(root==null){
            this.setPath(this.rootDir);
        }
        else if (root.isDirectory()) {
            String dirName = root.getName();
            if(root.getAbsolutePath().equals("/")){
                dirName = this.FS_LABEL;
            }
            else if(dirName.trim().equals("")){
                dirName = root.getAbsolutePath();
            }
            this.jLabel1.setText("<html> "+dirName+" </html>");
            this.currentPath = root;
            File[] items = root.listFiles(filter);
            Arrays.sort(items);
            List<File> files = new ArrayList<File>();

            this.filesModel.clear();
            for (File file : items) {
                if (file.isDirectory()) {
                    this.addItem(file, showHidden);
                } else {
                    files.add(file);
                }
            }
            for (Iterator<File> it = files.iterator(); it.hasNext();) {
                File file = it.next();
                this.addItem(file, showHidden);
            }

        }
    }

    private void setPath(RootDir root) {
            this.jLabel1.setText("<html>"+root.getPath()+"</html>");
            this.currentPath = null;
            
            File[] items = File.listRoots();
            Arrays.sort(items);

            this.filesModel.clear();
            for (File file : items) {
                this.addItem(file, true);
            }
    }

    private void addItem(File f, boolean showHidden) {
        if (f.isHidden()) {
            if (showHidden) {
                this.filesModel.addElement(f);
            }
        } else {
            this.filesModel.addElement(f);
        }
    }

    public void setDir(Object o) {
        if(o!=null){
            if(o instanceof File){
                File f = (File) o;
                if (f.isDirectory()) {
                    setPath(f, this.getFilter(), this.jToggleButton1.isSelected());
                    this.jList1.ensureIndexIsVisible(0);
                }
            }
            else if(o instanceof RootDir){
                RootDir r = (RootDir) o;
                this.setPath(r);
            }
        }
    }

    private FileFilter getFilter(){
        if(this.jRadioButton1.isSelected()){
            return allFilesFilter;
        }
        return glossaFileFilter;
    }

    public void goUp() {
        if (this.currentPath != null) {
            File parent = this.currentPath.getParentFile();
            if (parent != null) {
                this.setDir(parent);
            }
        }
    }

    private void itemEnabled() {
        File f = (File) this.jList1.getSelectedValue();
        if (f.isDirectory()) {
            this.setDir(f);
        } else if (f.isFile()) {
            if (this.documentContainer != null) {
                this.documentContainer.open(f, FileUtils.detectCharset(f));
            }
        }
    }

    public void refresh(){
        this.setDir(this.currentPath);
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
        jToolBar1 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jToggleButton1 = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N
        jToolBar1.setOpaque(false);
        jToolBar1.setPreferredSize(new java.awt.Dimension(209, 32));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(JFileBrowserPanel.class);
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setToolTipText(resourceMap.getString("jButton2.toolTipText")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("jButton1.toolTipText")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setToolTipText(resourceMap.getString("jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setToolTipText(resourceMap.getString("jButton4.toolTipText")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar1.add(jSeparator2);

        jToggleButton1.setIcon(resourceMap.getIcon("jToggleButton1.icon")); // NOI18N
        jToggleButton1.setText(resourceMap.getString("jToggleButton1.text")); // NOI18N
        jToggleButton1.setToolTipText(resourceMap.getString("jToggleButton1.toolTipText")); // NOI18N
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setName("jToggleButton1"); // NOI18N
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setBackground(resourceMap.getColor("jList1.background")); // NOI18N
        jList1.setForeground(resourceMap.getColor("jList1.foreground")); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setName("jList1"); // NOI18N
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jList1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
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

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setForeground(resourceMap.getColor("jPanel1.foreground")); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked

        if ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getClickCount() == 2)) {
            this.itemEnabled();
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.goUp();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       this.setDir(new File(System.getProperty("user.home")));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRadioButton2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton2StateChanged
        
    }//GEN-LAST:event_jRadioButton2StateChanged

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        this.setDir(this.currentPath);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        this.setDir(this.currentPath);
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        this.setDir(this.currentPath);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        this.refresh();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setDir(this.rootDir);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jList1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyReleased
        int keyCode = evt.getKeyCode();
        if((keyCode == KeyEvent.VK_ENTER)||(keyCode == KeyEvent.VK_SPACE)){
            this.itemEnabled();
        }else if(keyCode == KeyEvent.VK_BACK_SPACE){
            this.goUp();
        }
    }//GEN-LAST:event_jList1KeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}

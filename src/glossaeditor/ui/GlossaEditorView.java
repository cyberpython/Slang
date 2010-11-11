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
 * GlossaEditorView.java
 */
package glossaeditor.ui;

import glossaeditor.ui.dialogs.SlangAboutBox;
import glossaeditor.ui.dialogs.JOpenOrInsertDialog;
import glossaeditor.ui.dialogs.JFindReplaceDialog;
import glossaeditor.ui.dialogs.ExportToHTMLResultsDialog;
import glossaeditor.ui.dialogs.JPreferencesDialog;
import glossaeditor.preferences.HighlighterProfile;
import documentcontainer.DocumentContainer;
import documentcontainer.DocumentIOManager;
import glossaeditor.Slang;
import glossaeditor.ui.components.codeinspector.CodeInspectorContainer;
import glossaeditor.ui.components.misc.JWin7Menu;
import glossaeditor.ui.components.misc.JWin7MenuBar;
import glossaeditor.ui.components.misc.JWin7SplitPaneUI;
import glossaeditor.ui.components.editor.EditorViewContainer;
import glossaeditor.ui.components.editor.EditorView;
import glossaeditor.ui.filefilters.GlossaFileFilter;
import glossaeditor.ui.filefilters.HtmlFileFilter;
import glossaeditor.ui.components.filepane.JFileBrowserPanel;
import glossaeditor.export.htmlgenerator.HtmlGenerator;
import glossaeditor.integration.FileDrop;
import glossaeditor.integration.GlossaEditorIconLoader;
import glossaeditor.integration.iconlocator.IconSearchKey;
import glossaeditor.preferences.ApplicationPreferences;
import glossaeditor.preferences.ApplicationPreferencesListener;
import glossaeditor.util.FileUtils;
import glossaeditor.util.MiscUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import org.jdesktop.application.Application.ExitListener;

/**
 * The application's main frame.
 */
public class GlossaEditorView extends FrameView implements EditorViewContainer, CodeInspectorContainer, DocumentContainer, ApplicationPreferencesListener, ExitListener {

    private final String applicationTitle = "Επεξεργαστής κώδικα για τη ΓΛΩΣΣΑ";
    private int newDocumentsCounter;
    private JFrame frame;
    private DocumentIOManager ioManager;
    private EditorView editorView1;
    private JOpenOrInsertDialog ooid;
    private JFindReplaceDialog findReplaceDialog;
    private JPreferencesDialog preferencesDialog;
    private ApplicationPreferences appPrefs;
    private JFileBrowserPanel jFileBrowserPanel1;
    private Icon glossaFileIcon;

    public GlossaEditorView(SingleFrameApplication app, String arg, ApplicationPreferences appPrefs) {
        super(app);

        this.appPrefs = appPrefs;

        preInit();
        initComponents();
        postInit(arg);
    }

    // <editor-fold defaultstate="collapsed" desc="Initialization">
    public final void preInit() {
        System.setProperty("suppressSwingDropSupport", "True");
        this.getApplication().addExitListener(this);
        this.frame = this.getFrame();
        this.setApplicationIcon();
        this.newDocumentsCounter = 0;
        this.frame.setTitle(applicationTitle);

        this.ioManager = new DocumentIOManager(this, Slang.class);
        this.ioManager.setConfirmOverwriteMessage("Το αρχείο:\n    %filename%\nυπάρχει ήδη!\n\nΘέλετε να γίνει επανεγγραφή;");
        this.ioManager.setOverwriteDialogTitle("Επανεγγραφή αρχείου");
        this.ioManager.setFileModifiedWarningMessage("Το αρχείο:\n    %filename%\nέχει αλλάξει!\n\nΘέλετε να αποθηκευθούν οι αλλαγές;");
        this.ioManager.setFileModifiedWarningDialogTitle("Αποθήκευση αλλαγών");

        this.glossaFileIcon = loadGlossaFileIcon();

    }

    /*private void createDropDownOpenButton() {
    jToolBar1.remove(this.jButton2);
    this.jButton2 = new JDropDownButton();
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(GlossaEditorView.class);
    this.jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
    this.jButton2.addMouseListener(new MouseListener() {

    public void mouseClicked(MouseEvent e) {
    jButton2MouseClicked(e);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    });
    this.jButton2.addKeyListener(new KeyListener() {

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    if ((e.getKeyCode() == e.VK_ENTER) || (e.getKeyCode() == e.VK_SPACE)) {
    open();
    }
    }
    });
    ((JDropDownButton) this.jButton2).setPopupMenu(jPopupMenu2);
    this.jToolBar1.add(this.jButton2, 1);
    }

    private void jButton2MouseClicked(MouseEvent evt) {

    if (jButton2.isEnabled() && (evt.getClickCount() == 1)) {
    open();
    }
    }*/
    public final void postInit(String arg) {

        //this.createDropDownOpenButton();
        this.createDocument();
        this.ioManager.loadRecentlyAccessed();

        this.updateEditorFont();
        this.updateEditorColors();

        this.codeInspector1.setContainer(this);

        this.jFileBrowserPanel1 = new JFileBrowserPanel();
        this.jFileBrowserPanel1.setDocumentContainer(this);
        this.jFileBrowserPanel1.setDir(new File(System.getProperty("user.home")));
        this.jPanel13.add(this.jFileBrowserPanel1);

        this.addSearchBoxDocumentListener();
        this.setSearchTextfieldIcon();
        this.jSearchTextField1.setTextWhenNotFocused("Αναζήτηση...");

        this.addFileDropSupport();


        //Argument handling:
        if (arg != null) {
            File f = new File(arg);
            try {
                this.createDocument(f);
            } catch (Exception e) {
                this.createDocument();
            }
        }

        this.jTabbedPane3.remove(jPanel10);
        this.jTabbedPane3.remove(codeInspector1);
        this.jPanel12.setVisible(false);

        this.requestIcons();

        //this.applyWin7UIEnhancements();
    }

    private void addSearchBoxDocumentListener() {
        this.jSearchTextField1.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                doLightSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                doLightSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                doLightSearch();
            }
        });
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Drag n Drop support">
    private void addFileDropSupport() {
        FileDrop fileDrop = new FileDrop(frame, new FileDrop.Listener() {

            public void filesDropped(java.io.File[] files) {
                if (files.length > 0) {
                    File myFile = files[0];
                    if (myFile != null) {
                        fileDropped(myFile);
                    }
                }
            }
        });
    }

    private void fileDropped(File f) {
        int result = ooid.showDialog();

        if (result == JOpenOrInsertDialog.OPEN) {
            open(f, FileUtils.detectCharset(f));
            editorView1.update(editorView1.getGraphics());
        } else if (result == JOpenOrInsertDialog.INSERT) {
            insertFile(f, FileUtils.detectCharset(f));
        }
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Settings and preferences">
    public void updateEditorFont() {
        Font f = this.appPrefs.getEditorFont();
        this.editorView1.setEditorFont(f);
        this.jCommandsListPanel1.setListFont(f.deriveFont(Font.BOLD));
    }

    public void updateEditorColors() {
        HighlighterProfile profile = this.appPrefs.getHighlighterProfile();
        this.editorView1.setEditorColors(profile.getColorsAsStrings());
        this.jCommandsListPanel1.setListColors(profile.getKeywordsColor(), profile.getBgColor());
    }

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Icon management">
    private void requestIcons() {
        GlossaEditorIconLoader iconLoader = Slang.getApplication().getIconLoader();

        //Buttons:
        iconLoader.addItem(this.jButton1, new IconSearchKey("document-new", 24));
        iconLoader.addItem(this.jButton2, new IconSearchKey("document-open", 24));
        iconLoader.addItem(this.jButton14, new IconSearchKey("document-open-recent", 24));
        iconLoader.addItem(this.jButton3, new IconSearchKey("document-save", 24));
        iconLoader.addItem(this.jButton4, new IconSearchKey("document-print", 24));
        iconLoader.addItem(this.jButton5, new IconSearchKey("edit-cut", 24));
        iconLoader.addItem(this.jButton6, new IconSearchKey("edit-copy", 24));
        iconLoader.addItem(this.jButton7, new IconSearchKey("edit-paste", 24));
        iconLoader.addItem(this.jButton8, new IconSearchKey("edit-undo", 24));
        iconLoader.addItem(this.jButton9, new IconSearchKey("edit-redo", 24));
        iconLoader.addItem(this.jButton10, new IconSearchKey("help-browser", 24));
        iconLoader.addItem(this.jButton11, new IconSearchKey("document-new", 16));
        iconLoader.addItem(this.jButton12, new IconSearchKey("document-open", 16));
        iconLoader.addItem(this.jButton13, new IconSearchKey("document-save", 16));
        iconLoader.addItem(this.jButton17, new IconSearchKey("window-close", 16));
        iconLoader.addItem(this.jButton18, new IconSearchKey("go-down", 16));

        //Menu items:
        //File:
        iconLoader.addItem(this.jMenuItem8, new IconSearchKey("document-new", 16));
        iconLoader.addItem(this.jMenuItem9, new IconSearchKey("document-open", 16));
        iconLoader.addItem(this.jMenu4, new IconSearchKey("document-open-recent", 16));
        iconLoader.addItem(this.jMenuItem10, new IconSearchKey("document-save", 16));
        iconLoader.addItem(this.jMenuItem11, new IconSearchKey("document-save-as", 16));
        iconLoader.addItem(this.jMenu2, new IconSearchKey("document-export", 16));
        iconLoader.addItem(this.jMenuItem27, new IconSearchKey("text-html", 16));
        iconLoader.addItem(this.jMenuItem12, new IconSearchKey("document-print", 16));
        iconLoader.addItem(this.jMenuItem13, new IconSearchKey("application-exit", 16));
        //Edit:
        iconLoader.addItem(this.jMenuItem14, new IconSearchKey("edit-undo", 16));
        iconLoader.addItem(this.jMenuItem15, new IconSearchKey("edit-redo", 16));
        iconLoader.addItem(this.jMenuItem16, new IconSearchKey("edit-cut", 16));
        iconLoader.addItem(this.jMenuItem17, new IconSearchKey("edit-copy", 16));
        iconLoader.addItem(this.jMenuItem18, new IconSearchKey("edit-paste", 16));
        iconLoader.addItem(this.jMenuItem19, new IconSearchKey("edit-delete", 16));
        iconLoader.addItem(this.jMenuItem20, new IconSearchKey("edit-select-all", 16));
        iconLoader.addItem(this.jMenuItem21, new IconSearchKey("edit-find", 16));
        iconLoader.addItem(this.jMenuItem22, new IconSearchKey("edit-find-replace", 16));
        iconLoader.addItem(this.jMenuItem25, new IconSearchKey("preferences-system", 16));
        //Help:
        iconLoader.addItem(this.jMenuItem23, new IconSearchKey("help-browser", 16));
        iconLoader.addItem(this.jMenuItem24, new IconSearchKey("help-about", 16));

        //Editor popup-menu:
        iconLoader.addItem(this.jMenuItem1, new IconSearchKey("edit-undo", 16));
        iconLoader.addItem(this.jMenuItem2, new IconSearchKey("edit-redo", 16));
        iconLoader.addItem(this.jMenuItem3, new IconSearchKey("edit-cut", 16));
        iconLoader.addItem(this.jMenuItem4, new IconSearchKey("edit-copy", 16));
        iconLoader.addItem(this.jMenuItem5, new IconSearchKey("edit-paste", 16));
        iconLoader.addItem(this.jMenuItem6, new IconSearchKey("edit-delete", 16));
        iconLoader.addItem(this.jMenuItem7, new IconSearchKey("edit-select-all", 16));

        //Search Field
        iconLoader.addItem(this.jSearchTextField1, new IconSearchKey("edit-find", 16));

    }

    private void setSearchTextfieldIcon() {
        String path = "/artwork/icons/png/crossplatform/s16x16/edit-find.png";
        URL iconURL = Slang.class.getResource(path);
        this.jSearchTextField1.setIcon(new ImageIcon(iconURL));
    }

    private Icon loadGlossaFileIcon() {
        String path = "resources/file-icon.png";
        URL iconURL = Slang.class.getResource(path);
        return new ImageIcon(iconURL);
    }

    private void setApplicationIcon() {
        java.net.URL url = ClassLoader.getSystemResource("glossaeditor/resources/icon.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        getFrame().setIconImage(img);
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Document I/O">   
    private void createDocument() {
        if (editorView1 != null) {
            jPanel8.remove(editorView1);
            editorView1.setVisible(false);
            this.jCommandsListPanel1.setEditorView(null);
        }
        this.editorView1 = new EditorView(++newDocumentsCounter, this);
        jPanel8.add(editorView1);
        this.jCommandsListPanel1.setEditorView(editorView1);
        editorView1.initEditor(this.jPopupMenu1, this.appPrefs.getEditorFont(), this.appPrefs.getHighlighterProfile().getColorsAsStrings(), null);

        enableDisableUndoRedo();

        if (this.findReplaceDialog != null) {
            findReplaceDialog.setEditorView(editorView1);
        }

        if(this.codeInspector1!=null){
            this.codeInspector1.refresh(this.editorView1.getEditorPane().getText());
        }

        jPanel8.validate();
        jPanel8.repaint();

    }

    private void createDocument(File f) {
        if (editorView1 != null) {
            jPanel8.remove(editorView1);
            editorView1.setVisible(false);
            this.jCommandsListPanel1.setEditorView(null);
        }
        this.editorView1 = new EditorView(f, this);
        jPanel8.add(editorView1);
        this.jCommandsListPanel1.setEditorView(editorView1);
        editorView1.initEditor(this.jPopupMenu1, this.appPrefs.getEditorFont(), this.appPrefs.getHighlighterProfile().getColorsAsStrings(), ioManager.getLastSelectedCharset());
        enableDisableUndoRedo();

        if (this.findReplaceDialog != null) {
            findReplaceDialog.setEditorView(editorView1);
        }

        if(this.codeInspector1!=null){
            this.codeInspector1.refresh(this.editorView1.getEditorPane().getText());
        }
        jPanel8.validate();
        jPanel8.repaint();
    }

    private void insertFile(File f, Charset charset) {
        if (f != null) {
            if (editorView1 != null) {
                try {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
                    StringBuilder buffer = new StringBuilder();
                    int c = 0;
                    try {
                        while ((c = reader.read()) != -1) {
                            buffer.append((char) c);
                        }
                    } catch (IOException ioe) {
                    }
                    editorView1.insertText(buffer.toString());
                } catch (FileNotFoundException fnfe) {
                }
            }
        }
    }

    public void createNew() {
        try {
            ioManager.createNew(frame, new GlossaFileFilter());
        } catch (IOException ioe) {
        }
    }

    public void open() {
        try {
            ioManager.open(frame, new GlossaFileFilter());
        } catch (IOException ioe) {
        }
    }

    public void open(File f, Charset charset) {
        try {
            ioManager.open(frame, f, new GlossaFileFilter(), charset);
        } catch (IOException ioe) {
        }
    }

    public boolean save() {
        try {
            return ioManager.save(frame, new GlossaFileFilter());
        } catch (IOException ioe) {
            return false;
        }
    }

    public boolean saveAs() {

        try {
            return ioManager.saveAs(frame, new GlossaFileFilter());
        } catch (IOException ioe) {
            return false;
        }
    }

    public void exportCurrentAsHtml() {



        JFileChooser fc = new JFileChooser();
        File current = new File(fc.getCurrentDirectory() + File.separator + glossaeditor.util.FileUtils.stripExtension(this.getCurrentDocTitle()) + ".html");
        fc.setSelectedFile(current);
        fc.setFileFilter(new HtmlFileFilter());


        boolean overwrite = false;
        int selection = fc.showSaveDialog(this.frame);

        File f = null;
        f = fc.getSelectedFile();

        if (selection == JFileChooser.APPROVE_OPTION) {

            if (f.exists()) {
                overwrite = this.ioManager.confirmOverwrite(this.frame, f.getName());
            } else {
                overwrite = true;
            }

            while (!overwrite) {
                selection = fc.showSaveDialog(this.frame);
                f = fc.getSelectedFile();
                if (selection == JFileChooser.APPROVE_OPTION) {
                    if (f.exists()) {
                        overwrite = this.ioManager.confirmOverwrite(this.frame, f.getName());
                    } else {
                        overwrite = true;
                    }
                } else {
                    return;
                }
            }


            HtmlGenerator gen = new HtmlGenerator();
            gen.setColors(this.appPrefs.getHighlighterProfile().getColors());
            try {
                Font editorFont = this.appPrefs.getEditorFont();
                String fontFamilyName = editorFont.getFamily();
                Float fontSizeInEm = editorFont.getSize() / 16.0f;

                boolean results = gen.generateHtml(new InputStreamReader(new ByteArrayInputStream(this.editorView1.getEditorPane().getText().getBytes())), f, this.getCurrentDocTitle(), fontFamilyName, fontSizeInEm, false, false, false);

                if (results == true) {
                    ExportToHTMLResultsDialog ethr = new ExportToHTMLResultsDialog(this.frame, f, Slang.getApplication().getSystemInfo().getOSName());
                    ethr.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Η δημιουργία του αρχείου HTML απέτυχε !", "Η εξαγωγή απέτυχε!", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this.frame, "Συνέβη ένα σφάλμα κατά τη δημιουργία της ιστοσελίδας!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            return;
        }

    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Document Printing">
    private void printDocument() {

        try {
            File f = File.createTempFile("glossa", ".html");
            HtmlGenerator gen = new HtmlGenerator();
            gen.setColors(this.appPrefs.getHighlighterProfile().getColors());
            try {
                Font editorFont = this.appPrefs.getEditorFont();
                String fontFamilyName = editorFont.getFamily();
                Float fontSizeInEm = editorFont.getSize() / 16.0f;

                boolean results = gen.generateHtml(new InputStreamReader(new ByteArrayInputStream(this.editorView1.getEditorPane().getText().getBytes())), f, this.getCurrentDocTitle(), fontFamilyName, fontSizeInEm, false, true, true);

                if (results == true) {
                    this.showFileInBrowser(f);
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Η δημιουργία του αρχείου HTML απέτυχε !", "Η εξαγωγή απέτυχε!", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this.frame, "Συνέβη ένα σφάλμα κατά τη δημιουργία της ιστοσελίδας!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this.frame, "Συνέβη ένα σφάλμα κατά τη δημιουργία της ιστοσελίδας!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Document editing">
    public void undo() {
        editorView1.undo();
        enableDisableUndoRedo();
    }

    public void redo() {
        editorView1.redo();
        enableDisableUndoRedo();
    }

    public void cut() {
        editorView1.cut();
    }

    public void copy() {
        editorView1.copy();
    }

    public void paste() {
        editorView1.paste();
    }

    public void deleteSelection() {
        editorView1.deleteSelection();
    }

    public void selectAll() {
        editorView1.selectAll();
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GUI updating/creation">
    private void applyWin7UIEnhancements() {
        if (MiscUtils.runningOnWindowsVistaOrLater()) {
            JWin7MenuBar jWin7MenuBar1 = this.createWin7MenuBar();
            this.setMenuBar(jWin7MenuBar1);
            jToolBar1.setVisible(false);

            jSplitPane1.setUI(new JWin7SplitPaneUI());
            jSplitPane2.setUI(new JWin7SplitPaneUI());

            Color bgColor = new Color(251, 251, 251);
            this.jSplitPane1.setBackground(bgColor);
            this.jSplitPane2.setBackground(bgColor);

            this.jPanel2.setBackground(bgColor);
            this.jPanel3.setBackground(bgColor);
            this.jPanel4.setBackground(bgColor);
            this.jPanel5.setBackground(bgColor);
            this.jPanel6.setBackground(bgColor);
            //this.jPanel7.setBackground(bgColor);
            this.jPanel11.setBackground(bgColor);
            this.jPanel12.setBackground(bgColor);
            this.jPanel13.setBackground(bgColor);
            this.jFileBrowserPanel1.setBackgrounds(bgColor);
            this.cliInterface1.setBackground(bgColor);

            this.jToolBar2.setBackground(bgColor);
            this.jButton11.setBackground(bgColor);
            this.jButton12.setBackground(bgColor);
            this.jButton13.setBackground(bgColor);


            this.jSplitPane1.setBorder(null);
            this.jSplitPane2.setBorder(null);

            this.jPanel2.setBorder(null);
            this.jPanel3.setBorder(null);
            this.jPanel5.setBorder(null);
            this.jPanel11.setBorder(null);

            this.jTabbedPane1.setBorder(null);
            this.jTabbedPane3.setBorder(null);
            this.jPanel8.setBorder(null);
            this.jPanel12.setBorder(null);

            /*this.jScrollPane1.setBorder(null);
            this.jScrollPane3.setBorder(null);*/

            //this.editorView1.removeBorders();
        }
    }

    private JWin7MenuBar createWin7MenuBar() {
        JWin7MenuBar jWin7MenuBar1 = new JWin7MenuBar();
        jWin7MenuBar1.setPreferredSize(new Dimension(jWin7MenuBar1.getPreferredSize().width, 30));

        JWin7Menu jWin7FileMenu = new JWin7Menu("Αρχείο");
        jWin7FileMenu.copyMenuItems(this.fileMenu);
        jWin7MenuBar1.add(jWin7FileMenu);

        JWin7Menu editMenu = new JWin7Menu("Επεξεργασία");
        editMenu.copyMenuItems(this.jMenu1);
        jWin7MenuBar1.add(editMenu);

        JWin7Menu programMenu = new JWin7Menu("Πρόγραμμα");
        programMenu.copyMenuItems(this.jMenu3);
        jWin7MenuBar1.add(programMenu);

        JWin7Menu jWin7HelpMenu = new JWin7Menu("Βοήθεια");
        jWin7HelpMenu.copyMenuItems(this.helpMenu);
        jWin7MenuBar1.add(jWin7HelpMenu);

        return jWin7MenuBar1;
    }

    private void enableDisableUndoRedo() {
        if (editorView1 != null) {
            boolean canUndo = editorView1.canUndo();
            boolean canRedo = editorView1.canRedo();

            jButton8.setEnabled(canUndo);
            jMenuItem1.setEnabled(canUndo);
            jMenuItem14.setEnabled(canUndo);

            jButton9.setEnabled(canRedo);
            jMenuItem2.setEnabled(canRedo);
            jMenuItem15.setEnabled(canRedo);

        }
    }

    private void enableUndoControls() {
        if (editorView1 != null) {
            boolean canUndo = true;

            jButton8.setEnabled(canUndo);
            jMenuItem1.setEnabled(canUndo);
            jMenuItem14.setEnabled(canUndo);

        }
    }

    private void enableDisableTextManipulation() {
        if (editorView1 != null) {
            String selectedText = editorView1.getSelectedText();
            boolean textIsSelected;
            if (selectedText != null) {
                textIsSelected = (selectedText.length() > 0);
            } else {
                textIsSelected = false;
            }

            jButton5.setEnabled(textIsSelected);
            jButton6.setEnabled(textIsSelected);
            jMenuItem16.setEnabled(textIsSelected);
            jMenuItem17.setEnabled(textIsSelected);
            jMenuItem19.setEnabled(textIsSelected);
            jMenuItem3.setEnabled(textIsSelected);
            jMenuItem4.setEnabled(textIsSelected);
            jMenuItem6.setEnabled(textIsSelected);


        }
    }

    private void enableDisableSave() {
        if (editorView1 != null) {
            boolean canSave = editorView1.isModified();

            jButton3.setEnabled(canSave);
            jMenuItem10.setEnabled(canSave);
        }
    }

    private void updatePositionLabel(int caretX, int caretY) {
        this.jLabel1.setText("Γρ. " + caretY + ", Στ. " + caretX);
    }

    private void updateFullPathLabel(String path) {
        this.jLabel3.setText(path);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="GUI Dialogs management">
    public void setFindReplaceDialog(JFindReplaceDialog dlg) {
        this.findReplaceDialog = dlg;
        this.findReplaceDialog.setEditorView(this.editorView1);
    }

    public void setOpenOrInsertDialog(JOpenOrInsertDialog dlg) {
        this.ooid = dlg;
    }

    public void setPreferencesDialog(JPreferencesDialog dlg) {
        this.preferencesDialog = dlg;
    }

    public void showPreferencesDialog() {
        if (this.preferencesDialog != null) {
            this.preferencesDialog.showDialog();
        }
    }

    public void showHelpContents() {
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            URI a = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            File f = new File(a);
            String cp = f.getParentFile().getCanonicalPath();
            File f2 = new File(cp + File.separator + "help" + File.separator + "index.html");
            desktop.open(f2);
        } catch (UnsupportedOperationException uoe) {
            this.xdgOpenHelp();
        } catch (Exception e) {
            System.err.println(e.toString());

        }
    }

    private void xdgOpenHelp() {
        String CROSS_DESKTOP_OPEN_FILE_COMMAND = "/usr/bin/xdg-open";
        String osName = Slang.getApplication().getSystemInfo().getOSName();
        if (osName.toLowerCase().equals("linux")) {
            try {
                URI a = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
                File f = new File(a);
                String cp = f.getParentFile().getCanonicalPath();
                File helpDir = new File(cp + File.separator + "help");
                String cmd = "/bin/sh \"" + CROSS_DESKTOP_OPEN_FILE_COMMAND + " index.html\"";
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(cmd, null, helpDir);
                p.waitFor();
            } catch (Exception e) {
                
            }
        }
    }

    public void showFileInBrowser(File f) {
        try {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            desktop.open(f);
        } catch (UnsupportedOperationException uoe) {
            this.xdgOpenFile(f);
        } catch (Exception e) {
            
        }
    }

    private void xdgOpenFile(File f) {
        String CROSS_DESKTOP_OPEN_FILE_COMMAND = "/usr/bin/xdg-open";
        String osName = Slang.getApplication().getSystemInfo().getOSName();
        if (osName.toLowerCase().equals("linux")) {
            try {
                File parentDir = f.getParentFile();
                String fname = f.getName();
                String cmd = "/bin/sh " + CROSS_DESKTOP_OPEN_FILE_COMMAND + " \"" + fname +"\"";
                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec(cmd, null, parentDir);
                p.waitFor();
            } catch (Exception e) {
                
            }
        }
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Find/Replace related">
    public void showFindReplaceDialog() {
        if (this.findReplaceDialog != null) {
            this.findReplaceDialog.showDialog(this.editorView1.getSelectedText());
        }
    }

    public void showSearchPanel() {
        this.jPanel12.setVisible(true);
        this.jSearchTextField1.requestFocusInWindow();
        this.doLightSearch();
    }

    public void hideSearchPanel() {
        this.jPanel12.setVisible(false);
        this.editorView1.clearSearchHighlights();
        this.editorView1.requestFocusOnEditor();
    }

    public void doLightSearch() {
        String pattern = this.jSearchTextField1.getText();
        if (pattern.equals("")) {
            this.editorView1.clearSearchHighlights();
        } else {
            this.editorView1.simplifiedSearch(pattern);
        }
    }

    public void findNext() {
        this.editorView1.findNext(false, true);
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="EditorViewContainer implementation">
    public void notifyDocumentModified(String title, String path, boolean modified) {
        frame.setTitle(title + " - " + applicationTitle);
        //this.jTabbedPane2.setToolTipTextAt(0, path);
        this.updateFullPathLabel(path);
        jLabel2.setEnabled(modified);
        enableDisableUndoRedo();
        enableDisableSave();
        if(this.codeInspector1!=null && this.editorView1!=null){
            this.codeInspector1.refresh(this.editorView1.getEditorPane().getText());
        }
    }

    public void notifyCaretChanged(CaretEvent e) {
        enableDisableTextManipulation();
        Point p = editorView1.getCaretPosition();
        updatePositionLabel(p.x + 1, p.y + 1);
    }

    public void notifyFirstUndoableEditHappened(UndoableEditEvent evt) {
        enableUndoControls();
    }

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CodeInspectorContainer implementation">

    public String getCode(){
        return this.editorView1.getEditorPane().getText();
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="DocumentContainer implementation">
    public boolean isCurrentDocModified() {
        return this.editorView1.isModified();
    }

    public void setCurrentDocModified(boolean modified) {
        this.editorView1.setModified(modified);
    }

    public boolean isCurrentDocNew() {
        return this.editorView1.isNewFile();
    }

    public File getCurrentDocFile() {
        return this.editorView1.getFile();
    }

    public String getCurrentDocTitle() {
        return this.editorView1.getTitle();
    }

    public boolean saveDocument(File output) {
        return this.editorView1.saveFile(output);
    }

    public boolean openDocument(File input) {
        if (input.exists()) {
            this.createDocument(input);
            return true;
        } else {
            JOptionPane.showMessageDialog(this.frame, "Το αρχείο:\n\t" + input.getAbsolutePath() + "\nδεν υπάρχει!", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean newDocument() {
        this.createDocument();
        return true;
    }

    public void recentlyAccessedFilesChanged() {
        JMenu menu = this.jMenu4;
        JPopupMenu popupMenu = this.jPopupMenu2;
        menu.removeAll();
        popupMenu.removeAll();
        File[] files = this.ioManager.getRecentlyAccessedFiles();
        for (File file : files) {
            final File f = file;
            if (f.exists()) {
                JMenuItem item = new JMenuItem(f.getName());
                JMenuItem popupItem = new JMenuItem(f.getName());
                item.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        open(f, FileUtils.detectCharset(f));
                    }
                });
                popupItem.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        open(f, FileUtils.detectCharset(f));
                    }
                });
                item.setIcon(this.glossaFileIcon);
                popupItem.setIcon(this.glossaFileIcon);
                menu.add(item);
                popupMenu.add(popupItem);
            }
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="ApplicationPreferencesListener Implementation">
    public void useSystemLAFChanged() {
    }

    public void crossPlatformLAFNameChanged() {
    }

    public void profilesManagerChanged() {
    }

    public void highlighterProfileChanged() {
        this.updateEditorColors();
    }

    public void highlighterProfileColorsChanged() {
        this.updateEditorColors();
    }

    public void editorFontChangedEvent() {
        this.updateEditorFont();
    }

    public void useSystemIconsChangedEvent(boolean useSystemIcons){
        Slang app = Slang.getApplication();
        app.getIconManager().updateIcons(app.getSystemInfo(), useSystemIcons);
        this.findReplaceDialog.iconsChangedEvent();
        this.ooid.iconsChangedEvent();
        this.jFileBrowserPanel1.iconsChangedEvent();
        app.getIconLoader().loadIcons();
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Application exit event handling">
    public boolean canExit(java.util.EventObject e) {
        /*Object source = (e != null) ? e.getSource() : null;
        Component owner = (source instanceof Component) ? (Component) source : null;*/
        return queryCloseApp();
    }

    public void willExit(java.util.EventObject e) {
        this.getApplication().removeExitListener(this);
    }

    public boolean queryCloseApp() {
        if (this.editorView1.isModified()) {
            int res = ioManager.showModifiedWarning(frame, this.editorView1.getTitle());
            if (res == JOptionPane.YES_OPTION) {
                if (save()) {
                    return true;
                }
                return false;
            } else if (res == JOptionPane.NO_OPTION) {
                this.editorView1.setModified(false);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //</editor-fold>
    public void exit() {
        this.getApplication().exit();
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = Slang.getApplication().getMainFrame();
            aboutBox = new SlangAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Slang.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jSeparator18 = new javax.swing.JToolBar.Separator();
        jButton10 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        runtimeInspector1 = new glossaeditor.ui.components.runtimeinspector.RuntimeInspector();
        jPanel13 = new javax.swing.JPanel();
        codeInspector1 = new glossaeditor.ui.components.codeinspector.CodeInspector();
        jCommandsListPanel1 = new glossaeditor.ui.components.commandslist.JCommandsListPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jSearchTextField1 = new glossaeditor.ui.components.misc.JSearchTextField();
        jPanel11 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel9 = new javax.swing.JPanel();
        cliInterface1 = new glossaeditor.ui.components.cli.CliInterface();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jToolBar2 = new javax.swing.JToolBar();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem27 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        helpMenu = new javax.swing.JMenu();
        jMenuItem23 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        jMenuItem24 = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator17 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();

        mainPanel.setName("mainPanel"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(GlossaEditorView.class);
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

        jButton14.setIcon(resourceMap.getIcon("jButton14.icon")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setToolTipText(resourceMap.getString("jButton14.toolTipText")); // NOI18N
        jButton14.setFocusable(false);
        jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton14.setName("jButton14"); // NOI18N
        jButton14.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton14MousePressed(evt);
            }
        });
        jToolBar1.add(jButton14);

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

        jButton8.setIcon(resourceMap.getIcon("jButton8.icon")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setToolTipText(resourceMap.getString("jButton8.toolTipText")); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton8);

        jButton9.setIcon(resourceMap.getIcon("jButton9.icon")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setToolTipText(resourceMap.getString("jButton9.toolTipText")); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton9);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar1.add(jSeparator3);

        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setToolTipText(resourceMap.getString("jButton5.toolTipText")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setToolTipText(resourceMap.getString("jButton6.toolTipText")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton7.setIcon(resourceMap.getIcon("jButton7.icon")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setToolTipText(resourceMap.getString("jButton7.toolTipText")); // NOI18N
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setName("jButton7"); // NOI18N
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton7);

        jSeparator12.setName("jSeparator12"); // NOI18N
        jToolBar1.add(jSeparator12);

        jButton15.setIcon(resourceMap.getIcon("jButton15.icon")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setToolTipText(resourceMap.getString("jButton15.toolTipText")); // NOI18N
        jButton15.setFocusable(false);
        jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton15.setName("jButton15"); // NOI18N
        jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton15);

        jButton16.setIcon(resourceMap.getIcon("jButton16.icon")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setToolTipText(resourceMap.getString("jButton16.toolTipText")); // NOI18N
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setName("jButton16"); // NOI18N
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton16);

        jSeparator18.setName("jSeparator18"); // NOI18N
        jToolBar1.add(jSeparator18);

        jButton10.setIcon(resourceMap.getIcon("jButton10.icon")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setToolTipText(resourceMap.getString("jButton10.toolTipText")); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton10);

        jPanel1.setBorder(null);
        jPanel1.setName("jPanel1"); // NOI18N

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(260);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setDoubleBuffered(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel2.setBorder(null);
        jPanel2.setName("jPanel2"); // NOI18N

        jSplitPane2.setBorder(null);
        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setDoubleBuffered(true);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel3.setBorder(null);
        jPanel3.setName("jPanel3"); // NOI18N

        jTabbedPane3.setDoubleBuffered(true);
        jTabbedPane3.setMinimumSize(new java.awt.Dimension(174, 0));
        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N

        runtimeInspector1.setMinimumSize(new java.awt.Dimension(139, 0));
        runtimeInspector1.setName("runtimeInspector1"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(runtimeInspector1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(runtimeInspector1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab(resourceMap.getString("jPanel10.TabConstraints.tabTitle"), jPanel10); // NOI18N

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));
        jTabbedPane3.addTab(resourceMap.getString("jPanel13.TabConstraints.tabTitle"), jPanel13); // NOI18N

        codeInspector1.setName("codeInspector1"); // NOI18N
        jTabbedPane3.addTab(resourceMap.getString("codeInspector1.TabConstraints.tabTitle"), codeInspector1); // NOI18N

        jCommandsListPanel1.setName("jCommandsListPanel1"); // NOI18N
        jTabbedPane3.addTab(resourceMap.getString("jCommandsListPanel1.TabConstraints.tabTitle"), jCommandsListPanel1); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel3);

        jPanel5.setBorder(null);
        jPanel5.setName("jPanel5"); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));

        jPanel12.setName("jPanel12"); // NOI18N

        jButton17.setIcon(resourceMap.getIcon("jButton17.icon")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setToolTipText(resourceMap.getString("jButton17.toolTipText")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setIcon(resourceMap.getIcon("jButton18.icon")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setToolTipText(resourceMap.getString("jButton18.toolTipText")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jSearchTextField1.setText(resourceMap.getString("jSearchTextField1.text")); // NOI18N
        jSearchTextField1.setName("jSearchTextField1"); // NOI18N
        jSearchTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jSearchTextField1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addComponent(jSearchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton17)
                    .addComponent(jButton18)
                    .addComponent(jSearchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );

        jSplitPane1.setTopComponent(jPanel2);

        jPanel11.setBorder(null);
        jPanel11.setName("jPanel11"); // NOI18N

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setDoubleBuffered(true);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(118, 0));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel4.setBorder(null);
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane1.setDoubleBuffered(true);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setDoubleBuffered(true);
        jList1.setName("jList1"); // NOI18N
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel9.setBorder(null);
        jPanel9.setName("jPanel9"); // NOI18N

        cliInterface1.setName("cliInterface1"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cliInterface1, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cliInterface1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel9.TabConstraints.tabTitle"), jPanel9); // NOI18N

        jPanel6.setBorder(null);
        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane3.setDoubleBuffered(true);
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setDoubleBuffered(true);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane3.setViewportView(jTextArea1);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        jButton11.setIcon(resourceMap.getIcon("jButton11.icon")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setName("jButton11"); // NOI18N
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton11);

        jButton12.setIcon(resourceMap.getIcon("jButton12.icon")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setFocusable(false);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setName("jButton12"); // NOI18N
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton12);

        jButton13.setIcon(resourceMap.getIcon("jButton13.icon")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setName("jButton13"); // NOI18N
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton13);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel11);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        menuBar.setDoubleBuffered(true);
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setIcon(resourceMap.getIcon("jMenuItem8.icon")); // NOI18N
        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem8);

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setIcon(resourceMap.getIcon("jMenuItem9.icon")); // NOI18N
        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem9);

        jMenu4.setIcon(resourceMap.getIcon("jMenu4.icon")); // NOI18N
        jMenu4.setText(resourceMap.getString("jMenu4.text")); // NOI18N
        jMenu4.setName("jMenu4"); // NOI18N
        fileMenu.add(jMenu4);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem10.setIcon(resourceMap.getIcon("jMenuItem10.icon")); // NOI18N
        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem10);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setIcon(resourceMap.getIcon("jMenuItem11.icon")); // NOI18N
        jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem11);

        jSeparator15.setName("jSeparator15"); // NOI18N
        fileMenu.add(jSeparator15);

        jMenu2.setIcon(resourceMap.getIcon("jMenu2.icon")); // NOI18N
        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem27.setIcon(resourceMap.getIcon("jMenuItem27.icon")); // NOI18N
        jMenuItem27.setText(resourceMap.getString("jMenuItem27.text")); // NOI18N
        jMenuItem27.setName("jMenuItem27"); // NOI18N
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem27);

        fileMenu.add(jMenu2);

        jSeparator6.setName("jSeparator6"); // NOI18N
        fileMenu.add(jSeparator6);

        jMenuItem12.setIcon(resourceMap.getIcon("jMenuItem12.icon")); // NOI18N
        jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem12);

        jSeparator7.setName("jSeparator7"); // NOI18N
        fileMenu.add(jSeparator7);

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem13.setIcon(resourceMap.getIcon("jMenuItem13.icon")); // NOI18N
        jMenuItem13.setText(resourceMap.getString("jMenuItem13.text")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem13);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem14.setIcon(resourceMap.getIcon("jMenuItem14.icon")); // NOI18N
        jMenuItem14.setText(resourceMap.getString("jMenuItem14.text")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem14);

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem15.setIcon(resourceMap.getIcon("jMenuItem15.icon")); // NOI18N
        jMenuItem15.setText(resourceMap.getString("jMenuItem15.text")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem15);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jMenu1.add(jSeparator8);

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setIcon(resourceMap.getIcon("jMenuItem16.icon")); // NOI18N
        jMenuItem16.setText(resourceMap.getString("jMenuItem16.text")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem16);

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem17.setIcon(resourceMap.getIcon("jMenuItem17.icon")); // NOI18N
        jMenuItem17.setText(resourceMap.getString("jMenuItem17.text")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem17);

        jMenuItem18.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem18.setIcon(resourceMap.getIcon("jMenuItem18.icon")); // NOI18N
        jMenuItem18.setText(resourceMap.getString("jMenuItem18.text")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem18);

        jMenuItem19.setIcon(resourceMap.getIcon("jMenuItem19.icon")); // NOI18N
        jMenuItem19.setText(resourceMap.getString("jMenuItem19.text")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem19);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jMenu1.add(jSeparator9);

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem20.setIcon(resourceMap.getIcon("jMenuItem20.icon")); // NOI18N
        jMenuItem20.setText(resourceMap.getString("jMenuItem20.text")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem20);

        jSeparator10.setName("jSeparator10"); // NOI18N
        jMenu1.add(jSeparator10);

        jMenuItem21.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem21.setIcon(resourceMap.getIcon("jMenuItem21.icon")); // NOI18N
        jMenuItem21.setText(resourceMap.getString("jMenuItem21.text")); // NOI18N
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem21);

        jMenuItem26.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem26.setText(resourceMap.getString("jMenuItem26.text")); // NOI18N
        jMenuItem26.setName("jMenuItem26"); // NOI18N
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem26);

        jMenuItem22.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem22.setIcon(resourceMap.getIcon("jMenuItem22.icon")); // NOI18N
        jMenuItem22.setText(resourceMap.getString("jMenuItem22.text")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem22);

        jSeparator14.setName("jSeparator14"); // NOI18N
        jMenu1.add(jSeparator14);

        jMenuItem25.setIcon(resourceMap.getIcon("jMenuItem25.icon")); // NOI18N
        jMenuItem25.setText(resourceMap.getString("jMenuItem25.text")); // NOI18N
        jMenuItem25.setName("jMenuItem25"); // NOI18N
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem25);

        menuBar.add(jMenu1);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem28.setIcon(resourceMap.getIcon("jMenuItem28.icon")); // NOI18N
        jMenuItem28.setText(resourceMap.getString("jMenuItem28.text")); // NOI18N
        jMenuItem28.setName("jMenuItem28"); // NOI18N
        jMenu3.add(jMenuItem28);

        jMenuItem29.setIcon(resourceMap.getIcon("jMenuItem29.icon")); // NOI18N
        jMenuItem29.setText(resourceMap.getString("jMenuItem29.text")); // NOI18N
        jMenuItem29.setName("jMenuItem29"); // NOI18N
        jMenu3.add(jMenuItem29);

        jSeparator16.setName("jSeparator16"); // NOI18N
        jMenu3.add(jSeparator16);

        jCheckBoxMenuItem1.setText(resourceMap.getString("jCheckBoxMenuItem1.text")); // NOI18N
        jCheckBoxMenuItem1.setName("jCheckBoxMenuItem1"); // NOI18N
        jMenu3.add(jCheckBoxMenuItem1);

        menuBar.add(jMenu3);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem23.setIcon(resourceMap.getIcon("jMenuItem23.icon")); // NOI18N
        jMenuItem23.setText(resourceMap.getString("jMenuItem23.text")); // NOI18N
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        helpMenu.add(jMenuItem23);

        jSeparator11.setName("jSeparator11"); // NOI18N
        helpMenu.add(jSeparator11);

        jMenuItem24.setIcon(resourceMap.getIcon("jMenuItem24.icon")); // NOI18N
        jMenuItem24.setText(resourceMap.getString("jMenuItem24.text")); // NOI18N
        jMenuItem24.setName("jMenuItem24"); // NOI18N
        helpMenu.add(jMenuItem24);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator13.setName("jSeparator13"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);
        jLabel2.setName("jLabel2"); // NOI18N

        jSeparator17.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator17.setName("jSeparator17"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(578, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                    .addComponent(jSeparator17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                    .addComponent(jSeparator13, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        jMenuItem1.setIcon(resourceMap.getIcon("jMenuItem1.icon")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setIcon(resourceMap.getIcon("jMenuItem2.icon")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jPopupMenu1.add(jSeparator4);

        jMenuItem3.setIcon(resourceMap.getIcon("jMenuItem3.icon")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem3);

        jMenuItem4.setIcon(resourceMap.getIcon("jMenuItem4.icon")); // NOI18N
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem4);

        jMenuItem5.setIcon(resourceMap.getIcon("jMenuItem5.icon")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem5);

        jMenuItem6.setIcon(resourceMap.getIcon("jMenuItem6.icon")); // NOI18N
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem6);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jPopupMenu1.add(jSeparator5);

        jMenuItem7.setIcon(resourceMap.getIcon("jMenuItem7.icon")); // NOI18N
        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem7);

        jPopupMenu2.setName("jPopupMenu2"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    if (jButton2.isEnabled()) {
        open();
    }
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    if (jButton1.isEnabled()) {
        createNew();
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

    if (jButton8.isEnabled()) {
        undo();
    }
}//GEN-LAST:event_jButton8ActionPerformed

private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

    if (jButton9.isEnabled()) {
        redo();
    }
}//GEN-LAST:event_jButton9ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    if (jButton3.isEnabled()) {
        save();
    }
}//GEN-LAST:event_jButton3ActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

    if (jMenuItem1.isEnabled()) {
        undo();
    }
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

    if (jMenuItem2.isEnabled()) {
        redo();
    }
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

    if (jButton5.isEnabled()) {
        cut();
    }
}//GEN-LAST:event_jButton5ActionPerformed

private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

    if (jButton6.isEnabled()) {
        copy();
    }
}//GEN-LAST:event_jButton6ActionPerformed

private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

    if (jButton7.isEnabled()) {
        paste();
    }
}//GEN-LAST:event_jButton7ActionPerformed

private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

    if (jMenuItem3.isEnabled()) {
        cut();
    }
}//GEN-LAST:event_jMenuItem3ActionPerformed

private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

    if (jMenuItem4.isEnabled()) {
        copy();
    }
}//GEN-LAST:event_jMenuItem4ActionPerformed

private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed

    if (jMenuItem5.isEnabled()) {
        paste();
    }
}//GEN-LAST:event_jMenuItem5ActionPerformed

private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed

    if (jMenuItem6.isEnabled()) {
        deleteSelection();
    }
}//GEN-LAST:event_jMenuItem6ActionPerformed

private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed

    if (jMenuItem7.isEnabled()) {
        selectAll();
    }
}//GEN-LAST:event_jMenuItem7ActionPerformed

private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed

    if (jMenuItem7.isEnabled()) {
        exit();
    }
}//GEN-LAST:event_jMenuItem13ActionPerformed

private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed

    if (jMenuItem8.isEnabled()) {
        createNew();
    }
}//GEN-LAST:event_jMenuItem8ActionPerformed

private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

    if (jMenuItem9.isEnabled()) {
        open();
    }
}//GEN-LAST:event_jMenuItem9ActionPerformed

private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed

    if (jMenuItem10.isEnabled()) {
        save();
    }
}//GEN-LAST:event_jMenuItem10ActionPerformed

private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed

    if (jMenuItem11.isEnabled()) {
        saveAs();
    }
}//GEN-LAST:event_jMenuItem11ActionPerformed

private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed

    if (jMenuItem14.isEnabled()) {
        undo();
    }
}//GEN-LAST:event_jMenuItem14ActionPerformed

private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed

    if (jMenuItem15.isEnabled()) {
        redo();
    }
}//GEN-LAST:event_jMenuItem15ActionPerformed

private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed

    if (jMenuItem16.isEnabled()) {
        cut();
    }
}//GEN-LAST:event_jMenuItem16ActionPerformed

private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed

    if (jMenuItem17.isEnabled()) {
        copy();
    }
}//GEN-LAST:event_jMenuItem17ActionPerformed

private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed

    if (jMenuItem18.isEnabled()) {
        paste();
    }
}//GEN-LAST:event_jMenuItem18ActionPerformed

private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed

    if (jMenuItem19.isEnabled()) {
        deleteSelection();
    }
}//GEN-LAST:event_jMenuItem19ActionPerformed

private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed

    if (jMenuItem20.isEnabled()) {
        selectAll();
    }
}//GEN-LAST:event_jMenuItem20ActionPerformed

private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed

    /*if (jMenuItem21.isEnabled()) {
    this.showFindReplaceDialog();
    }*/
    this.showSearchPanel();
}//GEN-LAST:event_jMenuItem21ActionPerformed

private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed

    if (jMenuItem22.isEnabled()) {
        this.showFindReplaceDialog();
    }
}//GEN-LAST:event_jMenuItem22ActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    if (jButton4.isEnabled()) {
        //editorView1.print();
        this.printDocument();
    }

}//GEN-LAST:event_jButton4ActionPerformed

private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

    if (jButton10.isEnabled()) {
        showHelpContents();
    }
}//GEN-LAST:event_jButton10ActionPerformed

private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed

    if (jMenuItem25.isEnabled()) {
        showPreferencesDialog();
    }

}//GEN-LAST:event_jMenuItem25ActionPerformed

private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
    if (jMenuItem12.isEnabled()) {
        //editorView1.print();
        this.printDocument();
    }
}//GEN-LAST:event_jMenuItem12ActionPerformed

private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
    if (jMenuItem23.isEnabled()) {
        showHelpContents();
    }
}//GEN-LAST:event_jMenuItem23ActionPerformed

private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed

    if (jMenuItem26.isEnabled()) {
        findNext();
    }
}//GEN-LAST:event_jMenuItem26ActionPerformed

private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
    exportCurrentAsHtml();
}//GEN-LAST:event_jMenuItem27ActionPerformed

private void jButton14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton14MousePressed
    this.jPopupMenu2.show(this.jButton14, 0, this.jButton14.getHeight());
}//GEN-LAST:event_jButton14MousePressed

private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
    this.hideSearchPanel();
}//GEN-LAST:event_jButton17ActionPerformed

private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
    this.findNext();
}//GEN-LAST:event_jButton18ActionPerformed

private void jSearchTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSearchTextField1KeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        this.findNext();
    } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
        this.hideSearchPanel();
    }
}//GEN-LAST:event_jSearchTextField1KeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private glossaeditor.ui.components.cli.CliInterface cliInterface1;
    private glossaeditor.ui.components.codeinspector.CodeInspector codeInspector1;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private glossaeditor.ui.components.commandslist.JCommandsListPanel jCommandsListPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private glossaeditor.ui.components.misc.JSearchTextField jSearchTextField1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JToolBar.Separator jSeparator18;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private glossaeditor.ui.components.runtimeinspector.RuntimeInspector runtimeInspector1;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
}

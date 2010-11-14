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
import glossa.interpreter.Interpreter;
import glossa.interpreter.InterpreterListener;
import glossa.interpreter.symboltable.SymbolTable;
import glossaeditor.Slang;
import glossaeditor.ui.components.editor.EditorViewContainer;
import glossaeditor.ui.components.editor.EditorView;
import glossaeditor.ui.filefilters.GlossaFileFilter;
import glossaeditor.ui.filefilters.HtmlFileFilter;
import glossaeditor.ui.components.filepane.JFileBrowserPanel;
import glossaeditor.export.htmlgenerator.HtmlGenerator;
import glossaeditor.integration.FileDrop;
import glossaeditor.preferences.ApplicationPreferences;
import glossaeditor.preferences.ApplicationPreferencesListener;
import glossaeditor.util.FileUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.event.DocumentEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.Highlighter;
import org.jdesktop.application.Application.ExitListener;

/**
 * The application's main frame.
 */
public class GlossaEditorView extends FrameView implements EditorViewContainer, DocumentContainer, ApplicationPreferencesListener, ExitListener, InterpreterListener  {

    private final String applicationTitle = "Περιβάλλον ανάπτυξης για τη ΓΛΩΣΣΑ";
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
    private Interpreter interpreter;
    private boolean runInteractively;
    private int interpreterDelayBetweenSteps;
    private boolean running;
    private JTextArea tmp;

    private final Color EXEC_HIGHLIGHT_COLOR = new Color(217, 255, 126);
    private Object executionHighlight;

    public GlossaEditorView(SingleFrameApplication app, String arg, ApplicationPreferences appPrefs) {
        super(app);

        this.appPrefs = appPrefs;

        preInit();
        initComponents();
        postInit(arg);
    }

    // <editor-fold defaultstate="collapsed" desc="Code execution">


    public int setExecHighlight(int line) {
        removeExecHighlight();
        this.tmp.setText(this.editorView1.getEditorPane().getText());
        try {
            int start = tmp.getLineStartOffset(line);
            int end = tmp.getLineEndOffset(line);
            this.executionHighlight = this.editorView1.highlight(start, end, this.EXEC_HIGHLIGHT_COLOR);
            try{
                this.editorView1.getEditorPane().setCaretPosition(start);
            }catch(Exception e1){
                
            }
            return start;
        } catch (Exception e) {
            return -1;
        }

    }

    public void removeExecHighlight() {
        if ((this.editorView1 != null) && (this.executionHighlight != null)) {
            Highlighter h = this.editorView1.getEditorPane().getHighlighter();
            h.removeHighlight(this.executionHighlight);
        }
    }

    private void run(){
        jButton16.setEnabled(false);
        if(running){
            interpreter.resume();
        }else{
            runCurrentFile();
        }
    }

    private void stop(){
        if(interpreter!=null){
            interpreter.stop();
        }
    }


    public void parsingAndSemanticAnalysisFinished(boolean success) {

    }

    public void runtimeError() {
    }

    public void executionStarted(Interpreter sender) {
        running = true;
        this.editorView1.getEditorPane().setEditable(false);
        this.jButton16.setEnabled(false);
        this.jButton20.setEnabled(true);
        this.jCheckBox1.setEnabled(false);
        this.jCheckBox2.setEnabled(false);
        this.jSpinner1.setEnabled(false);
        this.jLabel4.setEnabled(false);
        this.jLabel5.setEnabled(false);
    }

    public void executionStopped(Interpreter sender) {
        running = false;
        this.editorView1.getEditorPane().setEditable(true);
        this.jButton16.setEnabled(true);
        this.jButton20.setEnabled(false);
        this.jCheckBox1.setEnabled(true);
        this.jCheckBox2.setEnabled(true);
        this.jSpinner1.setEnabled(true);
        this.jLabel4.setEnabled(true);
        this.jLabel5.setEnabled(true);

        removeExecHighlight();
    }

    public void readStatementExecuted(Interpreter sender, Integer line) {
        if(runInteractively || (interpreterDelayBetweenSteps>0)){
            setExecHighlight(line.intValue()-1);
        }
    }

    public void executionPaused(Interpreter sender, Integer line, Boolean wasPrintStatement) {

        if(runInteractively || (interpreterDelayBetweenSteps>0)){
            setExecHighlight(line.intValue()-1);
        }

        if(!runInteractively){
            if(interpreterDelayBetweenSteps>0){
                final Interpreter inter = sender;
                Thread t = new Thread(new Runnable() {

                    public void run() {
                        try{
                            Thread.sleep(interpreterDelayBetweenSteps*1000);
                        }catch(InterruptedException e){
                        }
                        inter.resume();
                    }
                });
                t.start();
            }else{
                sender.resume();
            }
        }else{
            jButton16.setEnabled(true);
        }
    }

    public void stackPopped() {
    }

    public void stackPushed(SymbolTable newSymbolTable) {
    }

    public void runCurrentFile() {
        File tmpFile = null;
        try{
            tmpFile = File.createTempFile(this.editorView1.getTitle(), ".gls");
            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8");
            BufferedWriter bw = new BufferedWriter(w);
            bw.write(editorView1.getEditorPane().getText());

            bw.flush();
            bw.close();
            w.close();
        }catch(IOException ioe){
            //TODO: report error
        }
        if (tmpFile != null) {
            runInterpreter(tmpFile);
        }
    }

    public void runInterpreter(File f) {

        removeExecHighlight();

        if(this.interpreter!=null){
            interpreter.removeListener(this);
            interpreter.removeListener(jGlossaStackPanel1.getStackRenderer());
            jInterpreterMessagesList1.setInterpreter(null);
        }

        this.runInteractively = jCheckBox1.isSelected();
        this.interpreterDelayBetweenSteps = ((Integer) this.jSpinner1.getModel().getValue()).intValue();

        jRuntimeWindow1.clear();
        jInterpreterMessagesList1.clear();
        jTabbedPane1.setSelectedIndex(0);
        InputStream in;
        if(jCheckBox2.isSelected()){
            in = new ByteArrayInputStream(this.jTextArea1.getText().getBytes());
        }else{
            in = jRuntimeWindowInputPanel1.getInputStream();
        }
        interpreter = new Interpreter(f, System.out, System.err, jRuntimeWindow1.getOut(), jRuntimeWindow1.getErr(), in);
        interpreter.addListener(this);
        jInterpreterMessagesList1.setInterpreter(interpreter);
        boolean success = interpreter.parseAndAnalyzeSemantics(true);
        if(success){
            jTabbedPane1.setSelectedIndex(1);
             if(runInteractively||(interpreterDelayBetweenSteps>0)){
                interpreter.addListener(jGlossaStackPanel1.getStackRenderer());
                jTabbedPane3.setSelectedIndex(2);
            }
            Thread t = new Thread(interpreter);
            t.start();
        }else{
            jButton16.setEnabled(true);
        }
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Initialization">
    public final void preInit() {
        System.setProperty("suppressSwingDropSupport", "True");
        this.getApplication().addExitListener(this);
        this.frame = this.getFrame();
        this.setApplicationIcon();
        this.newDocumentsCounter = 0;
        this.frame.setTitle(applicationTitle);

        this.interpreter = null;
        this.runInteractively = false;
        this.interpreterDelayBetweenSteps = 0;
        this.running = false;

        this.ioManager = new DocumentIOManager(this, Slang.class);
        this.ioManager.setConfirmOverwriteMessage("Το αρχείο:\n    %filename%\nυπάρχει ήδη!\n\nΘέλετε να γίνει επανεγγραφή;");
        this.ioManager.setOverwriteDialogTitle("Επανεγγραφή αρχείου");
        this.ioManager.setFileModifiedWarningMessage("Το αρχείο:\n    %filename%\nέχει αλλάξει!\n\nΘέλετε να αποθηκευθούν οι αλλαγές;");
        this.ioManager.setFileModifiedWarningDialogTitle("Αποθήκευση αλλαγών");

        this.glossaFileIcon = loadGlossaFileIcon();

    }

    
    public final void postInit(String arg) {

        this.tmp = new JTextArea();
        this.createDocument();
        this.ioManager.loadRecentlyAccessed();

        this.updateEditorFont();
        this.updateEditorColors();

        jRuntimeWindowInputPanel1.setWindow(jRuntimeWindow1);
        jRuntimeWindowInputPanel1.setSubmitButtonText("Υποβολή");
        jRuntimeWindowInputPanel1.setTextFieldNotFocusedText("Πληκτρολογήστε εδώ");

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

        this.jPanel12.setVisible(false);
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

    // <editor-fold defaultstate="collapsed" desc="Icon loading">
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

    // <editor-fold defaultstate="collapsed" desc="Input File I/O">
    public void openInputFile(){
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(frame);
        if(result==JFileChooser.APPROVE_OPTION){
            jTextArea1.setText("");
            try{
                BufferedReader r = new BufferedReader(new FileReader(fc.getSelectedFile()));
                String buffer = "";
                while( (buffer=r.readLine())!=null){
                    jTextArea1.append(buffer+"\n");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveInputFile(){
        JFileChooser fc = new JFileChooser();
        int result = fc.showSaveDialog(frame);
        if(result==JFileChooser.APPROVE_OPTION){
            try{
                BufferedWriter w = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                w.write(jTextArea1.getText());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void clearInputFile(){
        jTextArea1.setText("");
    }
    // </editor-fold>
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
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Application exit event handling">
    public boolean canExit(java.util.EventObject e) {
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
        jButton10 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel13 = new javax.swing.JPanel();
        jCommandsListPanel1 = new glossaeditor.ui.components.commandslist.JCommandsListPanel();
        jGlossaStackPanel1 = new glossa.ui.gui.stackrenderer.JGlossaStackPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jSearchTextField1 = new glossaeditor.ui.components.misc.JSearchTextField();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jInterpreterMessagesList1 = new glossa.ui.gui.io.JInterpreterMessagesList();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jRuntimeWindow1 = new glossa.ui.gui.io.JRuntimeWindow();
        jRuntimeWindowInputPanel1 = new glossa.ui.gui.io.JRuntimeWindowInputPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jToolBar2 = new javax.swing.JToolBar();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jButton16 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jSeparator18 = new javax.swing.JToolBar.Separator();
        jCheckBox1 = new javax.swing.JCheckBox();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        jLabel4 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        jCheckBox2 = new javax.swing.JCheckBox();
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

        jButton10.setIcon(resourceMap.getIcon("jButton10.icon")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setToolTipText(resourceMap.getString("jButton10.toolTipText")); // NOI18N
        jButton10.setEnabled(false);
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

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jSplitPane2.setDividerLocation(500);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setDoubleBuffered(true);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane3.setDoubleBuffered(true);
        jTabbedPane3.setMinimumSize(new java.awt.Dimension(174, 0));
        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13, javax.swing.BoxLayout.LINE_AXIS));
        jTabbedPane3.addTab(resourceMap.getString("jPanel13.TabConstraints.tabTitle"), jPanel13); // NOI18N

        jCommandsListPanel1.setName("jCommandsListPanel1"); // NOI18N
        jTabbedPane3.addTab(resourceMap.getString("jCommandsListPanel1.TabConstraints.tabTitle"), jCommandsListPanel1); // NOI18N

        jGlossaStackPanel1.setName("jGlossaStackPanel1"); // NOI18N
        jTabbedPane3.addTab(resourceMap.getString("jGlossaStackPanel1.TabConstraints.tabTitle"), jGlossaStackPanel1); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel3);

        jSplitPane3.setDividerLocation(200);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setResizeWeight(1.0);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setDoubleBuffered(true);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

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
                .addContainerGap(198, Short.MAX_VALUE)
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
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
        );

        jSplitPane3.setLeftComponent(jPanel5);

        jPanel2.setName("jPanel2"); // NOI18N

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane1.setDoubleBuffered(true);
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(118, 0));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jInterpreterMessagesList1.setName("jInterpreterMessagesList1"); // NOI18N
        jScrollPane1.setViewportView(jInterpreterMessagesList1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel9.setName("jPanel9"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jRuntimeWindow1.setName("jRuntimeWindow1"); // NOI18N
        jScrollPane2.setViewportView(jRuntimeWindow1);

        jRuntimeWindowInputPanel1.setName("jRuntimeWindowInputPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jRuntimeWindowInputPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRuntimeWindowInputPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel9.TabConstraints.tabTitle"), jPanel9); // NOI18N

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
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton11);

        jButton12.setIcon(resourceMap.getIcon("jButton12.icon")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setFocusable(false);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setName("jButton12"); // NOI18N
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton12);

        jButton13.setIcon(resourceMap.getIcon("jButton13.icon")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setName("jButton13"); // NOI18N
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton13);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        jButton16.setIcon(resourceMap.getIcon("jButton16.icon")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setToolTipText(resourceMap.getString("jButton16.toolTipText")); // NOI18N
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setName("jButton16"); // NOI18N
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton16);

        jButton20.setIcon(resourceMap.getIcon("jButton20.icon")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setEnabled(false);
        jButton20.setFocusable(false);
        jButton20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton20.setName("jButton20"); // NOI18N
        jButton20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton20);

        jSeparator18.setName("jSeparator18"); // NOI18N
        jToolBar3.add(jSeparator18);

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setFocusable(false);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jToolBar3.add(jCheckBox1);

        jSeparator19.setName("jSeparator19"); // NOI18N
        jToolBar3.add(jSeparator19);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jToolBar3.add(jLabel4);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10, 1));
        jSpinner1.setName("jSpinner1"); // NOI18N
        jToolBar3.add(jSpinner1);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jToolBar3.add(jLabel5);

        jSeparator16.setName("jSeparator16"); // NOI18N
        jToolBar3.add(jSeparator16);

        jCheckBox2.setText(resourceMap.getString("jCheckBox2.text")); // NOI18N
        jCheckBox2.setFocusable(false);
        jCheckBox2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jCheckBox2.setName("jCheckBox2"); // NOI18N
        jToolBar3.add(jCheckBox2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
        );

        jSplitPane3.setRightComponent(jPanel2);

        jSplitPane2.setLeftComponent(jSplitPane3);

        jPanel1.add(jSplitPane2);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
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

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem23.setIcon(resourceMap.getIcon("jMenuItem23.icon")); // NOI18N
        jMenuItem23.setText(resourceMap.getString("jMenuItem23.text")); // NOI18N
        jMenuItem23.setEnabled(false);
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
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
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
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
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
                .addContainerGap(686, Short.MAX_VALUE))
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

private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
    if(jButton16.isEnabled()){
        run();
    }
}//GEN-LAST:event_jButton16ActionPerformed

private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
    if(jButton20.isEnabled()){
        stop();
    }
}//GEN-LAST:event_jButton20ActionPerformed

private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
    boolean enable = !jCheckBox1.isSelected();
    this.jSpinner1.setEnabled(enable);
    this.jLabel4.setEnabled(enable);
    this.jLabel5.setEnabled(enable);
}//GEN-LAST:event_jCheckBox1ActionPerformed

private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
    clearInputFile();
}//GEN-LAST:event_jButton11ActionPerformed

private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    openInputFile();
}//GEN-LAST:event_jButton12ActionPerformed

private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
    saveInputFile();
}//GEN-LAST:event_jButton13ActionPerformed

private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
    showAboutBox();
}//GEN-LAST:event_jMenuItem24ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private glossaeditor.ui.components.commandslist.JCommandsListPanel jCommandsListPanel1;
    private glossa.ui.gui.stackrenderer.JGlossaStackPanel jGlossaStackPanel1;
    private glossa.ui.gui.io.JInterpreterMessagesList jInterpreterMessagesList1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
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
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private glossa.ui.gui.io.JRuntimeWindow jRuntimeWindow1;
    private glossa.ui.gui.io.JRuntimeWindowInputPanel jRuntimeWindowInputPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private glossaeditor.ui.components.misc.JSearchTextField jSearchTextField1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JToolBar.Separator jSeparator18;
    private javax.swing.JToolBar.Separator jSeparator19;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
}

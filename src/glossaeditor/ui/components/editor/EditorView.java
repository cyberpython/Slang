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
 * EditorView.java
 *
 * Created on 21 Δεκέμβριος 2008, 7:50 μμ
 */
package glossaeditor.ui.components.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.SyntaxStyles;
import jsyntaxpane.TokenType;
import jsyntaxpane.lexers.GlossaLexer;
import jsyntaxpane.syntaxkits.GlossaSyntaxKit;

/**
 *
 * @author  cyberpython
 */
public class EditorView extends javax.swing.JPanel implements DocumentListener, CaretListener, UndoableEditListener {

    private boolean newFile;
    private boolean modified;
    private String Title;
    private File storage;
    private SyntaxDocument document;
    private EditorViewContainer container;
    private String UNTITLED;
    private JPopupMenu popupMenu;
    //TODO: FIXME
    private Vector<Object> searchHighlights;
    private LinkedList<Point> searchResults;
    private String lastSearch;
    private Hashtable<String, String> searchedFor;
    //TODO: FIXME
    private Hashtable<String, String> replacedWith;

    /** Creates new form EditorView */
    public EditorView() {
        preInit(null, 0, null);
        initComponents();
        postInit();
    }

    /** Creates new form EditorView */
    public EditorView(EditorViewContainer container) {
        preInit(null, 0, container);
        initComponents();
        postInit();
    }

    public EditorView(int documentCount, EditorViewContainer container) {
        preInit(null, documentCount, container);
        initComponents();
        postInit();
    }

    public EditorView(File input, EditorViewContainer container) {
        preInit(input, -1, container);
        initComponents();
        postInit(input);
    }

    private void preInit(File input, int documentCount, EditorViewContainer container) {

        UNTITLED = "Ανώνυμο";

        this.container = container;

        this.storage = input;
        if (storage == null) {
            newFile = true;
            Title = UNTITLED;// + "-" + String.valueOf(documentCount);
        } else {
            newFile = false;
            Title = storage.getName();
        }
        modified = false;

        this.searchHighlights = new Vector<Object>();
        this.searchResults = new LinkedList<Point>();

        this.lastSearch = null;

        this.searchedFor = new Hashtable<String, String>();
        this.replacedWith = new Hashtable<String, String>();
    }

    private void postInit() {
        if (container != null) {
            String hint = null;
            if (this.storage != null) {
                try {
                    hint = this.storage.getCanonicalPath();
                } catch (IOException ioe) {
                }
            }
            container.notifyDocumentModified(getTitleWithModificationIndicator(), hint, modified);
        }
    }

    private void postInit(File f) {
        this.storage = f;
        this.Title = storage.getName();
        this.newFile = false;


        if (container != null) {
            String hint = null;
            if (this.storage != null) {
                try {
                    hint = this.storage.getCanonicalPath();
                } catch (IOException ioe) {
                }
            }
            container.notifyDocumentModified(getTitleWithModificationIndicator(), hint, modified);
        }
    }

    public void initEditor(JPopupMenu popupmenu, Font f, String[] colors, Charset c) {
        //jEditorPane1.setTransferHandler(null);

        this.popupMenu = popupmenu;

        DefaultSyntaxKit.initKit();
        jEditorPane1.setContentType("text/glossa");
        jEditorPane1.setFont(f);

        if (this.isNewFile()) {
            createEmptyFile();
        } else {
            boolean openedSuccessfully = loadFile(this.storage, c);
            if (!openedSuccessfully) {
                this.storage = null;
                newFile = true;
                Title = UNTITLED;
                container.notifyDocumentModified(getTitleWithModificationIndicator(), null, false);
            }
        }

        this.setEditorColors(colors);

        jEditorPane1.getDocument().addDocumentListener(this);
        jEditorPane1.addCaretListener(this);
        this.document.setUndoLimit(-1);//unlimited undo actions
        jEditorPane1.getDocument().addUndoableEditListener(this);

        container.notifyCaretChanged(null);

        jEditorPane1.invalidate();

    }

    public void insertText(String text) {
        this.jEditorPane1.replaceSelection(text);
    }

    public Font getEditorFont() {
        return jEditorPane1.getFont();
    }

    public JEditorPane getEditorPane() {
        return this.jEditorPane1;
    }

    public SyntaxDocument getDocument() {
        return this.document;
    }

    public void requestFocusOnEditor() {
        this.jEditorPane1.requestFocus();
    }

    public void clearHighlights() {
        this.jEditorPane1.getHighlighter().removeAllHighlights();
    }

    public void highlightCurrentLine(Color highlightColor) {

        int position = jEditorPane1.getCaretPosition();

        int lineStart = document.getLineStartOffset(position);
        int lineEnd = document.getLineEndOffset(position);

        DefaultHighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

        try {
            this.jEditorPane1.getHighlighter().addHighlight(lineStart, lineEnd, redPainter);
        } catch (BadLocationException ble) {
        }
    }

    public Object highlight(int start, int end, Color highlightColor) {

        DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

        try {
            Object o = this.jEditorPane1.getHighlighter().addHighlight(start, end, painter);
            this.jEditorPane1.repaint();
            return o;
        } catch (BadLocationException ble) {
            return null;
        }
    }

    public void clearSearchHighlights() {
        Highlighter h = this.getEditorPane().getHighlighter();
        for (int i = 0; i < searchHighlights.size(); i++) {
            h.removeHighlight(searchHighlights.get(i));
        }
        searchHighlights.clear();
    }

    public void clearSearchResults() {
        this.searchResults.clear();
    }

    public String putSearchedForItem(String key, String object) {
        return this.searchedFor.put(key, object);
    }

    public String putReplacedWithItem(String key, String object) {
        return this.replacedWith.put(key, object);
    }

    public void findNext(boolean matchCase, boolean cycle) {
        search(this.lastSearch, cycle, matchCase);
    }

    public long search(String pattern, boolean cycle, boolean caseSensitive) {

        long totalFound = 0;

        if ((pattern == null) || (pattern.equals(""))) {
            return 0;
        }

        JEditorPane pane = this.getEditorPane();

        searchResults.clear();

        String text = null;
        if (caseSensitive) {
            text = pane.getText();
        } else {
            pattern = pattern.toLowerCase();
            text = pane.getText().toLowerCase();
        }

        lastSearch = pattern;

        int pos = this.getSelectionStart();
        if ((lastSearch != null) && (lastSearch.equals(pattern))) {
            pos = pos + 1;
        }

        int initialPos = pos;
        int a = 0;
        int b = 0;
        int l = pattern.length();
        int end = text.length();

        boolean found = false;

        while (pos < end) {
            a = text.indexOf(pattern, pos);
            if (a != -1) {
                b = a + l;
                pos = b;
                found = true;
                searchResults.addLast(new Point(a, b));
                totalFound++;
            } else {
                pos = end;
            }
        }


        pos = 0;
        while (pos < initialPos) {
            a = text.indexOf(pattern, pos);
            if (a != -1) {
                b = a + l;
                pos = b;
                searchResults.addLast(new Point(a, b));
                totalFound++;
                if (cycle) {
                    found = true;
                }
            } else {
                pos = initialPos;
            }
        }


        highlightSearch(found);
        return totalFound;

    }

    public void highlightSearch(boolean found) {
        clearSearchHighlights();

        JEditorPane pane = this.getEditorPane();

        Point p;
        if (searchResults.size() > 0) {
            p = searchResults.get(0);
            if (found) {
                pane.setCaretPosition(p.y);
                pane.setSelectionStart(p.x);
                pane.setSelectionEnd(p.y);
            } else {
                searchHighlights.add(this.highlight(p.x, p.y, Color.yellow));
            }
        }


        for (int i = 1; i < searchResults.size(); i++) {
            p = searchResults.get(i);
            searchHighlights.add(this.highlight(p.x, p.y, Color.yellow));
        }
    }

    public long simplifiedSearch(String pattern) {
        if ((pattern == null) || (pattern.trim().equals(""))) {
            return 0;
        }
        return search(pattern, true, false);
    }

    public void print() {
        ((JGlossaEditorPane) jEditorPane1).printContents();

    }

    public Color getKeywordColor() {

        try {
            return SyntaxStyles.getInstance().getStyle(TokenType.KEYWORD).getColor();
        } catch (Exception e) {
            return Color.BLACK;
        }


    }
    
    public void setEditorColors(String[] colors) {
        if (colors.length >= 9) {

            GlossaSyntaxKit kit = (GlossaSyntaxKit)jEditorPane1.getEditorKit();

            Properties newStyles = new Properties();

            newStyles.setProperty("Style.KEYWORD", colors[0] + ", 1");
            newStyles.setProperty("Style.KEYWORD2", colors[0] + ", 1");

            newStyles.setProperty("Style.NUMBER", colors[1] + ", 0");

            newStyles.setProperty("Style.STRING", colors[2] + ", 0");
            newStyles.setProperty("Style.STRING2", colors[2] + ", 0");

            newStyles.setProperty("Style.OPERATOR", colors[3] + ", 0");

            newStyles.setProperty("Style.COMMENT", colors[4] + ", 2");
            newStyles.setProperty("Style.COMMENT2", colors[4] + ", 2");

            newStyles.setProperty("Style.TYPE", colors[5] + ", 1");
            newStyles.setProperty("Style.TYPE2", colors[5] + ", 1");
            newStyles.setProperty("Style.TYPE3", colors[5] + ", 1");

            newStyles.setProperty("Style.IDENTIFIER", colors[6] + ", 0");

            newStyles.setProperty("Style.DELIMITER", colors[7] + ", 1");
            newStyles.setProperty("Style.DEFAULT", colors[7] + ", 0");
            
            kit.setConfig(newStyles);

            jEditorPane1.updateUI();

            this.jEditorPane1.setBackground(Color.decode(colors[8]));
        }

    }

    private String colorToHex(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        String R = Integer.toHexString(r);
        String G = Integer.toHexString(g);
        String B = Integer.toHexString(b);
        if (R.length() == 1) {
            R = "0" + R;
        }
        if (G.length() == 1) {
            G = "0" + G;
        }
        if (B.length() == 1) {
            B = "0" + B;
        }
        return "0x" + R + G + B;
    }

    public void setEditorFont(Font font) {
        jEditorPane1.setFont(font);
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        if (container != null) {
            String hint = null;
            if (this.storage != null) {
                try {
                    hint = this.storage.getCanonicalPath();
                } catch (IOException ioe) {
                }
            }
            container.notifyDocumentModified(getTitleWithModificationIndicator(), hint, modified);
        }
    }

    public boolean isNewFile() {
        return this.newFile;
    }

    public File getFile() {
        return this.storage;
    }

    public String getTitle() {
        return this.Title;
    }

    public String getTitleWithModificationIndicator() {
        String s = this.Title;
        if (this.modified) {
            s = s + "*";
        }
        return s;
    }

    public String getSelectedText() {
        return this.jEditorPane1.getSelectedText();
    }

    public Point getCaretPosition() {
        int absoluteOffset = jEditorPane1.getCaretPosition();
        int y = document.getLineNumberAt(absoluteOffset);
        int x = absoluteOffset - document.getLineStartOffset(absoluteOffset);
        return new Point(x, y);
    }

    public int getSelectionStart() {
        return jEditorPane1.getSelectionStart();
    }

    public void reset(int documentCount) {
        this.jEditorPane1.setText("");
        this.storage = null;
        this.Title = UNTITLED + "-" + documentCount;
        this.newFile = true;
        setModified(false);
    }

    private void createEmptyFile() {
        this.document = new SyntaxDocument(new GlossaLexer());
        jEditorPane1.setDocument(this.document);
        jEditorPane1.setCaretPosition(0);

        setModified(false);
        this.document.clearUndos();
    }

    private boolean loadFile(File f, Charset charset) {
        try {
            this.document = new SyntaxDocument(new GlossaLexer());
            jEditorPane1.setDocument(this.document);
            EditorKit kit = jEditorPane1.getEditorKit();
            /*CharsetDetector cd = new CharsetDetector();
            String[] charsetsToBeTested = {"UTF-8", "windows-1253"};
            Charset charset = cd.detectCharset(f, charsetsToBeTested);*/
            if (charset == null) {
                charset = Charset.forName("UTF-8");
            }
            if (charset != null) {
                kit.read(new InputStreamReader(new FileInputStream(f), charset), this.document, 0);
            } else {
                return false;
            }
            jEditorPane1.setCaretPosition(0);

            setModified(false);
            this.document.clearUndos();

            return true;
        } catch (FileNotFoundException fnfe) {
            return false;
        } catch (IOException ioe) {
            return false;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;

        }
    }

    public boolean saveFile(File f) {

        try {
            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter bw = new BufferedWriter(w);
            bw.write(jEditorPane1.getText());

            bw.flush();
            bw.close();
            w.close();


            this.storage = f;
            this.newFile = false;
            this.Title = storage.getName();
            setModified(false);

            return true;
        } catch (IOException ioe) {
            return false;
        }

    }

    public void undo() {
        this.document.doUndo();
        if (!this.document.canUndo()) {
            this.setModified(false);
        }
    }

    public void redo() {
        this.document.doRedo();
    }

    public boolean canUndo() {
        return this.document.canUndo();
    }

    public boolean canRedo() {
        return this.document.canRedo();
    }

    public void cut() {
        this.jEditorPane1.cut();
    }

    public void copy() {
        this.jEditorPane1.copy();
    }

    public void paste() {
        this.jEditorPane1.paste();
    }

    public void deleteSelection() {
        this.jEditorPane1.replaceSelection("");
    }

    public void selectAll() {
        this.jEditorPane1.selectAll();
    }
    // <editor-fold defaultstate="expanded" desc="DocumentListener implementation">
    /* IMPLEMENTATION OF THE DOCUMENTLISTENER INTERFACE : */

    public void insertUpdate(DocumentEvent e) {
        setModified(true);
    }

    public void removeUpdate(DocumentEvent e) {
        setModified(true);
    }

    public void changedUpdate(DocumentEvent e) {
        setModified(true);
    }

    /* ----------------------------------------------------- */
    // </editor-fold>
    // <editor-fold defaultstate="expanded" desc="CaretListener implementation">
    public void caretUpdate(CaretEvent e) {
        this.container.notifyCaretChanged(e);
    }

    /* ----------------------------------------------------- */
    // </editor-fold>
    // <editor-fold defaultstate="expanded" desc="UndoableEditListener implementation">
    public void undoableEditHappened(UndoableEditEvent evt) {
        if (evt.getEdit().isSignificant()) {
            if (!canUndo()) {
                container.notifyFirstUndoableEditHappened(evt);
            }
        }
    }
    /* ----------------------------------------------------- */
    // </editor-fold>

    public void removeBorders() {
        this.setBorder(null);
        this.jScrollPane1.setBorder(null);
        this.jEditorPane1.setBorder(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new JGlossaEditorPane();

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(glossaeditor.Slang.class).getContext().getResourceMap(EditorView.class);
        jEditorPane1.setBackground(resourceMap.getColor("jEditorPane1.background")); // NOI18N
        jEditorPane1.setName("jEditorPane1"); // NOI18N
        jEditorPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jEditorPane1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jEditorPane1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jEditorPane1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void jEditorPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jEditorPane1MouseClicked
}//GEN-LAST:event_jEditorPane1MouseClicked

private void jEditorPane1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jEditorPane1MousePressed

    if (evt.isPopupTrigger()) {
        this.popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_jEditorPane1MousePressed

private void jEditorPane1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jEditorPane1MouseReleased

    if (evt.isPopupTrigger()) {
        this.popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_jEditorPane1MouseReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}

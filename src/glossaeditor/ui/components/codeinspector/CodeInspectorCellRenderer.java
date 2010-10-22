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
package glossaeditor.ui.components.codeinspector;

import glossaeditor.Slang;
import glossaeditor.ui.components.codeinspector.items.CodeInspectorItem;
import glossaeditor.ui.components.codeinspector.items.FunctionItem;
import glossaeditor.ui.components.codeinspector.items.ProcedureItem;
import glossaeditor.ui.components.codeinspector.items.ProgramItem;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author cyberpython
 */
public class CodeInspectorCellRenderer extends DefaultTreeCellRenderer {

    private Icon programIcon;
    private Icon functionIcon;
    private Icon procedureIcon;
    private Icon variableIcon;

    public CodeInspectorCellRenderer(){
        this.programIcon = loadIcon("/artwork/icons/png/crossplatform/s16x16/code_inspector_program.png");
        this.functionIcon = loadIcon("/artwork/icons/png/crossplatform/s16x16/code_inspector_function.png");
        this.procedureIcon = loadIcon("/artwork/icons/png/crossplatform/s16x16/code_inspector_procedure.png");
        this.variableIcon = loadIcon("/artwork/icons/png/crossplatform/s16x16/code_inspector_variable.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        CodeInspectorItem item = (CodeInspectorItem) ((DefaultMutableTreeNode)value).getUserObject();

        if ((item instanceof ProgramItem) && (item != null)) {
            setIcon(this.programIcon);
            setText("Πρόγραμμα "+ ((ProgramItem)item).getTitle() );
        }else if((item instanceof FunctionItem) && (item != null)) {
            setIcon(this.functionIcon);
            setText("Συνάρτηση "+ ((FunctionItem)item).getTitle()+
                    " : "+((FunctionItem)item).getReturnType());
        }else if((item instanceof ProcedureItem) && (item != null)) {
            setIcon(this.procedureIcon);
            setText("Διαδικασία "+ ((ProcedureItem)item).getTitle());
        }

        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }

        if (!tree.isEnabled()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
        setComponentOrientation(tree.getComponentOrientation());
        selected = sel;
        
        return this;

    }

    private Icon loadIcon(String path) {
        java.net.URL iconURL = Slang.class.getResource(path);
        return new javax.swing.ImageIcon(iconURL);
    }
}

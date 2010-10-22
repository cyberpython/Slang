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
package glossaeditor.ui.components.codeinspector.items;

/**
 *
 * @author cyberpython
 */
public class CodeInspectorItem implements Comparable {

    private String title;

    public CodeInspectorItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int compareTo(Object obj) {
        if (obj instanceof CodeInspectorItem) {
            CodeInspectorItem o = (CodeInspectorItem) obj;
            if (this instanceof ProgramItem) {
                if (o instanceof ProgramItem) {
                    return ((ProgramItem) this).getTitle().compareTo(((ProgramItem) o).getTitle());
                } else {
                    return -1;
                }
            } else if (this instanceof ProcedureItem) {
                if (o instanceof ProgramItem) {
                    return 1;
                } else if (o instanceof ProcedureItem) {
                    return ((ProcedureItem) this).getTitle().compareTo(((ProcedureItem) o).getTitle());
                } else if (o instanceof FunctionItem) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (this instanceof FunctionItem) {
                if (o instanceof ProgramItem) {
                    return 1;
                } else if (o instanceof ProcedureItem) {
                    return 1;
                } else if (o instanceof FunctionItem) {
                    return ((FunctionItem) this).getTitle().compareTo(((FunctionItem) o).getTitle());
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}

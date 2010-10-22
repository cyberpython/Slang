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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author cyberpython
 */
public class ParserEvoker {

    public DefaultTreeModel apply(String code) {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);

        /*try {

            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream(code.getBytes()));
            GlossaLexer lexer = new GlossaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GlossaParser parser = new GlossaParser(tokens);
            GlossaParser.unit_return r = parser.unit();


            CommonTree t = (CommonTree) r.getTree();
            BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(t);
            GlossaWalkerForCodeInspector walker = new GlossaWalkerForCodeInspector(nodes);

            try {
                walker.unit();
            } catch (RecognitionException re) {

            }

            Vector<CodeInspectorItem> results = walker.getResults();
            CodeInspectorItem[] resultsArray =  results.toArray(new CodeInspectorItem[0]);
            Arrays.sort(resultsArray);

            for (CodeInspectorItem codeInspectorItem : resultsArray) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(codeInspectorItem);
                root.add(treeNode);
            }


        } catch (RecognitionException re) {
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/

        return model;
    }
}

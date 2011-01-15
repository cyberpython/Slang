/*
 *  Copyright 2010 Georgios Migdos <cyberpython@gmail.com>.
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

package glossa.ui.gui.stackrenderer.components;

import glossa.interpreter.symboltable.symbols.RuntimeSymbol;
import javax.swing.DefaultListModel;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class RuntimeSymbolSortedListModel extends DefaultListModel{

    public void addElement(RuntimeSymbol s) {
        if(s==null){return;}
        String name = s.getName();
        int max = getSize();
        int index = 0;
        while(index<size()){
            String name2 = ((RuntimeSymbol)get(index)).getName();
            if(name.compareTo(name2)<0){
                super.add(index, s);
                return;
            }
            index++;
        }
        if(index==size()){
            super.addElement(s);
        }
    }

    @Override
    public void add(int index, Object element) {
        if(element instanceof RuntimeSymbol){
            super.add(index, element);
        }
    }

    @Override
    public void addElement(Object obj) {
        if(obj instanceof RuntimeSymbol){
            super.addElement(obj);
        }
    }

    @Override
    public RuntimeSymbol get(int index) {
        return (RuntimeSymbol)super.get(index);
    }







}

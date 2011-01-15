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

import glossa.interpreter.core.InterpreterUtils;
import glossa.interpreter.symboltable.symbols.RuntimeArray;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class JRuntimeArrayRenderer extends JPanel implements ListCellRenderer{

    private int borderWidth;
    private int strutWidth;

    private int offset;

    private Color bg1;
    private Color bg2;

    private Font indexLabelFont;
    private Font valueFont;
    private List<Dimension> labelSizes;
    private Dimension valueSize;

    private LineBorder border1;
    private LineBorder border2;

    private RuntimeArray arr;


    public JRuntimeArrayRenderer(){
        super();
        bg1 = Color.WHITE;
        bg2 = new Color(175, 198, 233);

        setBackground(bg1);


        indexLabelFont = new Font("Dialog", Font.BOLD, 12);
        valueFont = new Font("Dialog", Font.PLAIN, 12);
        labelSizes = new ArrayList<Dimension>();
        valueSize = null;

        borderWidth = 5;
        strutWidth = 1;

        border1 = (LineBorder) BorderFactory.createLineBorder(bg1, borderWidth);
        border2 = (LineBorder) BorderFactory.createLineBorder(bg2, borderWidth);

        this.arr = null;
    }

    public void setRuntimeArray(RuntimeArray arr) {
        this.arr = arr;
        populate();
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value instanceof RuntimeArray){
            RuntimeArray arr = (RuntimeArray)value;
            offset = 0;
            Object[] elements = arr.getValues();
            List<Integer> dimensions = arr.getDimensions();
            this.calculateLabelSizes(dimensions, list.getGraphics());
            this.calculateValueSize(elements, list.getGraphics());
            return populate(elements, dimensions, 0, BoxLayout.Y_AXIS);
        }else{
            return new JPanel();
        }
    }



    private void populate(){
        this.removeAll();
        this.setLayout(new FlowLayout(FlowLayout.LEFT));


        offset = 0;
        Object[] elements = arr.getValues();
        List<Integer> dimensions = arr.getDimensions();
        this.calculateLabelSizes(dimensions, this.getGraphics());
        this.calculateValueSize(elements, this.getGraphics());
        this.add(populate(elements, dimensions, 0, BoxLayout.Y_AXIS));
    }

    

    private int invertOrientation(int orientation) {
        if (orientation == BoxLayout.X_AXIS) {
            return BoxLayout.Y_AXIS;
        } else {
            return BoxLayout.X_AXIS;
        }
    }
    
    private void calculateLabelSizes(List<Integer> dimensions, Graphics g){
        int totalBorderWidth = 2*borderWidth;
        FontMetrics fm = g.getFontMetrics(indexLabelFont);
        List<Dimension> result = new ArrayList<Dimension>();
        for (Iterator<Integer> it = dimensions.iterator(); it.hasNext();) {
            Integer dimension = it.next();
            Dimension dim = new Dimension(fm.stringWidth(dimension.toString()+"  "+totalBorderWidth), fm.getHeight()+totalBorderWidth+10);
            result.add(dim);
        }
        labelSizes = result;
    }

    private void calculateValueSize(Object[] elements, Graphics g){
        FontMetrics fm = g.getFontMetrics(valueFont);
        int maxWidth = 2*borderWidth+10;
        for (Object element : elements) {
            if(element!=null){
                maxWidth = Math.max(fm.stringWidth(element.toString()), maxWidth);
            }
        }
        valueSize = new Dimension(maxWidth+2*borderWidth+20, fm.getHeight()+2*borderWidth+10);
    }

    private void setBg(JComponent c, int i){
        if(i % 2 == 0){
            c.setBackground(bg1);
        }else{
            c.setBackground(bg2);
        }
    }

    private void setBorder(JComponent c, int i){
        if(i % 2 == 0){
            c.setBorder(border1);
        }else{
            c.setBorder(border2);
        }
    }

    private void addStrut(JComponent c, int orientation){
        if (orientation == BoxLayout.X_AXIS) {
            c.add(Box.createHorizontalStrut(strutWidth));
        } else {
            c.add(Box.createVerticalStrut(strutWidth));
        }
    }

    private JRoundedPanel populate(Object[] elements, List<Integer> dimensions, int i, int orientation) {
        JRoundedPanel result = new JRoundedPanel();
        setBg(result, i);
        setBorder(result, i);
        result.setLayout(new BoxLayout(result, orientation));
        if (i == dimensions.size() - 1) {
            for (int v = 0; v < dimensions.get(i); v++) {
                JRoundedPanel valPan = new JRoundedPanel();
                setBg(valPan, i);
                setBorder(valPan, i);
                valPan.setLayout(new BoxLayout(valPan, invertOrientation(orientation))); //FIXME
                JLabel label = new JLabel();
                label.setFont(indexLabelFont);
                setBg(label, i);
                setBorder(label, i);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setText(String.valueOf(v+1));
                Dimension labelSize = labelSizes.get(i);

                if(orientation==BoxLayout.Y_AXIS){
                    label.setPreferredSize(labelSize);
                    label.setMinimumSize(labelSize);
                    label.setMaximumSize(labelSize);
                }else{
                    label.setPreferredSize(valueSize);
                    label.setMinimumSize(valueSize);
                    label.setMaximumSize(valueSize);
                }

                valPan.add(label);
                JLabel value = new JLabel();
                value.setFont(valueFont);
                setBg(value, i+1);
                setBorder(value, i+1);
                value.setOpaque(true);
                value.setHorizontalAlignment(SwingConstants.CENTER);
                value.setVerticalAlignment(SwingConstants.CENTER);
                Object element = InterpreterUtils.toPrintableString(elements[offset]);
                String valueText = element == null ? "" : element.toString();
                value.setText(valueText);

                value.setPreferredSize(valueSize);
                value.setMinimumSize(valueSize);
                value.setMaximumSize(valueSize);

                valPan.add(value);


                result.add(valPan);
                offset++;
            }

        } else {
            for (int j = 0; j < dimensions.get(i); j++) {
                JRoundedPanel container = new JRoundedPanel();
                setBg(container, i);
                setBorder(container, i);
                container.setLayout(new BoxLayout(container, invertOrientation(orientation)));

                JLabel label = new JLabel(String.valueOf(j+1));
                label.setFont(indexLabelFont);
                setBg(label, i);
                setBorder(label, i);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setHorizontalTextPosition(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);

                label.setPreferredSize(labelSizes.get(i));
                label.setMinimumSize(labelSizes.get(i));

                container.add(label);

                JRoundedPanel item = populate(elements, dimensions, i+1, invertOrientation(orientation));
                container.add(item);
                setBorder(item, i);

                result.add(container);
            }
        }
        addStrut(result, invertOrientation(orientation));
        return result;
    }


    public static void main(String[] args) {

    }
}

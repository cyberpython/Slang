/*
 *  Copyright 2009 Georgios "cyberpython" Migdos <cyberpython@gmail.com>.
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
package glossaeditor.preferences;

import java.awt.Color;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class HighlighterProfile {

    private Color KEYWORDS_COLOR;
    private Color NUMBERS_COLOR;
    private Color STRINGS_COLOR;
    private Color OPERATORS_COLOR;
    private Color COMMENTS_COLOR;
    private Color TYPES_COLOR;
    private Color IDENTIFIERS_COLOR;
    private Color DEFAULT_TEXT_COLOR;
    private Color BG_COLOR;

    public final String KEYWORDS_DEFAULT = "0x002967";
    public final String NUMBERS_DEFAULT = "0x016800";
    public final String STRINGS_DEFAULT = "0x650067";
    public final String OPERATORS_DEFAULT = "0x670001";
    public final String COMMENTS_DEFAULT = "0x114e21";
    public final String TYPES_DEFAULT = "0x002967";
    public final String IDENTIFIERS_DEFAULT = "0x000000";
    public final String DEFAULT_TEXT_DEFAULT = "0x000000";
    public final String BG_DEFAULT = "0xFFFFFF";

    private final String DEFAULT_TITLE = "ΓΛΩΣΣΑ";
    public final String  CURRENT_TITLE  = "Τρέχουσες επιλογές";
    public final String  CUSTOM_TITLE   = "Προσαρμοσμένο";
    
    private String title;

    public HighlighterProfile() {
        this.setTitle(this.DEFAULT_TITLE);

        String[] colors = {this.KEYWORDS_DEFAULT, this.NUMBERS_DEFAULT, 
                           this.STRINGS_DEFAULT, this.OPERATORS_DEFAULT, 
                           this.COMMENTS_DEFAULT, this.TYPES_DEFAULT, 
                           this.IDENTIFIERS_DEFAULT, this.DEFAULT_TEXT_DEFAULT, this.BG_DEFAULT};
        this.setColors(colors);
    }

    public HighlighterProfile(String title, Color[] colors) {
        this.setTitle(title);
        this.setColors(colors);
    }

    public HighlighterProfile(String title, String[] colors) {
        this.setTitle(title);
        this.setColors(colors);
    }

    public String getTitle() {
        return this.title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return this.getTitle();
    }

    private String getColorString(Color color) {
        return String.format("0x%06x", color.getRGB() & 0x00ffffff);
    }

    public final void setColors(String[] colors) {
        if (colors.length == 9) {
            this.KEYWORDS_COLOR = Color.decode(colors[0]);
            this.NUMBERS_COLOR = Color.decode(colors[1]);
            this.STRINGS_COLOR = Color.decode(colors[2]);
            this.OPERATORS_COLOR = Color.decode(colors[3]);
            this.COMMENTS_COLOR = Color.decode(colors[4]);
            this.TYPES_COLOR = Color.decode(colors[5]);
            this.IDENTIFIERS_COLOR = Color.decode(colors[6]);
            this.DEFAULT_TEXT_COLOR = Color.decode(colors[7]);
            this.BG_COLOR = Color.decode(colors[8]);
        }
    }

    public final void setColors(Color[] colors) {
        if (colors.length == 9) {
            this.KEYWORDS_COLOR = colors[0];
            this.NUMBERS_COLOR = colors[1];
            this.STRINGS_COLOR = colors[2];
            this.OPERATORS_COLOR = colors[3];
            this.COMMENTS_COLOR = colors[4];
            this.TYPES_COLOR = colors[5];
            this.IDENTIFIERS_COLOR = colors[6];
            this.DEFAULT_TEXT_COLOR = colors[7];
            this.BG_COLOR = colors[8];
        }
    }

    public Color[] getColors() {
        Color[] colors = new Color[9];

        colors[0] = this.KEYWORDS_COLOR;
        colors[1] = this.NUMBERS_COLOR;
        colors[2] = this.STRINGS_COLOR;
        colors[3] = this.OPERATORS_COLOR;
        colors[4] = this.COMMENTS_COLOR;
        colors[5] = this.TYPES_COLOR;
        colors[6] = this.IDENTIFIERS_COLOR;
        colors[7] = this.DEFAULT_TEXT_COLOR;
        colors[8] = this.BG_COLOR;

        return colors;
    }

    public String[] getColorsAsStrings() {
        String[] colors = new String[9];

        colors[0] = this.getColorString(this.KEYWORDS_COLOR);
        colors[1] = this.getColorString(this.NUMBERS_COLOR);
        colors[2] = this.getColorString(this.STRINGS_COLOR);
        colors[3] = this.getColorString(this.OPERATORS_COLOR);
        colors[4] = this.getColorString(this.COMMENTS_COLOR);
        colors[5] = this.getColorString(this.TYPES_COLOR);
        colors[6] = this.getColorString(this.IDENTIFIERS_COLOR);
        colors[7] = this.getColorString(this.DEFAULT_TEXT_COLOR);
        colors[8] = this.getColorString(this.BG_COLOR);

        return colors;
    }

    public Color getKeywordsColor() {
        return this.KEYWORDS_COLOR;
    }

    public Color getNumbersColor() {
        return this.NUMBERS_COLOR;
    }

    public Color getStringsColor() {
        return this.STRINGS_COLOR;
    }

    public Color getOperatorsColor() {
        return this.OPERATORS_COLOR;
    }

    public Color getCommentsColor() {
        return this.COMMENTS_COLOR;
    }

    public Color getTypesColor() {
        return this.TYPES_COLOR;
    }

    public Color getIdentifiersColor() {
        return this.IDENTIFIERS_COLOR;
    }

    public Color getDefaultColor() {
        return this.DEFAULT_TEXT_COLOR;
    }

    public Color getBgColor() {
        return this.BG_COLOR;
    }

    public void setKeywordsColor(Color color){
        this.KEYWORDS_COLOR = color;
    }

    public void setNumbersColor(Color color){
        this.NUMBERS_COLOR = color;
    }

    public void setStringsColor(Color color){
        this.STRINGS_COLOR = color;
    }

    public void setOperatorsColor(Color color){
        this.OPERATORS_COLOR = color;
    }

    public void setCommentsColor(Color color){
        this.COMMENTS_COLOR = color;
    }


    public void setTypesColor(Color color){
        this.TYPES_COLOR = color;
    }

    public void setIdentifiersColor(Color color){
        this.IDENTIFIERS_COLOR = color;
    }

    public void setDefaultColor(Color color){
        this.DEFAULT_TEXT_COLOR = color;
    }

    public void setBgColor(Color color){
        this.BG_COLOR = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HighlighterProfile) {
            HighlighterProfile profile = (HighlighterProfile) obj;
            if(
                    this.title.equals(profile.title) &&
                    this.KEYWORDS_COLOR.equals(profile.KEYWORDS_COLOR) &&
                    this.NUMBERS_COLOR.equals(profile.NUMBERS_COLOR) &&
                    this.COMMENTS_COLOR.equals(profile.COMMENTS_COLOR) &&
                    this.STRINGS_COLOR.equals(profile.STRINGS_COLOR) &&
                    this.IDENTIFIERS_COLOR.equals(profile.IDENTIFIERS_COLOR) &&
                    this.OPERATORS_COLOR.equals(profile.OPERATORS_COLOR) &&
                    this.TYPES_COLOR.equals(profile.TYPES_COLOR) &&
                    this.DEFAULT_TEXT_COLOR.equals(profile.DEFAULT_TEXT_COLOR) &&
                    this.BG_COLOR.equals(profile.BG_COLOR)
                ){
                return true;
            }
            else{
                return false;
            }

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.KEYWORDS_COLOR != null ? this.KEYWORDS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.NUMBERS_COLOR != null ? this.NUMBERS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.STRINGS_COLOR != null ? this.STRINGS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.OPERATORS_COLOR != null ? this.OPERATORS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.COMMENTS_COLOR != null ? this.COMMENTS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.TYPES_COLOR != null ? this.TYPES_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.IDENTIFIERS_COLOR != null ? this.IDENTIFIERS_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.DEFAULT_TEXT_COLOR != null ? this.DEFAULT_TEXT_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.BG_COLOR != null ? this.BG_COLOR.hashCode() : 0);
        hash = 37 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }
}

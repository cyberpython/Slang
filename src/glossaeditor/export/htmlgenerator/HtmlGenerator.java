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

package glossaeditor.export.htmlgenerator;

import glossaeditor.export.htmlgenerator.lexer.Lexer;
import glossaeditor.export.htmlgenerator.lexer.LexerException;
import glossaeditor.export.htmlgenerator.node.TAmp;
import glossaeditor.export.htmlgenerator.node.TAssign;
import glossaeditor.export.htmlgenerator.node.TBoolean;
import glossaeditor.export.htmlgenerator.node.TBrackets;
import glossaeditor.export.htmlgenerator.node.TComment;
import glossaeditor.export.htmlgenerator.node.TDataType;
import glossaeditor.export.htmlgenerator.node.TDifferent;
import glossaeditor.export.htmlgenerator.node.TGt;
import glossaeditor.export.htmlgenerator.node.TGte;
import glossaeditor.export.htmlgenerator.node.TIdentifier;
import glossaeditor.export.htmlgenerator.node.TKeyword;
import glossaeditor.export.htmlgenerator.node.TLineEnd;
import glossaeditor.export.htmlgenerator.node.TLt;
import glossaeditor.export.htmlgenerator.node.TLte;
import glossaeditor.export.htmlgenerator.node.TMisc;
import glossaeditor.export.htmlgenerator.node.TNumber;
import glossaeditor.export.htmlgenerator.node.TOperators;
import glossaeditor.export.htmlgenerator.node.TSpace;
import glossaeditor.export.htmlgenerator.node.TString;
import glossaeditor.export.htmlgenerator.node.TSymbols;
import glossaeditor.export.htmlgenerator.node.TTab;
import glossaeditor.export.htmlgenerator.node.Token;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackReader;

/**
 *
 * @author Georgios "cyberpython" Migdos <cyberpython@gmail.com>
 */
public class HtmlGenerator {

    private Color[] colors;

    public HtmlGenerator() {
        this.colors = null;
    }

    private void outputHtmlHeader(PrintWriter out, String documentTitle, String fontFamilyName, Float fontSizeInEm, boolean useDefaultStyles, boolean autoPrint, boolean showLineNumbers) {



        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"   \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");        
        out.println("");
        out.println("        <head>");
        out.println("            <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
        out.println("            <title>" + documentTitle + "</title>");
        out.println("            <style type=\"text/css\">");


        if (useDefaultStyles || (colors == null)) {
            outputDefaultStylesheet(out);
        } else {
            outputCustomStylesheet(out, fontFamilyName, fontSizeInEm, showLineNumbers);
        }


        out.println("            </style>");
        out.println();
        if (autoPrint) {
            out.println("            <script type=\"text/javascript\">");
            out.println("                window.onload = function() {");
            out.println("                    window.print();");
            out.println("                }");
            out.println("            </script>");
            out.println();
        }
        out.println("        </head>");
        out.println("");
        /*if (autoPrint) {
            out.println("        <body onload=\"window.print();\">");
        }
        else{
            out.println("        <body>");
        }*/
        out.println("        <body>");
        //out.println("            <p>");
        //out.println("");
        out.println("            <ol>");
        out.println("");
        out.print("<li>");



    }

    private void outputDefaultStylesheet(PrintWriter out) {

        try {


            InputStream is = this.getClass().getResourceAsStream("/jargonhtmlgenerator/resources/stylesheet.css");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = "";
            while (null != (line = br.readLine())) {
                out.println(line);
            }

        } catch (Exception e) {
        }
    }

    private void outputCustomStylesheet(PrintWriter out, String fontFamilyName, Float fontSizeInEm, boolean showLineNumbers) {

        if (colors.length < 9) {
            outputDefaultStylesheet(out);
            return;
        }


        out.println("body{");
        out.println("   background-color: rgb(" + colors[8].getRed() + "," + colors[8].getGreen() + "," + colors[8].getBlue() + ");");
        out.println("   line-height: "+fontSizeInEm+"em;");
        out.println("}");
        
        out.println("ol{");
        if(!showLineNumbers){
            out.println("    list-style-type: none;");
        }
        out.println("    font-family: "+ fontFamilyName +";");
        out.println("    font-size: "+ fontSizeInEm +"em;");
        out.println("    margin: 0;");
        out.println("    padding:1em 0 1em 2.8em; ");
        out.println("}");

        out.println(".keyword, .boolean{");
        out.println("    color: rgb(" + colors[0].getRed() + "," + colors[0].getGreen() + "," + colors[0].getBlue() + ");");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".number{");
        out.println("    color: rgb(" + colors[1].getRed() + "," + colors[1].getGreen() + "," + colors[1].getBlue() + ");");
        out.println("}");

        out.println(".string{");
        out.println("    color: rgb(" + colors[2].getRed() + "," + colors[2].getGreen() + "," + colors[2].getBlue() + ");");
        out.println("}");

        out.println(".operator, .bracket, .symbol{");
        out.println("    color: rgb(" + colors[3].getRed() + "," + colors[3].getGreen() + "," + colors[3].getBlue() + ");");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".comment{");
        out.println("    color: rgb(" + colors[4].getRed() + "," + colors[4].getGreen() + "," + colors[4].getBlue() + ");");
        out.println("    font-style: italic;");
        out.println("}");

        out.println(".type{");
        out.println("    color: rgb(" + colors[5].getRed() + "," + colors[5].getGreen() + "," + colors[5].getBlue() + ");");
        out.println("    font-weight: bold;");
        out.println("}");

        out.println(".identifier{");
        out.println("    color: rgb(" + colors[6].getRed() + "," + colors[6].getGreen() + "," + colors[6].getBlue() + ");");
        out.println("}");

        out.println(".misc{");
        out.println("    color: rgb(" + colors[7].getRed() + "," + colors[7].getGreen() + "," + colors[7].getBlue() + ");");
        out.println("}");
    }

    private void outputHtmlFooter(PrintWriter out) {
        out.println("</li>");
        out.println("");
        out.println("            </ol>");
        //out.println("");
        //out.println("            </p>");
        out.println("        </body>");
        out.println("");
        out.println("</html>");
        out.println("");


    }

    private void printToken(Token token) {
        Class cls = token.getClass();

        System.out.print("New " + cls.getName() + " token: \"" + token.getText() + "\"  \n");
    }

    private void outputHtmlForToken(Token token, PrintWriter out) {
        Class cls = token.getClass();

        String tokenHtmlClass = "";
        if (token instanceof TComment) {
            tokenHtmlClass = "comment";
        } else if (token instanceof TDataType) {
            tokenHtmlClass = "type";
        } else if (token instanceof TKeyword) {
            tokenHtmlClass = "keyword";
        } else if (token instanceof TOperators) {
            tokenHtmlClass = "operator";
        } else if (token instanceof TBrackets) {
            tokenHtmlClass = "bracket";
        } else if (token instanceof TSymbols) {
            tokenHtmlClass = "symbol";
        } else if (token instanceof TAssign) {
            tokenHtmlClass = "assign";
        } else if (token instanceof TDifferent) {
            tokenHtmlClass = "different";
        } else if (token instanceof TLt) {
            tokenHtmlClass = "lt";
        } else if (token instanceof TGt) {
            tokenHtmlClass = "gt";
        } else if (token instanceof TLte) {
            tokenHtmlClass = "lte";
        } else if (token instanceof TGte) {
            tokenHtmlClass = "gte";
        } else if (token instanceof TAmp) {
            tokenHtmlClass = "amp";
        } else if (token instanceof TNumber) {
            tokenHtmlClass = "number";
        } else if (token instanceof TString) {
            tokenHtmlClass = "string";
        } else if (token instanceof TBoolean) {
            tokenHtmlClass = "boolean";
        } else if (token instanceof TIdentifier) {
            tokenHtmlClass = "identifier";
        } else if (token instanceof TMisc) {
            tokenHtmlClass = "misc";
        } else if (token instanceof TLineEnd) {
            tokenHtmlClass = "lineEnd";
        } else if (token instanceof TSpace) {
            tokenHtmlClass = "space";
        } else if (token instanceof TTab) {
            tokenHtmlClass = "tab";
        } else {
            tokenHtmlClass = "-";
        }

        if (tokenHtmlClass.equals("-")) {
            out.print(token.getText());
        } else if (tokenHtmlClass.equals("space")) {
            out.print("&nbsp;");
        } else if (tokenHtmlClass.equals("tab")) {
            out.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        } else if (tokenHtmlClass.equals("assign")) {
            out.print("<span class=\"operator\">&lt;-</span>");
        } else if (tokenHtmlClass.equals("different")) {
            out.print("<span class=\"operator\">&lt;&gt;</span>");
        } else if (tokenHtmlClass.equals("lt")) {
            out.print("<span class=\"operator\">&lt;</span>");
        } else if (tokenHtmlClass.equals("gt")) {
            out.print("<span class=\"operator\">&gt;</span>");
        } else if (tokenHtmlClass.equals("lte")) {
            out.print("<span class=\"operator\">&lt;=</span>");
        } else if (tokenHtmlClass.equals("gte")) {
            out.print("<span class=\"operator\">&gt;=</span>");
        } else if (tokenHtmlClass.equals("amp")) {
            out.print("<span class=\"symbol\">&amp;</span>");
        } else if (tokenHtmlClass.equals("lineEnd")) {
            out.print("&nbsp;</li>\n<li>");
        } else if (tokenHtmlClass.equals("string")) {
            String s = token.getText();
            out.print("<span class=\"string\">" + "&#39;" + s.substring(1, s.length() - 1) + "&#39;</span>");
        } else {
            out.print("<span class=\"" + tokenHtmlClass + "\">" + token.getText() + "</span>");
        }


    }

    public void setColors(Color[] colorsArray) {
        this.colors = colorsArray;
    }

    public Color[] getColors() {
        return this.colors;
    }

    public boolean generateHtml(InputStreamReader input, File output, String title, String fontFamilyName, Float fontSizeInEm, boolean useDefaultStyles, boolean autoPrint, boolean showLineNumbers) throws IOException {


        Lexer lexer = new Lexer(new PushbackReader(input));

        PrintWriter p = new PrintWriter(output, "UTF-8");
        outputHtmlHeader(p, title, fontFamilyName, fontSizeInEm, useDefaultStyles, autoPrint, showLineNumbers);

        try {
            Token token = lexer.next();
            Token lastToken = null;
            while (!token.getText().equals("")) {

                String s = new String();
                // printToken(token);
                outputHtmlForToken(token, p);
                lastToken = token;
                token = lexer.next();
            }
        } catch (LexerException le) {
            System.err.println(le.toString());
            return false;
        }

        outputHtmlFooter(p);

        p.flush();
        p.close();

        return true;
    }

    public static void main(String[] args) {
        if ((args.length != 3)) {
            System.out.println("-Invalid parameters list.");
            System.out.println("-Correct usage is:");
            System.out.println("    java -jar jargonhtmlgenerator <input file> <output file> <html document title>");
            System.exit(0);
        }

        boolean useDefaultStyles = true;

        HtmlGenerator gen = new HtmlGenerator();

        try {
            gen.generateHtml(new FileReader(new File(args[0])), new File(args[1]), args[2], args[3], Float.valueOf(args[4]), useDefaultStyles, false, true);
        } catch (Exception e) {
        }
    }
}

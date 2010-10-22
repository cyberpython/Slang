/*
 * Copyright 2010 Georgios Migdos cyberpython@gmail.com
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
 
 package jsyntaxpane.lexers;


import jsyntaxpane.Token;
import jsyntaxpane.TokenType;
 
%% 

%public
%class GlossaLexer
%extends DefaultJFlexLexer
%final
%unicode
%char
%ignorecase
%type Token

%{
    /**
     * Create an empty lexer, yyrset will be called later to reset and assign
     * the reader
     */
    public GlossaLexer() {
        super();
    }

    @Override
    public int yychar() {
        return yychar;
    }

    private static final byte PARAN     = 1;
    private static final byte BRACKET   = 2;
    private static final byte CURLY     = 3;

%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]+

/* comments */
Comment = {EndOfLineComment} 

sigma    = 'ς' | 'Σ' | 'Σ' ;

EndOfLineComment = "!" {InputCharacter}* {LineTerminator}?

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*
    
/* floating point literals */        
FloatLiteral  = [0-9]+ \. [0-9]*


/* string and character literals */
SingleCharacter = [^\r\n\'\\]

%state CHARLITERAL

%%

<YYINITIAL> {

  /* keywords */
  "ΠΡΟΓΡΑΜΜΑ"                   |
  "ΑΡΧΗ"                      	|
  "ΤΕΛΟ"ς"_ΠΡΟΓΡΑΜΜΑΤΟ"ς        |
  "ΣΤΑΘΕΡΕ"ς                    |
  "ΜΕΤΑΒΛΗΤΕ" ς                 |
  "ΑΝ"                     	|
  "ΤΟΤΕ"                        |
  "ΑΛΛΙΩ"ς"_ΑΝ"                 |
  "ΑΛΛΙΩ"ς                      |
  "ΤΕΛΟ"ς"_ΑΝ"                  |
  "ΕΠΙΛΕΞΕ"                     |
  "ΠΕΡΙΠΤΩΣΗ"                   |
  "ΤΕΛΟ"ς"_ΕΠΙΛΟΓΩΝ"            |
  "ΔΙΑΒΑΣΕ"                     |
  "ΓΡΑΨΕ"                       |
  "ΟΣΟ"                      	|
  "ΕΠΑΝΑΛΑΒΕ"                   |
  "ΤΕΛΟ"ς"_ΕΠΑΝΑΛΗΨΗ"ς          |
  "ΑΡΧΗ_ΕΠΑΝΑΛΗΨΗ"ς             |
  "ΜΕΧΡΙ"ς"_ΟΤΟΥ"               |
  "ΓΙΑ"                    	|
  "ΑΠΟ"                         |
  "ΜΕΧΡΙ"                       |
  "ΜΕ"                          |
  "ΒΗΜΑ"                        |
  "ΔΙΑΔΙΚΑΣΙΑ"                  |
  "ΤΕΛΟ"ς"_ΔΙΑΔΙΚΑΣΙΑς"         |
  "ΣΥΝΑΡΤΗΣΗ"                   |
  "ΤΕΛΟ"ς"_ΣΥΝΑΡΤΗΣΗ"ς          |
  "ΚΑΛΕΣΕ"                      |

  "πρόγραμμα"                   |
  "αρχή"                      	|
  "τέλο"ς"_προγράμματο"ς        |
  "σταθερέ"ς                    |
  "μεταβλητέ"ς                  |
  "αν"                     	|
  "τότε"                        |
  "αλλιώ"ς"_αν"                 |
  "αλλιώ"ς                      |
  "τέλο"ς"_αν"                  |
  "επίλεξε"                     |
  "περίπτωση"                   |
  "τέλο"ς"_επιλογών"            |
  "διάβασε"                     |
  "γράψε"                       |
  "όσο"                      	|
  "επανάλαβε"                   |
  "τέλο"ς"_επανάληψη"ς          |
  "αρχή_επανάληψη"ς             |
  "μέχρι"ς"_ότου"               |
  "για"                    	|
  "από"                         |
  "μέχρι"                       |
  "με"                          |
  "βήμα"                        |
  "διαδικασία"                  |
  "τέλο"ς"_διαδικασία"ς         |
  "συνάρτηση"                   |
  "τέλο"ς"_συνάρτηση"ς          |
  "κάλεσε"                      |
  
  "<-"                          |
  "←"                           |
  "ΟΧΙ"                         |
  "όχι"                         |
  "ΚΑΙ"                         |
  "Ή"                           |
  "DIV"                         |
  "MOD"                         |
  
  
  "ΑΛΗΘΗ"ς                      |
  "ΨΕΥΔΗ"ς                      |
  "αληθή"ς                      |
  "ψευδή"ς                      { return token(TokenType.KEYWORD); }

  /* Glossa Built in types */
  "ΑΚΕΡΑΙΕ"ς                    |
  "ΠΡΑΓΜΑΤΙΚΕ"ς                 |
  "ΧΑΡΑΚΤΗΡΕ"ς                  |
  "ΛΟΓΙΚΕ"ς                     |
  "ΑΚΕΡΑΙΑ"                     |
  "ΠΡΑΓΜΑΤΙΚΗ"                  |
  "ΧΑΡΑΚΤΗΡΑ"ς                  |
  "ΛΟΓΙΚΗ"                      |  
  "ακέραιε"ς                    |
  "πραγματικέ"ς                 |
  "χαρακτήρε"ς                  |
  "λογικέ"ς                     |
  "ακέραια"                     |
  "πραγματική"                  |
  "χαρακτήρα"ς                  |
  "λογική"                      { return token(TokenType.TYPE); }
  
  /* operators */

  "("                            { return token(TokenType.OPERATOR,  PARAN); }
  ")"                            { return token(TokenType.OPERATOR, -PARAN); }
  "["                            { return token(TokenType.OPERATOR,  BRACKET); }
  "]"                            { return token(TokenType.OPERATOR, -BRACKET); }
  ","                            | 
  "."                            | 
  ":"                            | 
  "&"                            | 
  ">"                            | 
  "<"                            |
  "<>"                           |
  "="                            | 
  "<="                           | 
  ">="                           | 
  "+"                            | 
  "-"                            | 
  "*"                            | 
  "/"                            | 
  "^"                            { return token(TokenType.OPERATOR); } 
  
  /* character literal */
  \'                             {  
                                    yybegin(CHARLITERAL); 
                                    tokenStart = yychar; 
                                    tokenLength = 1; 
                                 }

  /* numeric literals */

  {DecIntegerLiteral}            |  
  {FloatLiteral}                 { return token(TokenType.NUMBER); }
  

  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  /* whitespace */
  {WhiteSpace}                   { }

  /* identifiers */ 
  {Identifier}                   { return token(TokenType.IDENTIFIER); }
}


/*<STRING> {
  \"                             { 
                                     yybegin(YYINITIAL); 
                                     // length also includes the trailing quote
                                     return new Token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }
  
  {StringCharacter}+             { tokenLength += yylength(); }*/
    
  /* escape sequences */

/*  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}*/

<CHARLITERAL> {
  \'                             { 
                                     yybegin(YYINITIAL); 
                                     // length also includes the trailing quote
                                     return new Token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }
  
  {SingleCharacter}+             { tokenLength += yylength(); }
  
  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }

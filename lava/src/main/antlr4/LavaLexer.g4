lexer grammar LavaLexer;

@header {
package org.ine5426.lava.generated;
}

tokens { INDENT, DEDENT }

@lexer::members {

  // A queue where extra tokens are pushed on (see the NEWLINE lexer rule).
  private java.util.LinkedList<Token> tokens = new java.util.LinkedList<>();

  // The stack that keeps track of the indentation level.
  private java.util.Stack<Integer> indents = new java.util.Stack<>();

  // The amount of opened braces, brackets and parenthesis.
  private int opened = 0;

  // The most recently produced token.
  private Token lastToken = null;

  @Override
  public void emit(Token t) {
    super.setToken(t);
    tokens.offer(t);
  }

  @Override
  public Token nextToken() {

    // Check if the end-of-file is ahead and there are still some DEDENTS expected.
    if (_input.LA(1) == EOF && !this.indents.isEmpty()) {

      // Remove any trailing EOF tokens from our buffer.
      for (int i = tokens.size() - 1; i >= 0; i--) {
        if (tokens.get(i).getType() == EOF) {
          tokens.remove(i);
        }
      }

      // First emit an extra line break that serves as the end of the statement.
      this.emit(commonToken(LavaParser.NEWLINE, "\n"));

      // Now emit as much DEDENT tokens as needed.
      while (!indents.isEmpty()) {
        this.emit(createDedent());
        indents.pop();
      }

      // Put the EOF back on the token stream.
      this.emit(commonToken(LavaParser.EOF, "<EOF>"));
    }

    Token next = super.nextToken();

    if (next.getChannel() == Token.DEFAULT_CHANNEL) {
      // Keep track of the last token on the default channel.
      this.lastToken = next;
    }

    return tokens.isEmpty() ? next : tokens.poll();
  }

  private Token createDedent() {
    CommonToken dedent = commonToken(LavaParser.DEDENT, "");
    dedent.setLine(this.lastToken.getLine());
    return dedent;
  }

  private CommonToken commonToken(int type, String text) {
  	return new CommonToken(type, text);
    //int stop = this.getCharIndex() - 1;
    //int start = text.isEmpty() ? stop : stop - text.length() + 1;
    //return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
  }

  // Calculates the indentation of the provided spaces, taking the
  // following rules into account:
  //
  // "Tabs are replaced (from left to right) by one to eight spaces
  //  such that the total number of characters up to and including
  //  the replacement is a multiple of eight [...]"
  //
  //  -- https://docs.python.org/3.1/reference/lexical_analysis.html#indentation
  static int getIndentationCount(String spaces) {

    int count = 0;

    for (char ch : spaces.toCharArray()) {
      switch (ch) {
        case '\t':
          count += 8 - (count % 8);
          break;
        default:
          // A normal space char.
          count++;
      }
    }

    return count;
  }

  boolean atStartOfInput() {
    return super.getCharPositionInLine() == 0 && super.getLine() == 1;
  }
}

/**
 * Statements
 */ 

CLASS	: 'class';
PASS 	: 'pass';
WHILE	: 'while';
IF   	: 'if';
ELSE 	: 'else';
ELSIF	: 'elsif';
RETURN  : 'return';
PRINTLN : 'println';

/**
 * Operators
 */ 

ASSIGN  : '=';
AND 	: 'and';
OR		: 'or';
NOT		: '!';
EQUAL	: '==';
NEQUAL	: '!=';
LT		: '<';
LTE		: '<=';
GT		: '>';
GTE		: '>=';

PLUS	: '+';
MINUS	: '-';
STAR	: '*';
SLASH	: '/';

/**
 * Puntuaction
 */

DOT : '.';
COMMA	: ',';
COLON	: ':';
LPARENT : '(';
RPARENT : ')';

/**
 * Types
 */

PRIMITIVE_TYPE : 'int' | 'string' | 'bool';
VOID_TYPE: 'void';

NEW: 'new';

/**
 * Literals
 */

FLOAT   : INTEGER '.' INTEGER;
INTEGER	: [0-9]+ ;
STRING	: '"' ~[\"\r\n]* '"';
TRUE	: 'true' ;
FALSE	: 'false';	

// An identifier (variable, method, classe name ...)
ID 		: [a-zA-Z_][a-zA-Z0-9_]* ;
	
/**
 * Other
 */
	
NEWLINE
 : ( {atStartOfInput()}?   SPACES
   | ( '\r'? '\n' | '\r' ) SPACES?
   )
   {
     String newLine = getText().replaceAll("[^\r\n]+", "");
     String spaces = getText().replaceAll("[\r\n]+", "");
     int next = _input.LA(1);

     if (opened > 0 || next == '\r' || next == '\n' || next == '#') {
       // If we're inside a list or on a blank line, ignore all indents, 
       // dedents and line breaks.
       skip();
     }
     else {
       emit(commonToken(NEWLINE, newLine));

       int indent = getIndentationCount(spaces);
       int previous = indents.isEmpty() ? 0 : indents.peek();

       if (indent == previous) {
         // skip indents of the same size as the present indent-size
         skip();
       }
       else if (indent > previous) {
         indents.push(indent);
         emit(commonToken(LavaParser.INDENT, spaces));
       }
       else {
         // Possibly emit more than 1 DEDENT token.
         while(!indents.isEmpty() && indents.peek() > indent) {
           this.emit(createDedent());
           indents.pop();
         }
       }
     }
   }
 ;
	
SKIP
	: (SPACES | COMMENT) -> skip
	;
	
fragment SPACES
	: [ \t]+
	;
 
fragment COMMENT
	: '#' ~[\r\n]*
	;
	
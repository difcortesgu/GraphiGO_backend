grammar Chocopy;

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
      this.emit(commonToken(ChocopyParser.NEWLINE, "\n"));

      // Now emit as much DEDENT tokens as needed.
      while (!indents.isEmpty()) {
        this.emit(createDedent());
        indents.pop();
      }

      // Put the EOF back on the token stream.
      this.emit(commonToken(ChocopyParser.EOF, "<EOF>"));
    }

    Token next = super.nextToken();

    if (next.getChannel() == Token.DEFAULT_CHANNEL) {
      // Keep track of the last token on the default channel.
      this.lastToken = next;
    }
    return tokens.isEmpty() ? next : tokens.poll();
  }

  private Token createDedent() {
    CommonToken dedent = commonToken(ChocopyParser.DEDENT, "");
    dedent.setLine(this.lastToken.getLine());
    return dedent;
  }

  private CommonToken commonToken(int type, String text) {
    int stop = this.getCharIndex() - 1;
    int start = text.isEmpty() ? stop : stop - text.length() + 1;
    return new CommonToken(this._tokenFactorySourcePair, type, DEFAULT_TOKEN_CHANNEL, start, stop);
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

program : var_def program
    | func_def program
    | class_def program
    | stmt* EOF;

class_def : CLASS ID PAR_IZQ ID PAR_DER DOS_PUNTOS NEWLINE INDENT class_body DEDENT;

class_body : PASS NEWLINE
    | class_body_def;

class_body_def:  var_def class_body_def
    | func_def class_body_def
    | var_def
    | func_def;

func_def : DEF ID PAR_IZQ (typed_var (COMMA typed_var)*)? PAR_DER (EJECUTA type)? DOS_PUNTOS NEWLINE INDENT func_body DEDENT;

func_body : global_decl func_body
    | nonlocal_decl func_body
    | var_def func_body
    | func_def func_body
    | stmt+;

typed_var : ID DOS_PUNTOS type;

type : ID
    | IDSTRING
    | COR_IZQ type COR_DER;

global_decl : GLOBAL ID NEWLINE;

nonlocal_decl : NONLOCAL ID NEWLINE;

var_def : typed_var IGUAL literal NEWLINE;

stmt : simple_stmt end_file
    | IF expr DOS_PUNTOS block (ELIF expr DOS_PUNTOS block )* (ELSE DOS_PUNTOS block)?
    | WHILE expr DOS_PUNTOS block
    | FOR ID IN expr DOS_PUNTOS block;

end_file : NEWLINE | EOF;

simple_stmt : PASS
    | expr
    | RETURN expr?
    | (target IGUAL)+ expr
    | PRINT PAR_IZQ expr PAR_DER;

block : NEWLINE INDENT stmt+ DEDENT;

literal : NONE
    | TRUE
    | FALSE
    | INTEGER
    | IDSTRING
    | STRING;

expr : expr_or IF expr_or ELSE expr
    | expr_or;

expr_or : expr_or OR expr_and
    | expr_and;

expr_and : expr_and AND simple_expr
    | simple_expr;

simple_expr: cexpr
    | NOT simple_expr;

cexpr : cexpr cmp cexpr_sum
    | cexpr_sum;

cexpr_sum : cexpr_sum op_suma cexpr_mul
    | cexpr_mul;

cexpr_mul : cexpr_mul op_mul simple_value
    | simple_value;

simple_value  : ID
    | literal
    | LEN PAR_IZQ expr PAR_DER
    | INPUT PAR_IZQ PAR_DER
    | simple_value PUNTO ID
    | simple_value PUNTO ID PAR_IZQ (expr (COMMA expr)*)? PAR_DER
    | simple_value COR_IZQ expr COR_DER
    | MENOS simple_value
    | COR_IZQ (expr (COMMA expr)*)? COR_DER
    | PAR_IZQ expr PAR_DER
    | ID PAR_IZQ (expr (COMMA expr)*)? PAR_DER;


cmp : DOBLE_IGUAL
    | DIFERENTE
    | MENOR_IGUAL
    | MAYOR_IGUAL
    | MENOR
    | MAYOR
    | IS;

op_suma : MAS
    | MENOS;

op_mul : MULTIPLICA
    | DIV_ENTERA
    | MODULO;

target : ID
    | simple_value PUNTO ID
    | simple_value COR_IZQ expr COR_DER;


NOT : 'not';
AND : 'and';
OR : 'or';
IF : 'if';
ELIF: 'elif';
ELSE : 'else';
INPUT : 'input';
PAR_IZQ : '(';
PAR_DER : ')';
LEN : 'len';
COMMA : ',';
COR_IZQ : '[';
COR_DER : ']';
PUNTO : '.';
MENOS : '-';
CLASS: 'class';
DOS_PUNTOS: ':';
PASS : 'pass';
DEF : 'def';
EJECUTA : '->';
GLOBAL : 'global';
NONLOCAL : 'nonlocal';
DOBLE_IGUAL : '==';
IGUAL : '=';
WHILE : 'while';
FOR : 'for';
IN : 'in';
RETURN : 'return';
PRINT : 'print';
MAS : '+';
MULTIPLICA : '*';
DIV_ENTERA : '//';
MODULO : '%';
DIFERENTE : '!=';
MENOR_IGUAL : '<=';
MAYOR_IGUAL : '>=';
MENOR : '<';
MAYOR : '>';
IS : 'is';
NONE: 'None';
TRUE : 'True';
FALSE : 'False';



INTEGER : [1-9][0-9]* | '0';
ID 		: [a-zA-Z_][a-zA-Z0-9_]* ;
IDSTRING: '"'[a-zA-Z][a-zA-Z0-9_]*'"' ;
STRING: '"'(ESC|.)*?'"' ; //Revisar
fragment
ESC : '\\"' | '\\\\';

LINE_COMMENT 	: '#' ~[\r\n]* -> skip ;
fragment SPACES
 : [ \t]+
 ;

fragment COMMENT
 : '#' ~[\r\n\f]*
 ;

fragment LINE_JOINING
 : '\\' SPACES? ( '\r'? '\n' | '\r' | '\f')
 ;
SKIP_
 : ( SPACES | COMMENT | LINE_JOINING ) -> skip
 ;
NEWLINE
 : ( {atStartOfInput()}?   SPACES
   | ( '\r'? '\n' | '\r' | '\f' ) SPACES?
   )
   {
     String newLine = getText().replaceAll("[^\r\n\f]+", "");
     String spaces = getText().replaceAll("[\r\n\f]+", "");

     // Strip newlines inside open clauses except if we are near EOF. We keep NEWLINEs near EOF to
     // satisfy the final newline needed by the single_put rule used by the REPL.
     int next = _input.LA(1);
     int nextnext = _input.LA(2);
     if (opened > 0 || (nextnext != -1 && (next == '\r' || next == '\n' || next == '\f' || next == '#'))) {
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
         emit(commonToken(ChocopyParser.INDENT, spaces));
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
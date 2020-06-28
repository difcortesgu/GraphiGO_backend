// Generated from C:/Users/diego/Documents/Lenguajes/GraphiGO/grammar\Chocopy.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ChocopyLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NOT=1, AND=2, OR=3, IF=4, ELIF=5, ELSE=6, INPUT=7, PAR_IZQ=8, PAR_DER=9, 
		LEN=10, COMMA=11, COR_IZQ=12, COR_DER=13, PUNTO=14, MENOS=15, CLASS=16, 
		DOS_PUNTOS=17, PASS=18, DEF=19, EJECUTA=20, GLOBAL=21, NONLOCAL=22, DOBLE_IGUAL=23, 
		IGUAL=24, WHILE=25, FOR=26, IN=27, RETURN=28, PRINT=29, MAS=30, MULTIPLICA=31, 
		DIV_ENTERA=32, MODULO=33, DIFERENTE=34, MENOR_IGUAL=35, MAYOR_IGUAL=36, 
		MENOR=37, MAYOR=38, IS=39, NONE=40, TRUE=41, FALSE=42, INTEGER=43, ID=44, 
		IDSTRING=45, STRING=46, LINE_COMMENT=47, SKIP_=48, NEWLINE=49;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NOT", "AND", "OR", "IF", "ELIF", "ELSE", "INPUT", "PAR_IZQ", "PAR_DER", 
			"LEN", "COMMA", "COR_IZQ", "COR_DER", "PUNTO", "MENOS", "CLASS", "DOS_PUNTOS", 
			"PASS", "DEF", "EJECUTA", "GLOBAL", "NONLOCAL", "DOBLE_IGUAL", "IGUAL", 
			"WHILE", "FOR", "IN", "RETURN", "PRINT", "MAS", "MULTIPLICA", "DIV_ENTERA", 
			"MODULO", "DIFERENTE", "MENOR_IGUAL", "MAYOR_IGUAL", "MENOR", "MAYOR", 
			"IS", "NONE", "TRUE", "FALSE", "INTEGER", "ID", "IDSTRING", "STRING", 
			"ESC", "LINE_COMMENT", "SPACES", "COMMENT", "LINE_JOINING", "SKIP_", 
			"NEWLINE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'not'", "'and'", "'or'", "'if'", "'elif'", "'else'", "'input'", 
			"'('", "')'", "'len'", "','", "'['", "']'", "'.'", "'-'", "'class'", 
			"':'", "'pass'", "'def'", "'->'", "'global'", "'nonlocal'", "'=='", "'='", 
			"'while'", "'for'", "'in'", "'return'", "'print'", "'+'", "'*'", "'//'", 
			"'%'", "'!='", "'<='", "'>='", "'<'", "'>'", "'is'", "'None'", "'True'", 
			"'False'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NOT", "AND", "OR", "IF", "ELIF", "ELSE", "INPUT", "PAR_IZQ", "PAR_DER", 
			"LEN", "COMMA", "COR_IZQ", "COR_DER", "PUNTO", "MENOS", "CLASS", "DOS_PUNTOS", 
			"PASS", "DEF", "EJECUTA", "GLOBAL", "NONLOCAL", "DOBLE_IGUAL", "IGUAL", 
			"WHILE", "FOR", "IN", "RETURN", "PRINT", "MAS", "MULTIPLICA", "DIV_ENTERA", 
			"MODULO", "DIFERENTE", "MENOR_IGUAL", "MAYOR_IGUAL", "MENOR", "MAYOR", 
			"IS", "NONE", "TRUE", "FALSE", "INTEGER", "ID", "IDSTRING", "STRING", 
			"LINE_COMMENT", "SKIP_", "NEWLINE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


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


	public ChocopyLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Chocopy.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 52:
			NEWLINE_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void NEWLINE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:

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
			   
			break;
		}
	}
	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 52:
			return NEWLINE_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean NEWLINE_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return atStartOfInput();
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\63\u016b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3"+
		"\17\3\17\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3"+
		"\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3"+
		"\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&"+
		"\3\'\3\'\3(\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3,\3"+
		",\7,\u010c\n,\f,\16,\u010f\13,\3,\5,\u0112\n,\3-\3-\7-\u0116\n-\f-\16"+
		"-\u0119\13-\3.\3.\3.\7.\u011e\n.\f.\16.\u0121\13.\3.\3.\3/\3/\3/\7/\u0128"+
		"\n/\f/\16/\u012b\13/\3/\3/\3\60\3\60\3\60\3\60\5\60\u0133\n\60\3\61\3"+
		"\61\7\61\u0137\n\61\f\61\16\61\u013a\13\61\3\61\3\61\3\62\6\62\u013f\n"+
		"\62\r\62\16\62\u0140\3\63\3\63\7\63\u0145\n\63\f\63\16\63\u0148\13\63"+
		"\3\64\3\64\5\64\u014c\n\64\3\64\5\64\u014f\n\64\3\64\3\64\5\64\u0153\n"+
		"\64\3\65\3\65\3\65\5\65\u0158\n\65\3\65\3\65\3\66\3\66\3\66\5\66\u015f"+
		"\n\66\3\66\3\66\5\66\u0163\n\66\3\66\5\66\u0166\n\66\5\66\u0168\n\66\3"+
		"\66\3\66\3\u0129\2\67\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33"+
		"\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\2a\61c\2e"+
		"\2g\2i\62k\63\3\2\n\3\2\63;\3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\4\2C\\c"+
		"|\4\2\f\f\17\17\4\2\13\13\"\"\4\2\f\f\16\17\2\u0179\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'"+
		"\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63"+
		"\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2"+
		"?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3"+
		"\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2"+
		"\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2a\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\3"+
		"m\3\2\2\2\5q\3\2\2\2\7u\3\2\2\2\tx\3\2\2\2\13{\3\2\2\2\r\u0080\3\2\2\2"+
		"\17\u0085\3\2\2\2\21\u008b\3\2\2\2\23\u008d\3\2\2\2\25\u008f\3\2\2\2\27"+
		"\u0093\3\2\2\2\31\u0095\3\2\2\2\33\u0097\3\2\2\2\35\u0099\3\2\2\2\37\u009b"+
		"\3\2\2\2!\u009d\3\2\2\2#\u00a3\3\2\2\2%\u00a5\3\2\2\2\'\u00aa\3\2\2\2"+
		")\u00ae\3\2\2\2+\u00b1\3\2\2\2-\u00b8\3\2\2\2/\u00c1\3\2\2\2\61\u00c4"+
		"\3\2\2\2\63\u00c6\3\2\2\2\65\u00cc\3\2\2\2\67\u00d0\3\2\2\29\u00d3\3\2"+
		"\2\2;\u00da\3\2\2\2=\u00e0\3\2\2\2?\u00e2\3\2\2\2A\u00e4\3\2\2\2C\u00e7"+
		"\3\2\2\2E\u00e9\3\2\2\2G\u00ec\3\2\2\2I\u00ef\3\2\2\2K\u00f2\3\2\2\2M"+
		"\u00f4\3\2\2\2O\u00f6\3\2\2\2Q\u00f9\3\2\2\2S\u00fe\3\2\2\2U\u0103\3\2"+
		"\2\2W\u0111\3\2\2\2Y\u0113\3\2\2\2[\u011a\3\2\2\2]\u0124\3\2\2\2_\u0132"+
		"\3\2\2\2a\u0134\3\2\2\2c\u013e\3\2\2\2e\u0142\3\2\2\2g\u0149\3\2\2\2i"+
		"\u0157\3\2\2\2k\u0167\3\2\2\2mn\7p\2\2no\7q\2\2op\7v\2\2p\4\3\2\2\2qr"+
		"\7c\2\2rs\7p\2\2st\7f\2\2t\6\3\2\2\2uv\7q\2\2vw\7t\2\2w\b\3\2\2\2xy\7"+
		"k\2\2yz\7h\2\2z\n\3\2\2\2{|\7g\2\2|}\7n\2\2}~\7k\2\2~\177\7h\2\2\177\f"+
		"\3\2\2\2\u0080\u0081\7g\2\2\u0081\u0082\7n\2\2\u0082\u0083\7u\2\2\u0083"+
		"\u0084\7g\2\2\u0084\16\3\2\2\2\u0085\u0086\7k\2\2\u0086\u0087\7p\2\2\u0087"+
		"\u0088\7r\2\2\u0088\u0089\7w\2\2\u0089\u008a\7v\2\2\u008a\20\3\2\2\2\u008b"+
		"\u008c\7*\2\2\u008c\22\3\2\2\2\u008d\u008e\7+\2\2\u008e\24\3\2\2\2\u008f"+
		"\u0090\7n\2\2\u0090\u0091\7g\2\2\u0091\u0092\7p\2\2\u0092\26\3\2\2\2\u0093"+
		"\u0094\7.\2\2\u0094\30\3\2\2\2\u0095\u0096\7]\2\2\u0096\32\3\2\2\2\u0097"+
		"\u0098\7_\2\2\u0098\34\3\2\2\2\u0099\u009a\7\60\2\2\u009a\36\3\2\2\2\u009b"+
		"\u009c\7/\2\2\u009c \3\2\2\2\u009d\u009e\7e\2\2\u009e\u009f\7n\2\2\u009f"+
		"\u00a0\7c\2\2\u00a0\u00a1\7u\2\2\u00a1\u00a2\7u\2\2\u00a2\"\3\2\2\2\u00a3"+
		"\u00a4\7<\2\2\u00a4$\3\2\2\2\u00a5\u00a6\7r\2\2\u00a6\u00a7\7c\2\2\u00a7"+
		"\u00a8\7u\2\2\u00a8\u00a9\7u\2\2\u00a9&\3\2\2\2\u00aa\u00ab\7f\2\2\u00ab"+
		"\u00ac\7g\2\2\u00ac\u00ad\7h\2\2\u00ad(\3\2\2\2\u00ae\u00af\7/\2\2\u00af"+
		"\u00b0\7@\2\2\u00b0*\3\2\2\2\u00b1\u00b2\7i\2\2\u00b2\u00b3\7n\2\2\u00b3"+
		"\u00b4\7q\2\2\u00b4\u00b5\7d\2\2\u00b5\u00b6\7c\2\2\u00b6\u00b7\7n\2\2"+
		"\u00b7,\3\2\2\2\u00b8\u00b9\7p\2\2\u00b9\u00ba\7q\2\2\u00ba\u00bb\7p\2"+
		"\2\u00bb\u00bc\7n\2\2\u00bc\u00bd\7q\2\2\u00bd\u00be\7e\2\2\u00be\u00bf"+
		"\7c\2\2\u00bf\u00c0\7n\2\2\u00c0.\3\2\2\2\u00c1\u00c2\7?\2\2\u00c2\u00c3"+
		"\7?\2\2\u00c3\60\3\2\2\2\u00c4\u00c5\7?\2\2\u00c5\62\3\2\2\2\u00c6\u00c7"+
		"\7y\2\2\u00c7\u00c8\7j\2\2\u00c8\u00c9\7k\2\2\u00c9\u00ca\7n\2\2\u00ca"+
		"\u00cb\7g\2\2\u00cb\64\3\2\2\2\u00cc\u00cd\7h\2\2\u00cd\u00ce\7q\2\2\u00ce"+
		"\u00cf\7t\2\2\u00cf\66\3\2\2\2\u00d0\u00d1\7k\2\2\u00d1\u00d2\7p\2\2\u00d2"+
		"8\3\2\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5\7g\2\2\u00d5\u00d6\7v\2\2\u00d6"+
		"\u00d7\7w\2\2\u00d7\u00d8\7t\2\2\u00d8\u00d9\7p\2\2\u00d9:\3\2\2\2\u00da"+
		"\u00db\7r\2\2\u00db\u00dc\7t\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7p\2\2"+
		"\u00de\u00df\7v\2\2\u00df<\3\2\2\2\u00e0\u00e1\7-\2\2\u00e1>\3\2\2\2\u00e2"+
		"\u00e3\7,\2\2\u00e3@\3\2\2\2\u00e4\u00e5\7\61\2\2\u00e5\u00e6\7\61\2\2"+
		"\u00e6B\3\2\2\2\u00e7\u00e8\7\'\2\2\u00e8D\3\2\2\2\u00e9\u00ea\7#\2\2"+
		"\u00ea\u00eb\7?\2\2\u00ebF\3\2\2\2\u00ec\u00ed\7>\2\2\u00ed\u00ee\7?\2"+
		"\2\u00eeH\3\2\2\2\u00ef\u00f0\7@\2\2\u00f0\u00f1\7?\2\2\u00f1J\3\2\2\2"+
		"\u00f2\u00f3\7>\2\2\u00f3L\3\2\2\2\u00f4\u00f5\7@\2\2\u00f5N\3\2\2\2\u00f6"+
		"\u00f7\7k\2\2\u00f7\u00f8\7u\2\2\u00f8P\3\2\2\2\u00f9\u00fa\7P\2\2\u00fa"+
		"\u00fb\7q\2\2\u00fb\u00fc\7p\2\2\u00fc\u00fd\7g\2\2\u00fdR\3\2\2\2\u00fe"+
		"\u00ff\7V\2\2\u00ff\u0100\7t\2\2\u0100\u0101\7w\2\2\u0101\u0102\7g\2\2"+
		"\u0102T\3\2\2\2\u0103\u0104\7H\2\2\u0104\u0105\7c\2\2\u0105\u0106\7n\2"+
		"\2\u0106\u0107\7u\2\2\u0107\u0108\7g\2\2\u0108V\3\2\2\2\u0109\u010d\t"+
		"\2\2\2\u010a\u010c\t\3\2\2\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d"+
		"\u010b\3\2\2\2\u010d\u010e\3\2\2\2\u010e\u0112\3\2\2\2\u010f\u010d\3\2"+
		"\2\2\u0110\u0112\7\62\2\2\u0111\u0109\3\2\2\2\u0111\u0110\3\2\2\2\u0112"+
		"X\3\2\2\2\u0113\u0117\t\4\2\2\u0114\u0116\t\5\2\2\u0115\u0114\3\2\2\2"+
		"\u0116\u0119\3\2\2\2\u0117\u0115\3\2\2\2\u0117\u0118\3\2\2\2\u0118Z\3"+
		"\2\2\2\u0119\u0117\3\2\2\2\u011a\u011b\7$\2\2\u011b\u011f\t\6\2\2\u011c"+
		"\u011e\t\5\2\2\u011d\u011c\3\2\2\2\u011e\u0121\3\2\2\2\u011f\u011d\3\2"+
		"\2\2\u011f\u0120\3\2\2\2\u0120\u0122\3\2\2\2\u0121\u011f\3\2\2\2\u0122"+
		"\u0123\7$\2\2\u0123\\\3\2\2\2\u0124\u0129\7$\2\2\u0125\u0128\5_\60\2\u0126"+
		"\u0128\13\2\2\2\u0127\u0125\3\2\2\2\u0127\u0126\3\2\2\2\u0128\u012b\3"+
		"\2\2\2\u0129\u012a\3\2\2\2\u0129\u0127\3\2\2\2\u012a\u012c\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012c\u012d\7$\2\2\u012d^\3\2\2\2\u012e\u012f\7^\2\2\u012f"+
		"\u0133\7$\2\2\u0130\u0131\7^\2\2\u0131\u0133\7^\2\2\u0132\u012e\3\2\2"+
		"\2\u0132\u0130\3\2\2\2\u0133`\3\2\2\2\u0134\u0138\7%\2\2\u0135\u0137\n"+
		"\7\2\2\u0136\u0135\3\2\2\2\u0137\u013a\3\2\2\2\u0138\u0136\3\2\2\2\u0138"+
		"\u0139\3\2\2\2\u0139\u013b\3\2\2\2\u013a\u0138\3\2\2\2\u013b\u013c\b\61"+
		"\2\2\u013cb\3\2\2\2\u013d\u013f\t\b\2\2\u013e\u013d\3\2\2\2\u013f\u0140"+
		"\3\2\2\2\u0140\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141d\3\2\2\2\u0142"+
		"\u0146\7%\2\2\u0143\u0145\n\t\2\2\u0144\u0143\3\2\2\2\u0145\u0148\3\2"+
		"\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147f\3\2\2\2\u0148\u0146"+
		"\3\2\2\2\u0149\u014b\7^\2\2\u014a\u014c\5c\62\2\u014b\u014a\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u0152\3\2\2\2\u014d\u014f\7\17\2\2\u014e\u014d\3"+
		"\2\2\2\u014e\u014f\3\2\2\2\u014f\u0150\3\2\2\2\u0150\u0153\7\f\2\2\u0151"+
		"\u0153\4\16\17\2\u0152\u014e\3\2\2\2\u0152\u0151\3\2\2\2\u0153h\3\2\2"+
		"\2\u0154\u0158\5c\62\2\u0155\u0158\5e\63\2\u0156\u0158\5g\64\2\u0157\u0154"+
		"\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159"+
		"\u015a\b\65\2\2\u015aj\3\2\2\2\u015b\u015c\6\66\2\2\u015c\u0168\5c\62"+
		"\2\u015d\u015f\7\17\2\2\u015e\u015d\3\2\2\2\u015e\u015f\3\2\2\2\u015f"+
		"\u0160\3\2\2\2\u0160\u0163\7\f\2\2\u0161\u0163\4\16\17\2\u0162\u015e\3"+
		"\2\2\2\u0162\u0161\3\2\2\2\u0163\u0165\3\2\2\2\u0164\u0166\5c\62\2\u0165"+
		"\u0164\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0168\3\2\2\2\u0167\u015b\3\2"+
		"\2\2\u0167\u0162\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016a\b\66\3\2\u016a"+
		"l\3\2\2\2\25\2\u010d\u0111\u0117\u011f\u0127\u0129\u0132\u0138\u0140\u0146"+
		"\u014b\u014e\u0152\u0157\u015e\u0162\u0165\u0167\4\b\2\2\3\66\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
package steps;

import java.util.ArrayList;
import java.util.List;

public class Flexer {
	private static Flexer instance = new Flexer();
	private String input;
	private int pos;
	private int length;

	private Flexer() {}

	public static Flexer getInstance() {
		if (instance == null) {
			instance = new Flexer();
		}
		return instance;
	}

	public void setInput(String input) {
		this.input = input;
		this.pos = 0;
		this.length = input.length();
	}

	enum TokenType {
		LPAREN,//left parenthesis
		RPAREN,//right parenthesis
		SETQ,
		FUNC,
		LAMBDA,
		PROG,
		COND,
		WHILE,
		RETURN,
		BREAK,
		PLUS,
		MINUS,
		TIMES,
		DIVIDE,
		INTEGER,//literal
		REAL,//literal
		BOOLEAN,//literal
		NULL,
		ATOM,
		HEAD,
		TAIL,
		CONS,
		EQUAL,
		NONEQUAL,
		LESS,
		LESSEQ,
		GREATER,
		GREATEREQ,
		ISINT,
		ISREAL,
		ISBOOL,
		ISNULL,
		ISATOM,
		ISLIST,
		AND,
		OR,
		XOR,
		NOT,
		EVAL,
		QUOTE, //NO NEED TO EVALUATE
		EOF//end of file
	}

	public class Token {
		TokenType type;
		String value;

		Token(TokenType type, String value) {
			this.type = type;
			this.value = value;
		}

		@Override
		public String toString() {
			return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
		}
	}

	//Lexical analyzer
	public List<Token> tokenize() throws Exception {
		List<Token> tokens = new ArrayList<>();
		while (pos < length) {
			char curr = input.charAt(pos);

			switch (curr) {
				case '(':
					tokens.add(new Token(TokenType.LPAREN, "("));
					pos++;
					break;
				case ')':
					tokens.add(new Token(TokenType.RPAREN, ")"));
					pos++;
					break;
				case '\'':
					tokens.add(new Token(TokenType.QUOTE, "'"));
					//single quote in front of element is the short form of function
					//prevents evaluating
					pos++;
					break;
				default:
					if (Character.isWhitespace(curr)) {
						pos++;
					} else if (Character.isDigit(curr) || curr == '+' || curr == '-') {
						tokens.add(parseNumber());
					} else if (Character.isLetter(curr)) {
						tokens.add(parseIdOrKeyword());
					} else {
						throw new Exception("Unknown character: " + curr);
					}
			}
		}
		tokens.add(new Token(TokenType.EOF, ""));
		return tokens;
	}

	//parse numbers
	public Token parseNumber() throws Exception {
		StringBuilder num = new StringBuilder();
		boolean isReal = false;
		boolean isNegative = false;

		if (input.charAt(pos) == '-') {
			isNegative = true;
			pos++;
			if (!Character.isDigit(input.charAt(pos))) {
				throw new Exception("UNEXPECTED CHARACTER: -");
			}
		} else if (input.charAt(pos) == '+') {
			pos++;
			if (!Character.isDigit(input.charAt(pos))) {
				throw new Exception("UNEXPECTED CHARACTER: +");
			}
		}
		while (pos < length && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
			if (input.charAt(pos) == '.') {
				isReal = true;
			}
			num.append(input.charAt(pos));
			pos++;
		}
		if (isNegative) {
			num.insert(0, '-');
		}

		return new Token(isReal ? TokenType.REAL : TokenType.INTEGER, num.toString());
	}

	private Token parseIdOrKeyword() {
		StringBuilder ident = new StringBuilder();
		while (pos < length && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
			ident.append(input.charAt(pos));
			pos++;
		}

		String id = ident.toString();

		//search for keywords.
		switch (id) {
			case "setq":
				//2nd param gets evaluated and becomes the new value of atom from the first param replacing
				//its previous value

				// (setq Atom Element)

				return new Token(TokenType.SETQ, id);
			case "func":
				// func Atom List Element
				return new Token(TokenType.FUNC, id);
			case "lambda":
				return new Token(TokenType.LAMBDA, id);
			case "prog":
				return new Token(TokenType.PROG, id);
			case "cond":
				return new Token(TokenType.COND, id);
			case "while":
				return new Token(TokenType.WHILE, id);
			case "return":
				return new Token(TokenType.RETURN, id);
			case "break":
				return new Token(TokenType.BREAK, id);
			case "plus":
				return new Token(TokenType.PLUS, id);
			case "minus":
				return new Token(TokenType.MINUS, id);
			case "times":
				return new Token(TokenType.TIMES, id);
			case "divide":
				return new Token(TokenType.DIVIDE, id);
			case "head":
				return new Token(TokenType.HEAD, id);
			case "tail":
				return new Token(TokenType.TAIL, id);
			case "cons":
				return new Token(TokenType.CONS, id);
			case "equal":
				return new Token(TokenType.EQUAL, id);
			case "nonequal":
				return new Token(TokenType.NONEQUAL, id);
			case "less":
				return new Token(TokenType.LESS, id);
			case "lesseq":
				return new Token(TokenType.LESSEQ, id);
			case "greater":
				return new Token(TokenType.GREATER, id);
			case "greatereq":
				return new Token(TokenType.GREATEREQ, id);
			case "isint":
				return new Token(TokenType.ISINT, id);
			case "isreal":
				return new Token(TokenType.ISREAL, id);
			case "isbool":
				return new Token(TokenType.ISBOOL, id);
			case "isnull":
				return new Token(TokenType.ISNULL, id);
			case "isatom":
				return new Token(TokenType.ISATOM, id);
			case "islist":
				return new Token(TokenType.ISLIST, id);
			case "and":
				return new Token(TokenType.AND, id);
			case "or":
				return new Token(TokenType.OR, id);
			case "xor":
				return new Token(TokenType.XOR, id);
			case "not":
				return new Token(TokenType.NOT, id);
			case "eval":
				return new Token(TokenType.EVAL, id);
			case "true":
				return new Token(TokenType.BOOLEAN, id);
			case "false":
				return new Token(TokenType.BOOLEAN, id);
			case "quote":
				return new Token(TokenType.QUOTE, id);
			case "":
				return new Token(TokenType.EOF, id);
			default:
				return new Token(TokenType.ATOM, id);
		}
	}
}



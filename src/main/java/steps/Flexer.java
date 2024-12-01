package steps;

import java.util.ArrayList;
import java.util.List;

public class Flexer {
	private String input;
	private int pos;
	private int length;
	private int line;

	public Flexer() {
	}


	public void setInput(String input) {
		this.input = input;
		this.pos = 0;
		this.length = input.length();
		this.line = 1;
	}


	//Lexical analyzer
	public List<Token> tokenize() throws Exception {
		List<Token> tokens = new ArrayList<>();
		while (pos < length) {
			char curr = input.charAt(pos);

			switch (curr) {
				case '(' -> {
					tokens.add(new Token(TokenType.LPAREN, "(", line));
					pos++;
				}
				case ')' -> {
					tokens.add(new Token(TokenType.RPAREN, ")", line));
					pos++;
				}
				case '\'' -> {
					tokens.add(new Token(TokenType.QUOTE, "'", line));
					//single quote in front of element is the short form of function
					//prevents evaluating
					pos++;
				}
				case '\n' -> {
					line++;
					pos++;
				}
				default -> {
					if (Character.isWhitespace(curr)) {
						pos++;
					} else if (Character.isDigit(curr) || curr == '+' || curr == '-') {
						tokens.add(parseNumber());
					} else if (Character.isLetter(curr)) {
						tokens.add(parseIdOrKeyword());
					} else {
						throw new Exception("Unknown character: " + curr + "at line: " + line);
					}
				}
			}
		}
		tokens.add(new Token(TokenType.EOF, "", line));
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
				throw new Exception("UNEXPECTED CHARACTER: - at line: " + line);
			}
		} else if (input.charAt(pos) == '+') {
			pos++;
			if (!Character.isDigit(input.charAt(pos))) {
				throw new Exception("UNEXPECTED CHARACTER: + at line: " + line);
			}
		}
		while (pos < length && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
			if (input.charAt(pos) == '.') {
				isReal = true;
			}
			num.append(input.charAt(pos));
			pos++;
		}
		if (Character.isLetter(input.charAt(pos))) {
			throw new Exception("NOT A NUMBER at line: " + line);
		}
		if (isNegative) {
			num.insert(0, '-');
		}

		return new Token(isReal ? TokenType.REAL : TokenType.INTEGER, num.toString(), line);
	}

	private Token parseIdOrKeyword() {
		StringBuilder ident = new StringBuilder();
		while (pos < length && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
			ident.append(input.charAt(pos));
			pos++;
		}

		String id = ident.toString();

		//search for keywords.
		return switch (id) {
			case "setq" -> new Token(TokenType.SETQ, id, line);
			case "null" -> new Token(TokenType.NULL, id, line);
			case "func" -> new Token(TokenType.FUNC, id, line);
			case "lambda" -> new Token(TokenType.LAMBDA, id, line);
			case "prog" -> new Token(TokenType.PROG, id, line);
			case "cond" -> new Token(TokenType.COND, id, line);
			case "while" -> new Token(TokenType.WHILE, id, line);
			case "return" -> new Token(TokenType.RETURN, id, line);
			case "break" -> new Token(TokenType.BREAK, id, line);
			case "plus" -> new Token(TokenType.PLUS, id, line);
			case "minus" -> new Token(TokenType.MINUS, id, line);
			case "times" -> new Token(TokenType.TIMES, id, line);
			case "divide" -> new Token(TokenType.DIVIDE, id, line);
			case "head" -> new Token(TokenType.HEAD, id, line);
			case "tail" -> new Token(TokenType.TAIL, id, line);
			case "cons" -> new Token(TokenType.CONS, id, line);
			case "equal" -> new Token(TokenType.EQUAL, id, line);
			case "nonequal" -> new Token(TokenType.NONEQUAL, id, line);
			case "less" -> new Token(TokenType.LESS, id, line);
			case "lesseq" -> new Token(TokenType.LESSEQ, id, line);
			case "greater" -> new Token(TokenType.GREATER, id, line);
			case "greatereq" -> new Token(TokenType.GREATEREQ, id, line);
			case "isint" -> new Token(TokenType.ISINT, id, line);
			case "isreal" -> new Token(TokenType.ISREAL, id, line);
			case "isbool" -> new Token(TokenType.ISBOOL, id, line);
			case "isnull" -> new Token(TokenType.ISNULL, id, line);
			case "isatom" -> new Token(TokenType.ISATOM, id, line);
			case "islist" -> new Token(TokenType.ISLIST, id, line);
			case "and" -> new Token(TokenType.AND, id, line);
			case "or" -> new Token(TokenType.OR, id, line);
			case "xor" -> new Token(TokenType.XOR, id, line);
			case "not" -> new Token(TokenType.NOT, id, line);
			case "eval" -> new Token(TokenType.EVAL, id, line);
			case "true" -> new Token(TokenType.BOOLEAN, id, line);
			case "false" -> new Token(TokenType.BOOLEAN, id, line);
			case "quote" -> new Token(TokenType.QUOTE, id, line);
			case "'" -> new Token(TokenType.QUOTE, id, line);
			case "" -> new Token(TokenType.EOF, id, line);
			default -> new Token(TokenType.ATOM, id, line);
		};
	}
}



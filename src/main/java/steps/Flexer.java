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
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(", line));
                    pos++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")", line));
                    pos++;
                    break;
                case '\'':
                    tokens.add(new Token(TokenType.QUOTE, "'", line));
                    //single quote in front of element is the short form of function
                    //prevents evaluating
                    pos++;
                    break;
                case '\n':
                    line++;
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
                        throw new Exception("Unknown character: " + curr + "at line: " + line);
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
        switch (id) {
            case "setq":
                //2nd param gets evaluated and becomes the new value of atom from the first param replacing
                //its previous value

                // (setq Atom Element)

                return new Token(TokenType.SETQ, id, line);
            case "null":
                return new Token(TokenType.NULL, id, line);
            case "func":
                // func Atom List Element
                return new Token(TokenType.FUNC, id, line);
            case "lambda":
                return new Token(TokenType.LAMBDA, id, line);
            case "prog":
                return new Token(TokenType.PROG, id, line);
            case "cond":
                return new Token(TokenType.COND, id, line);
            case "while":
                return new Token(TokenType.WHILE, id, line);
            case "return":
                return new Token(TokenType.RETURN, id, line);
            case "break":
                return new Token(TokenType.BREAK, id, line);
            case "plus":
                return new Token(TokenType.PLUS, id, line);
            case "minus":
                return new Token(TokenType.MINUS, id, line);
            case "times":
                return new Token(TokenType.TIMES, id, line);
            case "divide":
                return new Token(TokenType.DIVIDE, id, line);
            case "head":
                return new Token(TokenType.HEAD, id, line);
            case "tail":
                return new Token(TokenType.TAIL, id, line);
            case "cons":
                return new Token(TokenType.CONS, id, line);
            case "equal":
                return new Token(TokenType.EQUAL, id, line);
            case "nonequal":
                return new Token(TokenType.NONEQUAL, id, line);
            case "less":
                return new Token(TokenType.LESS, id, line);
            case "lesseq":
                return new Token(TokenType.LESSEQ, id, line);
            case "greater":
                return new Token(TokenType.GREATER, id, line);
            case "greatereq":
                return new Token(TokenType.GREATEREQ, id, line);
            case "isint":
                return new Token(TokenType.ISINT, id, line);
            case "isreal":
                return new Token(TokenType.ISREAL, id, line);
            case "isbool":
                return new Token(TokenType.ISBOOL, id, line);
            case "isnull":
                return new Token(TokenType.ISNULL, id, line);
            case "isatom":
                return new Token(TokenType.ISATOM, id, line);
            case "islist":
                return new Token(TokenType.ISLIST, id, line);
            case "and":
                return new Token(TokenType.AND, id, line);
            case "or":
                return new Token(TokenType.OR, id, line);
            case "xor":
                return new Token(TokenType.XOR, id, line);
            case "not":
                return new Token(TokenType.NOT, id, line);
            case "eval":
                return new Token(TokenType.EVAL, id, line);
            case "true":
                return new Token(TokenType.BOOLEAN, id, line);
            case "false":
                return new Token(TokenType.BOOLEAN, id, line);
            case "quote":
                return new Token(TokenType.QUOTE, id, line);
            case "'":
                return new Token(TokenType.QUOTE, id, line);
            case "":
                return new Token(TokenType.EOF, id, line);
            default:
                return new Token(TokenType.ATOM, id, line);
        }
    }
}



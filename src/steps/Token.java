package steps;

public class Token {
	TokenType type;
	String value;
	int line;

	Token(TokenType type, String value, int line) {
		this.type = type;
		this.value = value;
		this.line = line;
	}

	public TokenType getType() {
		return type;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
	}
}

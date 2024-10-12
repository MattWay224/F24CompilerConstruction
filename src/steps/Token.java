package steps;

public class Token {
	TokenType type;
	String value;

	Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	public TokenType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Token{" + "type=" + type + ", value='" + value + '\'' + '}';
	}
}

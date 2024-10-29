package testing;

import steps.Flexer;
import steps.Token;
import things.InputFileReader;

import java.util.List;

public class FlexerTest {
	public String test(String testFilePath) throws Exception {
		String input = InputFileReader.readInputFromFile(testFilePath);
		Flexer lexer = new Flexer();
		lexer.setInput(input);
		List<Token> tokens = lexer.tokenize();
		return convertTokensToString(tokens);
	}

	private String convertTokensToString(List<Token> tokens) {
		StringBuilder output = new StringBuilder();
		for (Token token : tokens) {
			output.append(token.toString()).append("\n");
		}
		return output.toString();
	}
}

package flexer;

import steps.Flexer;
import steps.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class FlexerTest {
	Scanner scanner;
	StringBuilder input;
	Flexer lexer;

	public String test(String test) throws FileNotFoundException {
		File file = new File(test);
		this.scanner = new Scanner(file);
		this.input = new StringBuilder();

		while (scanner.hasNext()) {
			input.append(scanner.nextLine()).append("\n");
		}
		this.lexer = new Flexer();
		this.lexer.setInput(input.toString());
		try {
			List<Token> tokens = lexer.tokenize();

			StringBuilder output = new StringBuilder();
			for (Token token : tokens) {
				output.append(token.toString()).append("\n");
			}
			return output.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return null;
	}
}


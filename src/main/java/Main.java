import ast.nodes.ASTNode;
import steps.ASTPrinter;
import steps.Flexer;
import steps.Parser;
import steps.Token;
import visitors.PrettyVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		PrettyVisitor visitor = new PrettyVisitor();
		Flexer lexer = new Flexer();
		Parser parser = new Parser();

		for (int i = 1; i < 18; i++) {
			File text = new File("src/main/resources/inputs/test" + i + ".txt");
			File outL = new File("src/main/resources/flexer/outputs/output" + i + ".txt");
			File outSA = new File("src/main/resources/fsyntaxer/outputs/output" + i + ".txt");

			try (Scanner scanner = new Scanner(text);
				 Writer writerL = new FileWriter(outL);
				 Writer writerSA = new FileWriter(outSA)) {

				StringBuilder input = new StringBuilder();
				while (scanner.hasNext()) {
					input.append(scanner.nextLine()).append("\n");
				}

				lexer.setInput(input.toString());
				List<Token> tokens = lexer.tokenize();

				StringBuilder output = new StringBuilder();
				for (Token token : tokens) {
					output.append(token.toString()).append("\n");
				}
				writerL.write(output.toString());

				parser.setTokens(tokens);
				ASTNode ast = parser.parse();

				ASTPrinter.printAST(writerSA, ast, visitor, 0);
            } catch (IOException e) {
				System.err.println("Error: " + e.getMessage() + "Test: " + i);
			} catch (Exception e) {
				System.out.println(i);
				throw new RuntimeException(e);
			}
		}
	}
}

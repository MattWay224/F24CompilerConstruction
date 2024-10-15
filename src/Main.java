import ast.nodes.ASTNode;
import steps.FSyntaxAnalysis;
import steps.Flexer;
import steps.Token;
import visitors.PrettyVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		PrettyVisitor visitor = new PrettyVisitor();
		Flexer lexer = Flexer.getInstance();
		FSyntaxAnalysis fSyntaxAnalysis = FSyntaxAnalysis.getInstance();

		for (int i = 1; i < 18; i++) {
			File text = new File("src/testing/inputs/test" + i + ".txt");
			File outL = new File("src/testing/flexer/outputs/output" + i + ".txt");
			File outSA = new File("src/testing/syntax/outputs/output" + i + ".txt");

			try (Scanner scanner = new Scanner(text);
				 FileWriter writerL = new FileWriter(outL);
				 FileWriter writerSA = new FileWriter(outSA)) {

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

				fSyntaxAnalysis.setter(tokens);
				ASTNode ast = fSyntaxAnalysis.parse();

				printAST(writerSA, ast, visitor, 0);

			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage() + "Test: "+i);
			} catch (Exception e) {
				System.out.println(i);
				throw new RuntimeException(e);
			}
		}
	}

	private static void printAST(FileWriter writer, ASTNode node, PrettyVisitor visitor, int depth) throws IOException {
		for (int i = 0; i < depth; i++) {
			writer.write("  ");
		}

		writer.write(node.accept(visitor) + "\n");

		for (ASTNode child : node.getChildren()) {
			printAST(writer, child, visitor, depth + 1);
		}
	}
}

import ast.ASTNodeFactory;
import ast.nodes.*;
import steps.FSyntaxAnalysis;
import steps.Flexer;
import visitors.PrettyVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		ASTNodeFactory factory = new ASTNodeFactory();
		PrettyVisitor visitor = new PrettyVisitor();
		for (int i = 1; i < 10; i++) {
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

				Flexer lexer = new Flexer(input.toString());
				List<Flexer.Token> tokens = lexer.tokenize();

				StringBuilder output = new StringBuilder();
				for (Flexer.Token token : tokens) {
					output.append(token.toString()).append("\n");
				}

				writerL.write(output.toString());
				FSyntaxAnalysis fSyntaxAnalysis = new FSyntaxAnalysis(tokens, visitor, factory);

				List<ASTNode> ast = fSyntaxAnalysis.parse();

				for (ASTNode node : ast) {
					printAST(node, 0);  //each node starting at depth 0
				}
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			} catch (Exception e) {
				System.out.println(i);
				throw new RuntimeException(e);
			}
		}
	}

	// Нужно переписать для работы с filewriter и подумать про visitor, чтобы не импортить сюда все ноды
	static void printAST(ASTNode node, int depth) {
		for (int i = 0; i < depth; i++) System.out.print("  ");

		//current node
		System.out.println(node);

		// print children with recursion
		if (node instanceof FunctionNode func) {
			printAST(func.getBody(), depth + 1);
		} else if (node instanceof AssignmentNode assign) {
			printAST(assign.getValue(), depth + 1);
		} else if (node instanceof ConditionNode cond) {
			for (ConditionBranch branch : cond.getBranches()) {
				printAST(branch.getCondition(), depth + 1);
				printAST(branch.getAction(), depth + 1);
			}
		}
	}
}

import ast.ASTNodeFactory;
import steps.FSyntaxAnalysis;
import steps.Flexer;
import steps.Token;
import visitors.PrettyVisitor;
import ast.nodes.ASTNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		ASTNodeFactory factory = new ASTNodeFactory();
		PrettyVisitor visitor = new PrettyVisitor();
		Flexer lexer = Flexer.getInstance();
		FSyntaxAnalysis fSyntaxAnalysis = FSyntaxAnalysis.getInstance();

		for (int i = 0; i < 17; i++) {
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

				fSyntaxAnalysis.setter(tokens, visitor, factory);
				List<ASTNode> ast = fSyntaxAnalysis.parse();

				for (ASTNode node : ast) {
					 writerSA.write(node.accept(visitor)+"\n");  //each node starting at depth 0
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
//	static void printAST(FileWriter writer, ASTNode node,PrettyVisitor visitor, int depth) throws IOException {
//		for (int i = 0; i < depth; i++) writer.write("  ");
//
//		//current node
//		writer.write(node.accept(visitor) + "\n");
//
//		// print children with recursion
//		if (node instanceof FunctionNode func) {
//			printAST(writer, func.getBody(), visitor,depth + 1);
//		} else if (node instanceof AssignmentNode assign) {
//			printAST(writer, assign.getValue(), visitor,depth + 1);
//		} else if (node instanceof ConditionNode cond) {
//			for (ConditionBranch branch : cond.getBranches()) {
//				printAST(writer, branch.getCondition(), visitor,depth + 1);
//				printAST(writer, branch.getAction(), visitor,depth + 1);
//			}
//		}
//	}
}

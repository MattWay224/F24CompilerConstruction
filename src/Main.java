import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File text = new File("src/test.txt");
        File outL = new File("src/output.txt");
        //File outSA = new File("src/outputSA.txt");

        try (Scanner scanner = new Scanner(text); FileWriter writerL = new FileWriter(outL)) {
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

            FSyntaxAnalysis fSyntaxAnalysis = new FSyntaxAnalysis(tokens);

            List<ASTNode> ast = fSyntaxAnalysis.parse();

            for (ASTNode node : ast) {
                printAST(node, 0);  //each node starting at depth 0
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void printAST(ASTNode node, int depth) {
        for (int i = 0; i < depth; i++) System.out.print("  ");

        //current node
        System.out.println(node);

        // print children with recursion
        if (node instanceof FunctionNode) {
            FunctionNode func = (FunctionNode) node;
            printAST(func.body, depth + 1);
        } else if (node instanceof AssignmentNode) {
            AssignmentNode assign = (AssignmentNode) node;
            printAST(assign.value, depth + 1);
        } else if (node instanceof ConditionNode) {
            ConditionNode cond = (ConditionNode) node;
            for (ConditionBranch branch : cond.branches) {
                printAST(branch.condition, depth + 1);
                printAST(branch.action, depth + 1);
            }
        }
    }

}

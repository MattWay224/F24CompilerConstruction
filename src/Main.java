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

            ASTNodeFactory factory = new ASTNodeFactory();
            PrettyVisitor visitor = new PrettyVisitor();

            FSyntaxAnalysis fSyntaxAnalysis = new FSyntaxAnalysis(tokens, visitor, factory);

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
    // Нужно переписать
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

package steps;

import ast.nodes.ASTNode;
import visitors.PrettyVisitor;

import java.io.IOException;
import java.io.Writer;

public class ASTPrinter {
    public static void printAST(Writer writer, ASTNode node, PrettyVisitor visitor, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            writer.write("  ");
        }

        writer.write(node.accept(visitor) + "\n");

        for (ASTNode child : node.getChildren()) {
            printAST(writer, child, visitor, depth + 1);
        }
    }
}

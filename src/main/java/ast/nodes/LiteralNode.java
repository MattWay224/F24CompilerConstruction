package ast.nodes;

import visitors.ASTVisitor;

//literals
public class LiteralNode extends ASTNode {
    String value;
    int line;

    public LiteralNode(String value, int line) {
        this.value = value;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralNode(this);
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}

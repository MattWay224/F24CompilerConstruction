package ast.nodes;

import visitors.ASTVisitor;

public class BooleanNode extends ASTNode{
    String value;
    int line;
    public BooleanNode(String value, int line) {
        this.value = value;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBoolNode(this);
    }

    public int getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }
}

package ast.nodes;

import visitors.ASTVisitor;

public class BooleanNode extends ASTNode{
    Boolean value;
    int line;
    public BooleanNode(Boolean value, int line) {
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

    public Boolean getValue() {
        return value;
    }
}

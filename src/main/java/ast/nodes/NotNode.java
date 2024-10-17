package ast.nodes;

import visitors.ASTVisitor;

public class NotNode extends ASTNode {
    ASTNode element;
    int line;

    public NotNode(ASTNode element, int line) {
        this.element = element;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitNotNode(this);
    }

    public ASTNode getElement() {
        return element;
    }

    public int getLine() {
        return line;
    }
}

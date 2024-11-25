package ast.nodes;

import visitors.ASTVisitor;

public class NotNode extends ASTNode {
    ASTNode element;
    int line;

    private BooleanNode constantValue;

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

    public BooleanNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(BooleanNode constantValue) {
        this.constantValue = constantValue;
    }

    @Override
    public NotNode clone() {
        NotNode clonedNode = new NotNode(element, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }
}

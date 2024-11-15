package ast.nodes;

import visitors.ASTVisitor;

public class TailNode extends ASTNode {
    ASTNode tail;
    int line;
    private ListNode constantValue;

    public TailNode(ASTNode tail, int line) {
        this.tail = tail;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitTailNode(this);
    }

    public ASTNode getTail() {
        return tail;
    }

    public int getLine() {
        return line;
    }

    public ListNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(ListNode constantValue) {
        this.constantValue = constantValue;
    }

    @Override
    public ASTNode clone() {
        TailNode clonedNode = new TailNode(tail, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }
}

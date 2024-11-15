package ast.nodes;

import visitors.ASTVisitor;

public class HeadNode extends ASTNode {
    ASTNode head;
    int line;
    private ASTNode constantValue;

    public HeadNode(ASTNode head, int line) {
        this.head = head;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitHeadNode(this);
    }

    public ASTNode getHead() {
        return head;
    }

    public int getLine() {
        return line;
    }

    public ASTNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(ASTNode constantValue) {
        this.constantValue = constantValue;
    }

    @Override
    public HeadNode clone() {
        HeadNode clonedNode = new HeadNode(head, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }
}

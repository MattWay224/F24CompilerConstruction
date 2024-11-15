package ast.nodes;

import visitors.ASTVisitor;

public class ReturnNode extends ASTNode {
    private final ASTNode returnValue;
    int line;

    private ASTNode constantValue;

    public ReturnNode(ASTNode returnValue, int line) {
        this.returnValue = returnValue;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitReturnNode(this);
    }

    public ASTNode getReturnValue() {
        return returnValue;
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
    public ASTNode clone() {
        ReturnNode clonedNode = new ReturnNode(returnValue, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }
}

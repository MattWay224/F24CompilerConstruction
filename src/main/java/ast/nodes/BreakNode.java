package ast.nodes;

import visitors.ASTVisitor;

public class BreakNode extends ASTNode {
    int line;

    public BreakNode(int line) {
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitBreakNode(this);
    }

    @Override
    public BreakNode clone() {
        BreakNode clonedNode = new BreakNode(line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public int getLine() {
        return line;
    }
}
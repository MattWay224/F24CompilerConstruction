package ast.nodes;

import visitors.ASTVisitor;

public class NullNode extends ASTNode {
    int line;

    public NullNode(int line) {
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitNullNode(this);
    }

    @Override
    public NullNode clone() {
        NullNode clonedNode = new NullNode(line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public int getLine() {
        return line;
    }
}

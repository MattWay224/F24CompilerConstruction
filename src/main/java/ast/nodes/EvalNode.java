package ast.nodes;

import visitors.ASTVisitor;

public class EvalNode extends ASTNode {
    ASTNode node;
    int line;

    public EvalNode(ASTNode node, int line) {
        this.node = node;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitEvalNode(this);
    }

    @Override
    public EvalNode clone() {
        EvalNode clonedNode = new EvalNode(node, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public ASTNode getNode() {
        return node;
    }

    public int getLine() {
        return line;
    }
}

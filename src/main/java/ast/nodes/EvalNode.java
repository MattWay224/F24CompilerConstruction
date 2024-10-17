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

    public ASTNode getNode() {
        return node;
    }

    public int getLine() {
        return line;
    }
}

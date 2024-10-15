package ast.nodes;

import visitors.ASTVisitor;

public class QuoteNode extends ASTNode{
    private final ASTNode quotedExpr;
    int line;

    public QuoteNode(ASTNode quotedExpr, int line) {
        this.quotedExpr = quotedExpr;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitQuoteNode(this);
    }
    public ASTNode getQuotedExpr() {
        return quotedExpr;
    }

    public int getLine() {
        return line;
    }
}

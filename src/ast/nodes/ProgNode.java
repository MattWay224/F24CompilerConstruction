package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class ProgNode extends ASTNode {
    private final List<ASTNode> statements;
    private final List<ASTNode> finalExpression;
    int lineOp;
    int lineClo;

    public ProgNode(List<ASTNode> statements, List<ASTNode> finalExpression, int lineOp, int lineClo) {
        this.statements = statements;
        this.finalExpression = finalExpression;
        this.lineOp = lineOp;
        this.lineClo = lineClo;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgNode(this);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public List<ASTNode> getFinalExpression() {
        return finalExpression;
    }

    public int getLineOp() {
        return lineOp;
    }

    public int getLineClo() {
        return lineClo;
    }
}

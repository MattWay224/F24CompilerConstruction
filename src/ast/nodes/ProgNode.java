package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class ProgNode extends ASTNode {
    private final List<ASTNode> statements;
    int lineOp;
    int lineClo;

    public ProgNode(List<ASTNode> statements, int lineOp, int lineClo) {
        this.statements = statements;
        this.lineOp = lineOp;
        this.lineClo = lineClo;

        for (ASTNode statement : this.statements) {
            addChild(statement);
        }
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitProgNode(this);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public int getLineOp() {
        return lineOp;
    }

    public int getLineClo() {
        return lineClo;
    }
}

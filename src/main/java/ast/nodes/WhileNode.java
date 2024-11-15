package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> body;
    int lineOp;
    int lineClo;

    public WhileNode(ASTNode condition, List<ASTNode> body, int lineOp, int lineClo) {
        this.condition = condition;
        this.body = body;
        this.lineOp = lineOp;
        this.lineClo = lineClo;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitWhileNode(this);
    }

    @Override
    public ASTNode clone() {
        WhileNode clonedNode = new WhileNode(condition.clone(), body, lineOp, lineClo);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public ASTNode getCondition() {
        return condition;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public int getLineClo() {
        return lineClo;
    }

    public int getLineOp() {
        return lineOp;
    }
}

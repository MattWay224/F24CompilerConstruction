package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

//cond
public class ConditionNode extends ASTNode {
    List<ConditionBranch> branches;
    ASTNode defaultAction;
    int lineOp;
    int lineClo;

    public ConditionNode(List<ConditionBranch> branches, ASTNode defaultAction, int lineOp, int lineClo) {
        this.branches = branches;
        this.defaultAction = defaultAction;
        this.lineOp = lineOp;
        this.lineClo = lineClo;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitConditionNode(this);
    }

    public List<ConditionBranch> getBranches() {
        return branches;
    }

    public ASTNode getDefaultAction() {
        return defaultAction;
    }

    public int getLineClo() {
        return lineClo;
    }

    public int getLineOp() {
        return lineOp;
    }
}

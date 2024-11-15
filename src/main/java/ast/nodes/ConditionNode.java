package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

//cond
public class ConditionNode extends ASTNode {
    List<ConditionBranch> branches;
    ASTNode defaultAction;
    int lineOp;
    int lineClo;

    private boolean conditionval;
    private ASTNode constantValue;

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

    @Override
    public ConditionNode clone() {
        ConditionNode clonedNode = new ConditionNode(branches, defaultAction, lineOp, lineClo);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
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


    public ASTNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(ASTNode constantValue) {
        this.constantValue = constantValue;
    }

    public void setConditionval(boolean conditionval) {
        this.conditionval = conditionval;
    }
    public boolean getConditionval() {
        return conditionval;
    }
}

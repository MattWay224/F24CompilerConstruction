package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

//cond
public class ConditionNode extends ASTNode {
	List<ConditionBranch> branches;
	ASTNode defaultAction;

	public ConditionNode(List<ConditionBranch> branches, ASTNode defaultAction) {
		this.branches = branches;
		this.defaultAction = defaultAction;
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
}

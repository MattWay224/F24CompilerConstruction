package ast;

import visitors.ASTVisitor;

//single cond branch
public class ConditionBranch {
	ASTNode condition;
	ASTNode action;

	public ConditionBranch(ASTNode condition, ASTNode action) {
		this.condition = condition;
		this.action = action;
	}

	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitConditionBranch(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public ASTNode getAction() {
		return action;
	}
}

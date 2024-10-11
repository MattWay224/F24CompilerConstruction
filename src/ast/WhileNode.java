package ast;

import visitors.ASTVisitor;

public class WhileNode extends ASTNode {
	private final ASTNode condition;
	private final ASTNode body;

	public WhileNode(ASTNode condition, ASTNode body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitWhileNode(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public ASTNode getBody() {
		return body;
	}
}

package ast;

import visitors.ASTVisitor;

public class ReturnNode extends ASTNode {
	private final ASTNode returnValue;

	public ReturnNode(ASTNode returnValue) {
		this.returnValue = returnValue;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitReturnNode(this);
	}

	public ASTNode getReturnValue() {
		return returnValue;
	}
}

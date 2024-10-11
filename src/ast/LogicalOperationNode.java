package ast;

import visitors.ASTVisitor;

public class LogicalOperationNode extends ASTNode {
	String operator;
	ASTNode leftElement;
	ASTNode rightElement;

	public LogicalOperationNode(String operator, ASTNode leftElement, ASTNode rightElement) {
		this.operator = operator;
		this.leftElement = leftElement;
		this.rightElement = rightElement;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitLogicalOperationNode(this);
	}

	public String getOperator() {
		return operator;
	}

	public ASTNode getLeftElement() {
		return leftElement;
	}

	public ASTNode getRightElement() {
		return rightElement;
	}
}

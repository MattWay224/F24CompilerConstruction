package ast.nodes;

import visitors.ASTVisitor;

public class ComparisonNode extends ASTNode {
	String comparison;
	ASTNode leftElement;
	ASTNode rightElement;

	public ComparisonNode(String comparison, ASTNode leftElement, ASTNode rightElement) {
		this.comparison = comparison;
		this.leftElement = leftElement;
		this.rightElement = rightElement;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitComparisonNode(this);
	}

	public String getComparison() {
		return comparison;
	}

	public ASTNode getLeftElement() {
		return leftElement;
	}

	public ASTNode getRightElement() {
		return rightElement;
	}
}

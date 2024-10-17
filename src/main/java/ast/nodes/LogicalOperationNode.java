package ast.nodes;

import visitors.ASTVisitor;

public class LogicalOperationNode extends ASTNode {
	String operator;
	ASTNode leftElement;
	ASTNode rightElement;
	int lineOp;
	int lineClo;

	public LogicalOperationNode(String operator, ASTNode leftElement, ASTNode rightElement, int lineOp, int lineClo) {
		this.operator = operator;
		this.leftElement = leftElement;
		this.rightElement = rightElement;
		this.lineOp = lineOp;
		this.lineClo = lineClo;
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

	public int getLineClo() {
		return lineClo;
	}

	public int getLineOp() {
		return lineOp;
	}
}

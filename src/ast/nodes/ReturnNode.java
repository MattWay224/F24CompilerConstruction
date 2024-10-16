package ast.nodes;

import visitors.ASTVisitor;

public class ReturnNode extends ASTNode {
	private final ASTNode returnValue;
	int line;

	public ReturnNode(ASTNode returnValue, int line) {
		this.returnValue = returnValue;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitReturnNode(this);
	}

	public ASTNode getReturnValue() {
		return returnValue;
	}

	public int getLine() {
		return line;
	}
}

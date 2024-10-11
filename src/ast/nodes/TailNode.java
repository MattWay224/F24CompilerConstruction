package ast.nodes;

import visitors.ASTVisitor;

public class TailNode extends ASTNode {
	ASTNode tail;

	public TailNode(ASTNode tail) {
		this.tail = tail;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitTailNode(this);
	}

	public ASTNode getTail() {
		return tail;
	}
}

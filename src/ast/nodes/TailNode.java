package ast.nodes;

import visitors.ASTVisitor;

public class TailNode extends ASTNode {
	ASTNode tail;
	int line;

	public TailNode(ASTNode tail, int line) {
		this.tail = tail;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitTailNode(this);
	}

	public ASTNode getTail() {
		return tail;
	}

	public int getLine() {
		return line;
	}
}

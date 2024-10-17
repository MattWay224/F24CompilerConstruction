package ast.nodes;

import visitors.ASTVisitor;

public class ConsNode extends ASTNode {
	private final ASTNode head;
	private final ASTNode tail;
	int line;

	public ConsNode(ASTNode head, ASTNode tail, int line) {
		this.head = head;
		this.tail = tail;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitConsNode(this);
	}

	public ASTNode getHead() {
		return head;
	}

	public ASTNode getTail() {
		return tail;
	}

	public int getLine() {
		return line;
	}
}

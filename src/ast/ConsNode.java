package ast;

import visitors.ASTVisitor;

public class ConsNode extends ASTNode {
	private final ASTNode head;
	private final ASTNode tail;

	public ConsNode(ASTNode head, ASTNode tail) {
		this.head = head;
		this.tail = tail;
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
}

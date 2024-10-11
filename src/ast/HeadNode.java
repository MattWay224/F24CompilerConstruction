package ast;

import visitors.ASTVisitor;

public class HeadNode extends ASTNode {
	ASTNode head;

	public HeadNode(ASTNode head) {
		this.head = head;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitHeadNode(this);
	}

	public ASTNode getHead() {
		return head;
	}
}

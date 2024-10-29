package ast.nodes;

import visitors.ASTVisitor;

public class HeadNode extends ASTNode {
	ASTNode head;
	int line;

	public HeadNode(ASTNode head, int line) {
		this.head = head;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitHeadNode(this);
	}

	public ASTNode getHead() {
		return head;
	}

	public int getLine() {
		return line;
	}
}

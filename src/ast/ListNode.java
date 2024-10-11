package ast;

import visitors.ASTVisitor;

import java.util.List;

public class ListNode extends ASTNode {
	List<ASTNode> elements;

	public ListNode(List<ASTNode> elements) {
		this.elements = elements;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitListNode(this);
	}

	public List<ASTNode> getElements() {
		return elements;
	}
}

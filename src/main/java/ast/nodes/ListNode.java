package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class ListNode extends ASTNode {
	List<ASTNode> elements;
	int line;

	public ListNode(List<ASTNode> elements, int line) {
		this.elements = elements;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitListNode(this);
	}

	public List<ASTNode> getElements() {
		return elements;
	}

	public int getLine() {
		return line;
	}
}

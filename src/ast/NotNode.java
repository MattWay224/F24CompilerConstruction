package ast;

import visitors.ASTVisitor;

public class NotNode extends ASTNode {
	ASTNode element;

	public NotNode(ASTNode element) {
		this.element = element;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitNotNode(this);
	}

	public ASTNode getElement() {
		return element;
	}
}

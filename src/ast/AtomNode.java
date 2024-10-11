package ast;

import visitors.ASTVisitor;

//atom
public class AtomNode extends ASTNode {
	String value;

	public AtomNode(String value) {
		this.value = value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitAtomNode(this);
	}

	public String getValue() {
		return value;
	}
}

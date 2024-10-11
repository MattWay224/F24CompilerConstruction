package ast;

import visitors.ASTVisitor;

//literals
public class LiteralNode extends ASTNode {
	String value;

	public LiteralNode(String value) {
		this.value = value;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitLiteralNode(this);
	}

	public String getValue() {
		return value;
	}
}

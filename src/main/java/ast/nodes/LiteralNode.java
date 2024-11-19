package ast.nodes;

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

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isInt() {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public boolean isReal() {
		if (isInt()) {
			return false;
		}
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}

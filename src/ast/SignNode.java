package ast;

import visitors.ASTVisitor;

public class SignNode extends ASTNode {
	private final String sign;

	public SignNode(String sign) {
		this.sign = sign;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitSignNode(this);
	}

	public String getSign() {
		return sign;
	}
}

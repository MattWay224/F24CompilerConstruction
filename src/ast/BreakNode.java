package ast;

import visitors.ASTVisitor;

public class BreakNode extends ASTNode {
	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitBreakNode(this);
	}
}
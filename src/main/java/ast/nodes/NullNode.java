package ast.nodes;

import visitors.ASTVisitor;

public class NullNode extends ASTNode {
	int line;

	public NullNode(int line) {
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitNullNode(this);
	}

	public int getLine() {
		return line;
	}
}

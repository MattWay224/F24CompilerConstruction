package ast.nodes;

import visitors.ASTVisitor;

public class BreakNode extends ASTNode {
    int line;
    public BreakNode(int line) {
        this.line=line;
    }

    @Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitBreakNode(this);
	}

    public int getLine() {
        return line;
    }
}
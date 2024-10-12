package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class WhileNode extends ASTNode {
	private final ASTNode condition;
	private final List<ASTNode> body;

	public WhileNode(ASTNode condition, List<ASTNode> body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitWhileNode(this);
	}

	public ASTNode getCondition() {
		return condition;
	}

	public List<ASTNode> getBody() {
		return body;
	}
}

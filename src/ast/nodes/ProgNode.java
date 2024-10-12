package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class ProgNode extends ASTNode {
	private final List<ASTNode> statements;
	private final List<ASTNode> finalExpression;

	public ProgNode(List<ASTNode> statements, List<ASTNode> finalExpression) {
		this.statements = statements;
		this.finalExpression = finalExpression;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitProgNode(this);
	}

	public List<ASTNode> getStatements() {
		return statements;
	}

	public List<ASTNode> getFinalExpression() {
		return finalExpression;
	}
}

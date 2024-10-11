package ast;

import visitors.ASTVisitor;

public class PredicateNode extends ASTNode {
	String predicate;
	ASTNode element;

	public PredicateNode(String predicate, ASTNode element) {
		this.predicate = predicate;
		this.element = element;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitPredicateNode(this);
	}

	public String getPredicate() {
		return predicate;
	}

	public ASTNode getElement() {
		return element;
	}
}

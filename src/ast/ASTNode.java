package ast;


// Base class for AST nodes
public abstract class ASTNode {
	public abstract <R> R accept(visitors.ASTVisitor<R> visitor);
}

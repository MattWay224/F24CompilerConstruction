package ast.nodes;


import visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

// Base class for AST nodes
public abstract class ASTNode {
	private NodeType type;

	private ASTNode parent;
	private final List<ASTNode> children;

	public ASTNode() {
		this.children = new ArrayList<>();
	}

	public List<ASTNode> getChildren() {
		return children;
	}

	public void addChild(ASTNode child) {
		child.setParent(this);
		children.add(child);
	}

	public ASTNode getParent() {
		return parent;
	}

	public void setParent(ASTNode parent) {
		this.parent = parent;
	}

	public abstract <R> R accept(ASTVisitor<R> visitor);

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public enum NodeType {
		ASSIGNMENT,
		ATOM,
		BOOL,
		BREAK,
		COMP,
		COND,
		CONS,
		EVAL,
		FUNC,
		FUNCCALL,
		HEAD,
		LAMBDA,
		LAMBDACALL,
		LIST,
		LITERAL,
		LOGICALOP,
		NOT,
		NULL,
		OPERATION,
		PREDICATE,
		PROG,
		QUOTE,
		RETURN,
		SIGN,
		TAIL,
		WHILE,
		VOID
	}
}
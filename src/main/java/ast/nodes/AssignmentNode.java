package ast.nodes;

import visitors.ASTVisitor;

//setq
public class AssignmentNode extends ASTNode {
	String variable;
	ASTNode value;
	int line;

	public AssignmentNode(String variable, ASTNode value, int line) {
		this.variable = variable;
		this.value = value;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitAssignmentNode(this);
	}

	public String getVariable() {
		return variable;
	}

	public ASTNode getValue() {
		return value;
	}

	public int getLine() {
		return line;
	}
}

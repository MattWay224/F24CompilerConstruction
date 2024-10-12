package ast.nodes;

import visitors.ASTVisitor;

//setq
public class AssignmentNode extends ASTNode {
	String variable;
	ASTNode value;

	public AssignmentNode(String variable, ASTNode value) {
		this.variable = variable;
		this.value = value;
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
}

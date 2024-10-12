package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class OperationNode extends ASTNode {
	String operator;
	List<ASTNode> operands;

	public OperationNode(String operator, List<ASTNode> operands, Boolean sign) {
		this.operator = operator;
		this.operands = operands;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitOperationNode(this);
	}

	public String getOperator() {
		return operator;
	}

	public List<ASTNode> getOperands() {
		return operands;
	}
}

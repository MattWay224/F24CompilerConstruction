package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class FunctionCallNode extends ASTNode {
	String functionName;
	List<ASTNode> parameters;

	public FunctionCallNode(String functionName, List<ASTNode> parameters) {
		this.functionName = functionName;
		this.parameters = parameters;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitFunctionCallNode(this);
	}

	public String getFunctionName() {
		return functionName;
	}

	public List<ASTNode> getParameters() {
		return parameters;
	}
}

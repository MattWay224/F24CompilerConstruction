package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class FunctionCallNode extends ASTNode {
	String functionName;
	List<ASTNode> parameters;
	int line;

	public FunctionCallNode(String functionName, List<ASTNode> parameters, int line) {
		this.functionName = functionName;
		this.parameters = parameters;
		this.line = line;
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

	public int getLine() {
		return line;
	}
}

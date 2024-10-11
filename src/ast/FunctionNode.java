package ast;

import visitors.ASTVisitor;

import java.util.List;

//func
public class FunctionNode extends ASTNode {
	String functionName;
	List<String> parameters;
	ASTNode body;

	public FunctionNode(String functionName, List<String> parameters, ASTNode body) {
		this.functionName = functionName;
		this.parameters = parameters;
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitFunctionNode(this);
	}

	public String getFunctionName() {
		return functionName;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public  ASTNode getBody() {
		return body;
	}
}
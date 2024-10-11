package ast;

import visitors.ASTVisitor;

import java.util.List;

public class LambdaNode extends ASTNode {
	List<String> parameters;
	ASTNode body;

	public LambdaNode(List<String> parameters, ASTNode body) {
		this.parameters = parameters;
		this.body = body;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitLambdaNode(this);
	}

	public List<String> getParameters() {
		return parameters;
	}

	public ASTNode getBody() {
		return body;
	}
}

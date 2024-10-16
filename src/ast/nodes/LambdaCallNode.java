package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class LambdaCallNode extends ASTNode {
	String lambdaName;
	List<ASTNode> parameters;
	int line;

	public LambdaCallNode(String lambdaName, List<ASTNode> parameters, int line) {
		this.lambdaName = lambdaName;
		this.parameters = parameters;
		this.line = line;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor) {
		return visitor.visitLambdaCallNode(this);
	}

	public String getLambdaName() {
		return lambdaName;
	}

	public List<ASTNode> getParameters() {
		return parameters;
	}

	public int getLine() {
		return line;
	}
}

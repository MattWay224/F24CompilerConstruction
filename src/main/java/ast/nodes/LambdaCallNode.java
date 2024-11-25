package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class LambdaCallNode extends ASTNode {
    String lambdaName;
    List<ASTNode> parameters;
    int line;


    private ASTNode constantValue;

    public LambdaCallNode(String lambdaName, List<ASTNode> parameters, int line) {
        this.lambdaName = lambdaName;
        this.parameters = parameters;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLambdaCallNode(this);
    }

    @Override
    public LambdaCallNode clone() {
        LambdaCallNode clonedNode = new LambdaCallNode(lambdaName, parameters, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
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

    public ASTNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(ASTNode constantValue) {
        this.constantValue = constantValue;
    }
}

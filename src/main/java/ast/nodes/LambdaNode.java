package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class LambdaNode extends ASTNode {
    List<String> parameters;
    ASTNode body;
    int line;

    public LambdaNode(List<String> parameters, ASTNode body, int line) {
        this.parameters = parameters;
        this.body = body;
        this.line = line;
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

    public int getLine() {
        return line;
    }
}

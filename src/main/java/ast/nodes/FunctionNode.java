package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

//func
public class FunctionNode extends ASTNode {
    private NodeType returnType;
    String functionName;
    List<String> parameters;
    ASTNode body;
    int lineOp;
    int lineClo;

    public FunctionNode(String functionName, List<String> parameters, ASTNode body, int lineOp, int lineClo, NodeType returnType) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
        this.lineOp = lineOp;
        this.lineClo = lineClo;
        this.returnType=returnType;
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

    public ASTNode getBody() {
        return body;
    }

    public int getLineOp() {
        return lineOp;
    }

    public int getLineClo() {
        return lineClo;
    }

    public void setReturnType(NodeType returnType) {
        this.returnType = returnType;
    }

    public NodeType getReturnType() {
        return returnType;
    }
}
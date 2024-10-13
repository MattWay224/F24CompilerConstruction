package ast.nodes;

import visitors.ASTVisitor;

import java.util.List;

public class OperationNode extends ASTNode {
    String operator;
    List<ASTNode> operands;
    int line;

    public OperationNode(String operator, List<ASTNode> operands, Boolean sign, int line) {
        this.operator = operator;
        this.operands = operands;
        this.line = line;
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

    public int getLine() {
        return line;
    }
}

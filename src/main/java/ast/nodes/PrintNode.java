package ast.nodes;

import visitors.ASTVisitor;

public class PrintNode extends ASTNode {
    ASTNode expression;
    int line;

    public PrintNode(ASTNode expression, int line) {
        this.expression = expression;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitPrintNode(this);
    }

    @Override
    public ASTNode clone() {
        PrintNode clonedNode = new PrintNode(expression.clone(), line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public int getLine() {
        return line;
    }

    public ASTNode getExpression() {
        return expression;
    }


}

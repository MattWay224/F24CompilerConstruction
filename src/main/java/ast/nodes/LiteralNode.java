package ast.nodes;

import visitors.ASTVisitor;

//literals
public class LiteralNode extends ASTNode {
    String value;
    int line;

    public LiteralNode(String value, int line) {
        this.value = value;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitLiteralNode(this);
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isInt() {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isReal() {
        if (isInt()) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public LiteralNode clone() {
        LiteralNode clonedNode = new LiteralNode(value, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

}

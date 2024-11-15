package ast.nodes;

import visitors.ASTVisitor;

//atom
public class AtomNode extends ASTNode {
    String value;
    int line;
    String eq;

    public AtomNode(String value, int line) {
        this.value = value;
        this.line = line;
        this.eq = null;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitAtomNode(this);
    }

    @Override
    public AtomNode clone() {
        AtomNode clonedNode = new AtomNode(value, line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public void setEq(String eq) {
        this.eq = eq;
    }

    public String getEq() {
        return eq;
    }
}

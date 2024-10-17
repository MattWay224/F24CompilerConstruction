package ast.nodes;

import visitors.ASTVisitor;

//atom
public class AtomNode extends ASTNode {
    String value;
    int line;

    public AtomNode(String value, int line) {
        this.value = value;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitAtomNode(this);
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}

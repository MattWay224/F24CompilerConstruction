package ast.nodes;

import visitors.ASTVisitor;

public class PredicateNode extends ASTNode {
    String predicate;
    ASTNode element;
    int line;
    private BooleanNode constantValue;

    public PredicateNode(String predicate, ASTNode element, int line) {
        this.predicate = predicate;
        this.element = element;
        this.line = line;
    }

    @Override
    public <R> R accept(ASTVisitor<R> visitor) {
        return visitor.visitPredicateNode(this);
    }

    public String getPredicate() {
        return predicate;
    }

    public ASTNode getElement() {
        return element;
    }

    public int getLine() {
        return line;
    }

    public BooleanNode getConstantValue() {
        return constantValue;
    }

    @Override
    public void setConstantValue(BooleanNode constantValue) {
        this.constantValue=constantValue;
    }

    @Override
    public PredicateNode clone() {
        PredicateNode clonedNode = new PredicateNode(predicate, element.clone(), line);
        for (ASTNode child : this.getChildren()) {
            clonedNode.addChild(child.clone());
        }
        return clonedNode;
    }
}

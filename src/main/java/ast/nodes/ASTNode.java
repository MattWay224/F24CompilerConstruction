package ast.nodes;


import visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

// Base class for AST nodes
public abstract class ASTNode {
    private final List<ASTNode> children;

    public ASTNode() {
        this.children = new ArrayList<>();
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public abstract <R> R accept(ASTVisitor<R> visitor);
}
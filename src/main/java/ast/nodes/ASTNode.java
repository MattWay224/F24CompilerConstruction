package ast.nodes;


import visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

// Base class for AST nodes
public abstract class ASTNode {
    private NodeType type;

    private ASTNode parent;

    private boolean isQuoted;

    private boolean isEvaluated;

    private final List<ASTNode> children;

    public ASTNode() {
        this.children = new ArrayList<>();
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        child.setParent(this);
        children.add(child);
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public abstract <R> R accept(ASTVisitor<R> visitor);

    public void setQuoted(boolean quoted) {
        isQuoted = quoted;
    }

    public boolean isQuoted() {
        return isQuoted;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void setConstantValue(LiteralNode constantValue) {
    }

    public void setConstantValue(BooleanNode constantValue) {
    }

    public void setConstantValue(ListNode constantValue) {
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isReal() {
        return false;
    }

    public void setConstantValue(ASTNode constantValue) {
    }

    ;

    public void setConstantValue(Object constantValue) {
    }

    public abstract ASTNode clone();


    public enum NodeType {
        ASSIGNMENT,
        ATOM,
        BOOL,
        BREAK,
        COMP,
        COND,
        CONS,
        EVAL,
        FUNC,
        FUNCCALL,
        HEAD,
        LAMBDA,
        LAMBDACALL,
        LIST,
        LITERAL,
        LOGICALOP,
        NOT,
        NULL,
        OPERATION,
        PREDICATE,
        PROG,
        QUOTE,
        RETURN,
        SIGN,
        TAIL,
        WHILE,
        PRINT,
        VOID
    }
}
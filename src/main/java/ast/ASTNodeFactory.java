package ast;

import ast.nodes.*;

import java.util.List;

public class ASTNodeFactory {

    public ASTNode createLiteralNode(String value, int line) {
        return new LiteralNode(value, line);
    }

    public ASTNode createAtomNode(String value, int line) {
        return new AtomNode(value, line);
    }

    public OperationNode createOperationNode(String operator, List<ASTNode> operands, boolean isUnary, int line) {
        return new OperationNode(operator, operands, isUnary, line);
    }

    public ComparisonNode createComparisonNode(String operator, ASTNode left, ASTNode right, int line) {
        return new ComparisonNode(operator, left, right, line);
    }

    public LogicalOperationNode createLogicalOperationNode(String operator, ASTNode left, ASTNode right, int lineOp, int lineClo) {
        return new LogicalOperationNode(operator, left, right, lineOp, lineClo);
    }

    public PredicateNode createPredicateNode(String predicate, ASTNode element, int line) {
        return new PredicateNode(predicate, element, line);
    }

    public HeadNode createHeadNode(ASTNode listExpr, int line) {
        return new HeadNode(listExpr, line);
    }

    public TailNode createTailNode(ASTNode listExpr, int line) {
        return new TailNode(listExpr, line);
    }

    public ConsNode createConsNode(ASTNode head, ASTNode tail, int line) {
        return new ConsNode(head, tail, line);
    }

    public WhileNode createWhileNode(ASTNode condition, List<ASTNode> body, int lineOp, int lineClo) {
        return new WhileNode(condition, body, lineOp, lineClo);
    }

    public ReturnNode createReturnNode(ASTNode returnValue, int line) {
        return new ReturnNode(returnValue, line);
    }

    public BreakNode createBreakNode(int line) {
        return new BreakNode(line);
    }

    public ProgNode createProgNode(List<ASTNode> statements, int lineOp, int lineClo) {
        return new ProgNode(statements, lineOp, lineClo);
    }

    public FunctionNode createFunctionNode(String functionName, List<String> parameters, ASTNode body, int lineOp, int lineClo, ASTNode.NodeType returnType) {
        return new FunctionNode(functionName, parameters, body, lineOp, lineClo, returnType);
    }

    public FunctionCallNode createFunctionCallNode(String functionName, List<ASTNode> parameters, int line) {
        return new FunctionCallNode(functionName, parameters, line);
    }

    public FunctionCallNode createLambdaCallNode(String lambdaName, List<ASTNode> parameters, int line) {
        return new FunctionCallNode(lambdaName, parameters, line);
    }

    public LambdaNode createLambdaNode(List<String> parameters, ASTNode body, int line) {
        return new LambdaNode(parameters, body, line);
    }

    public ConditionNode createConditionNode(List<ConditionBranch> branches, ASTNode defaultAction, int lineOp, int lineClo) {
        return new ConditionNode(branches, defaultAction, lineOp, lineClo);
    }

    public ConditionBranch createConditionBranch(ASTNode condition, ASTNode action) {
        return new ConditionBranch(condition, action);
    }

    public AssignmentNode createAssignmentNode(String variable, ASTNode value, int line) {
        return new AssignmentNode(variable, value, line);
    }

    public ListNode createListNode(List<ASTNode> list, int line) {
        return new ListNode(list, line);
    }

    public NotNode createNotNode(ASTNode element, int line) {
        return new NotNode(element, line);
    }

    public QuoteNode createQuoteNode(ASTNode value, int line) {
        return new QuoteNode(value, line);
    }

    public EvalNode createEvalNode(ASTNode node, int line) {
        return new EvalNode(node, line);
    }

    public ASTNode createNullNode(String value, int line) {
        return new NullNode(line);
    }

    public ASTNode createBooleanNode(boolean value, int line) {
        return new BooleanNode(value, line);
    }
}


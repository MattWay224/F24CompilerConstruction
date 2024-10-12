package ast;

import ast.nodes.*;
import java.util.List;

public class ASTNodeFactory {

	public ASTNode createLiteralNode(String value) {
		return new LiteralNode(value);
	}

	public ASTNode createAtomNode(String value) {
		return new AtomNode(value);
	}

	public OperationNode createOperationNode(String operator, List<ASTNode> operands, boolean isUnary) {
		return new OperationNode(operator, operands, isUnary);
	}

	public ComparisonNode createComparisonNode(String operator, ASTNode left, ASTNode right) {
		return new ComparisonNode(operator, left, right);
	}

	public LogicalOperationNode createLogicalOperationNode(String operator, ASTNode left, ASTNode right) {
		return new LogicalOperationNode(operator, left, right);
	}

	public PredicateNode createPredicateNode(String predicate, ASTNode element) {
		return new PredicateNode(predicate, element);
	}

	public HeadNode createHeadNode(ASTNode listExpr) {
		return new HeadNode(listExpr);
	}

	public TailNode createTailNode(ASTNode listExpr) {
		return new TailNode(listExpr);
	}

	public ConsNode createConsNode(ASTNode head, ASTNode tail) {
		return new ConsNode(head, tail);
	}

	public WhileNode createWhileNode(ASTNode condition, ASTNode body) {
		return new WhileNode(condition, body);
	}

	public ReturnNode createReturnNode(ASTNode returnValue) {
		return new ReturnNode(returnValue);
	}

	public BreakNode createBreakNode() {
		return new BreakNode();
	}

	public ProgNode createProgNode(List<ASTNode> statements, ASTNode finalExpression) {
		return new ProgNode(statements, finalExpression);
	}

	public FunctionNode createFunctionNode(String functionName, List<String> parameters, ASTNode body) {
		return new FunctionNode(functionName, parameters, body);
	}

	public FunctionCallNode createFunctionCallNode(String functionName, List<ASTNode> parameters) {
		return new FunctionCallNode(functionName, parameters);
	}

	public LambdaNode createLambdaNode(List<String> parameters, ASTNode body) {
		return new LambdaNode(parameters, body);
	}

	public ConditionNode createConditionNode(List<ConditionBranch> branches, ASTNode defaultAction) {
		return new ConditionNode(branches, defaultAction);
	}

	public ConditionBranch createConditionBranch(ASTNode condition, ASTNode action) {
		return new ConditionBranch(condition, action);
	}

	public AssignmentNode createAssignmentNode(String variable, ASTNode value) {
		return new AssignmentNode(variable, value);
	}

	public ListNode createListNode(List<ASTNode> list) {
		return new ListNode(list);
	}

	public NotNode createNotNode(ASTNode element) {
		return new NotNode(element);
	}

	// Add other node creation methods as needed...
}


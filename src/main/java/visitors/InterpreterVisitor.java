package visitors;

import ast.nodes.*;
import visitors.ASTVisitor;

public class InterpreterVisitor implements ASTVisitor<String> {
	@Override
	public String visitAssignmentNode(AssignmentNode node) {
		return null;
	}

	@Override
	public String visitAtomNode(AtomNode node) {
		return null;
	}

	@Override
	public String visitBreakNode(BreakNode node) {
		return null;
	}

	@Override
	public String visitComparisonNode(ComparisonNode node) {
		return null;
	}

	@Override
	public String visitConditionNode(ConditionNode node) {
		return null;
	}

	@Override
	public String visitConsNode(ConsNode node) {
		return null;
	}

	@Override
	public String visitFunctionNode(FunctionNode node) {
		return null;
	}

	@Override
	public String visitFunctionCallNode(FunctionCallNode node) {
		return null;
	}

	@Override
	public String visitHeadNode(HeadNode node) {
		return null;
	}

	@Override
	public String visitLambdaNode(LambdaNode node) {
		return null;
	}

	@Override
	public String visitListNode(ListNode node) {
		return null;
	}

	@Override
	public String visitLiteralNode(LiteralNode node) {
		return null;
	}

	@Override
	public String visitLogicalOperationNode(LogicalOperationNode node) {
		return null;
	}

	@Override
	public String visitNotNode(NotNode node) {
		return null;
	}

	@Override
	public String visitOperationNode(OperationNode node) {
		return null;
	}

	@Override
	public String visitPredicateNode(PredicateNode node) {
		return null;
	}

	@Override
	public String visitProgNode(ProgNode node) {
		return null;
	}

	@Override
	public String visitReturnNode(ReturnNode node) {
		return null;
	}

	@Override
	public String visitSignNode(SignNode node) {
		return null;
	}

	@Override
	public String visitTailNode(TailNode node) {
		return null;
	}

	@Override
	public String visitWhileNode(WhileNode node) {
		return null;
	}

	@Override
	public String visitConditionBranch(ConditionBranch branch) {
		return null;
	}

	@Override
	public String visitQuoteNode(QuoteNode node) {
		return null;
	}

	@Override
	public String visitLambdaCallNode(LambdaCallNode node) {
		return null;
	}

	@Override
	public String visitEvalNode(EvalNode node) {
		return null;
	}

	@Override
	public String visitNullNode(NullNode nullNode) {
		return null;
	}

	@Override
	public String visitBoolNode(BooleanNode booleanNode) {
		return null;
	}
}
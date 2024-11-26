package steps;

import ast.nodes.*;

import java.util.ArrayList;
import java.util.List;

public class FSemanter {
	public void checkArithmeticOperation(ASTNode operation) throws Exception {
		String operator = ((OperationNode) operation).getOperator();
		List<ASTNode> operands = ((OperationNode) operation).getOperands();

		if (operands.isEmpty()) {
			throw new Exception("ARITHMETIC OPERATION " + operator + " HAS NO OPERANDS");
		}
	}

	private void checkLogicalOperation(LogicalOperationNode operation) throws Exception {
		String operator = operation.getOperator();
		ASTNode leftOperand = operation.getLeftElement();
		ASTNode rightOperand = operation.getRightElement();

		if (leftOperand == null || rightOperand == null) {
			throw new Exception("LOGICAL OPERATION " + operator + " HAS NO OPERANDS");
		}

		if (leftOperand.getType() != ASTNode.NodeType.BOOL || rightOperand.getType() != ASTNode.NodeType.BOOL) {
			throw new Exception("TYPE ERROR: LOGICAL OPERATORS REQUIRE BOOLEAN OPERANDS on line "
					+ operation.getLineClo());
		}
	}

	private void checkComparisonOperation(ComparisonNode operation) throws Exception {
		String comparison = operation.getComparison();
		ASTNode leftOperand = operation.getLeftElement();
		ASTNode rightOperand = operation.getRightElement();

		if (leftOperand == null || rightOperand == null) {
			throw new Exception("COMPARISON " + comparison + " HAS NO OPERANDS");
		}

		if (leftOperand.getType() != rightOperand.getType()) {
			throw new Exception("TYPE ERROR IN COMPARISON: " + leftOperand.getType()
					+ " CANNOT BE COMPARED WITH " + rightOperand.getType()
					+ " on line " + operation.getLine());
		}
	}


	public void analyze(ASTNode root) throws Exception {
		traverseAndCheck(root);
	}

	private void traverseAndCheck(ASTNode node) throws Exception {
		if (node == null) return;

		else if (node.getClass().getSimpleName().equals("OperationNode")) {
			checkArithmeticOperation(node);
			simplifyExpression(node);

		} else if (node.getClass().getSimpleName().equals("ComparisonNode")) {
			checkComparisonOperation((ComparisonNode) node);

		} else if (node.getClass().getSimpleName().equals("LogicalOperationNode")) {
			checkLogicalOperation((LogicalOperationNode) node);
		}
		for (ASTNode child : node.getChildren()) {
			traverseAndCheck(child);
		}
	}

	private Number evalInt(String operator, List<ASTNode> operands) throws Exception {
		int firstOperand = Integer.parseInt(((LiteralNode) operands.get(0)).getValue());
		int secondOperand = Integer.parseInt(((LiteralNode) operands.get(1)).getValue());

		switch (operator) {
			case "plus" -> {
				return firstOperand + secondOperand;
			}
			case "minus" -> {
				return firstOperand - secondOperand;
			}
			case "times" -> {
				return firstOperand * secondOperand;
			}
			case "divide" -> {
				if (secondOperand == 0) {
					throw new Exception("Division by zero");
				}
				return (double) firstOperand / secondOperand;
			}
			default -> throw new Exception("Unsupported operator: " + operator);
		}
	}

	private void simplifyExpression(ASTNode node) {

	}

	private double evalReal(String operator, List<ASTNode> operands) throws Exception {
		double firstOperand = Double.parseDouble(((LiteralNode) operands.get(0)).getValue());
		double secondOperand = Double.parseDouble(((LiteralNode) operands.get(1)).getValue());

		switch (operator) {
			case "plus" -> {
				return firstOperand + secondOperand;
			}
			case "minus" -> {
				return firstOperand - secondOperand;
			}
			case "times" -> {
				return firstOperand * secondOperand;
			}
			case "divide" -> {
				if (secondOperand == 0) {
					throw new Exception("Division by zero");
				}
				return firstOperand / secondOperand;
			}
			default -> throw new Exception("Unsupported operator: " + operator);
		}
	}

	private void replaceNodeInParent(ASTNode parent, ASTNode oldNode, ASTNode newNode) {
		if (parent == null) return;
		List<ASTNode> children = parent.getChildren();
		int index = children.indexOf(oldNode);
		if (index >= 0) {
			children.set(index, newNode);
			newNode.setParent(parent);
		}
	}
}

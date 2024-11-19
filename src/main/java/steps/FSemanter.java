package steps;

import ast.nodes.*;

import java.util.List;

public class FSemanter {
	public void checkArithmeticOperation(ASTNode operation) throws Exception {
		String operator = ((OperationNode) operation).getOperator();
		List<ASTNode> operands = ((OperationNode) operation).getOperands();

		if (operands.isEmpty()) {
			throw new Exception("ARITHMETIC OPERATION " + operator + " HAS NO OPERANDS");
		}

		ASTNode.NodeType expectedType = operands.get(0).getType();
		for (ASTNode operand : operands) {
			if (operand.getType() != expectedType) {
				throw new Exception("TYPE ERROR IN " + operator + " OPERATION: EXPECTED " + expectedType
						+ ", FOUND " + operand.getType() + " on line " + ((OperationNode) operation).getLine());
			}
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

		else if (node.getClass().getSimpleName().equals("OperationNode") && !node.isConstant()) {
			checkArithmeticOperation(node);
			simplifyExpression(node);

		} else if (node.getClass().getSimpleName().equals("ComparisonNode") && !node.isConstant()) {
			checkComparisonOperation((ComparisonNode) node);
			simplifyExpression(node);

		} else if (node.getClass().getSimpleName().equals("LogicalOperationNode")) {
			checkLogicalOperation((LogicalOperationNode) node);
			simplifyExpression(node);
		}
		for (ASTNode child : node.getChildren()) {
			traverseAndCheck(child);
		}
	}

	//Constant Expression Simplification
	private void simplifyExpression(ASTNode operation) throws Exception {
		ASTNode parent = operation.getParent();

		if (operation.getClass().getSimpleName().equals("OperationNode")) {
			OperationNode opNode = (OperationNode) operation;
			List<ASTNode> operands = opNode.getOperands();

			if (operands.stream().allMatch(ASTNode::isInt)) {
				Number result = evalInt(opNode.getOperator(), operands);
				LiteralNode constantNode = new LiteralNode(result.toString(), opNode.getLine());
				replaceNodeInParent(parent, opNode, constantNode);
			} else if (operands.stream().anyMatch(ASTNode::isReal)) {
				Number result = evalReal(opNode.getOperator(), operands);
				LiteralNode constantNode = new LiteralNode(result.toString(), opNode.getLine());
				replaceNodeInParent(parent, opNode, constantNode);
			}
		} else if (operation.getClass().getSimpleName().equals("ComparisonNode")) {
			ComparisonNode compNode = (ComparisonNode) operation;
			boolean result = evalComp(compNode);
			BooleanNode constNode = new BooleanNode(result, compNode.getLine());
			replaceNodeInParent(parent, compNode, constNode);
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

	private boolean evalComp(ComparisonNode operation) {
		ASTNode leftOperand = operation.getLeftElement();
		ASTNode rightOperand = operation.getRightElement();

		if (leftOperand.isConstant() && rightOperand.isConstant()) {
			double left = Double.parseDouble(((LiteralNode) leftOperand).getValue());
			double right = Double.parseDouble(((LiteralNode) rightOperand).getValue());
			return switch (operation.getComparison()) {
				case "equal" -> left == right;
				case "nonequal" -> left != right;
				case "less" -> left < right;
				case "lesseq" -> left <= right;
				case "greater" -> left > right;
				case "greatereq" -> left >= right;
				default -> false;
			};
		}
		return false;
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

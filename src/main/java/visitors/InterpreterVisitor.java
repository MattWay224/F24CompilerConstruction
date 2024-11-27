package visitors;

import ast.nodes.*;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class InterpreterVisitor implements ASTVisitor<Object> {
	private final SymbolTable symbolTable;
	private final boolean globalScope; // Flag to track whether in global scope

	public InterpreterVisitor(SymbolTable symbolTable) {
		this(symbolTable, true); // Default to global scope
	}

	public InterpreterVisitor(SymbolTable symbolTable, boolean globalScope) {
		this.symbolTable = symbolTable;
		this.globalScope = globalScope;
	}

	@Override
	public Object visitAssignmentNode(AssignmentNode node) {
		Object value = visit(node.getChildren().getFirst());
		symbolTable.define(node.getVariable(), new LiteralNode(value.toString()));
		return value;
	}

	@Override
	public Object visitAtomNode(AtomNode node) {
		try {
			return visit(symbolTable.lookup(node.getValue()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Object visitLiteralNode(LiteralNode node) {
		String value = node.getValue();
		try {
			if (value.contains(".")) {
				return Double.parseDouble(value);
			} else {
				return Integer.parseInt(value);
			}
		} catch (NumberFormatException e) {
			return value;
		}
	}

	@Override
	public Object visitOperationNode(OperationNode node) {
		String operator = node.getOperator();
		List<Object> evaluatedOperands = new ArrayList<>();
		for (ASTNode operand : node.getOperands()) {
			evaluatedOperands.add(visit(operand));
		}

		return evalOperation(operator, evaluatedOperands);
	}

	@Override
	public Object visitPredicateNode(PredicateNode node) {
		switch (node.getPredicate()) {
			case "isint" -> {
				if (node.getElement().accept(this) instanceof Integer) {
					return true;
				} else return false;
			}
			case "isreal" -> {
				if (node.getElement().accept(this) instanceof Double ||
						node.getElement().accept(this) instanceof Integer) {
					return true;
				} else return false;
			}
			case "isbool" -> {
				if (node.getElement().accept(this) instanceof Boolean) {
					return true;
				} else return false;
			}
			case "isnull" -> {
				if (node.getElement() instanceof NullNode) {
					return true;
				} else return false;
			}
			case "islist" -> {
				if (node.getElement() instanceof ConsNode) {
					return true;
				} else return false;
			}
		}
		Object value = visit(node.getElement());
		return value != null;
	}

	private Number evalOperation(String operator, List<Object> operands) {
		List<Double> numericOperands = operands.stream()
				.map(o -> ((Number) o).doubleValue()) // Ensure all are Numbers
				.toList();

		switch (operator) {
			case "plus" -> {
				return numericOperands.stream().mapToDouble(Double::doubleValue).sum();
			}
			case "minus" -> {
				double result = numericOperands.get(0);
				for (int i = 1; i < numericOperands.size(); i++) {
					result -= numericOperands.get(i);
				}
				return result;
			}
			case "times" -> {
				double result = 1.0;
				for (Double operand : numericOperands) {
					result *= operand;
				}
				return result;
			}
			case "divide" -> {
				double result = numericOperands.get(0);
				for (int i = 1; i < numericOperands.size(); i++) {
					double divisor = numericOperands.get(i);
					if (divisor == 0) {
						throw new RuntimeException("Division by zero.");
					}
					result /= divisor;
				}
				return result;
			}
			default -> throw new RuntimeException("Unknown operator: " + operator);
		}
	}


	@Override
	public Object visitProgNode(ProgNode node) {
		Object result = null;
		for (ASTNode statement : node.getStatements()) {
			result = visit(statement);
			if (globalScope && result != null) { // Only print in global scope
				System.out.println(result);
			}
		}
		return result;
	}

	@Override
	public Object visitConditionNode(ConditionNode node) {
		return node.getChildren().get(1);
	}

	@Override
	public Object visitWhileNode(WhileNode node) {
		Object result = null;
		while ((Boolean) visit(node.getCondition())) {
			for (ASTNode stmt : node.getBody()) {
				result = visit(stmt);
			}
			if (result == "break") break;
		}
		return null;
	}

	@Override
	public Object visitFunctionNode(FunctionNode node) {
		symbolTable.define(node.getFunctionName(), node);
		return null;
	}

	@Override
	public Object visitFunctionCallNode(FunctionCallNode node) {
		try {
			FunctionNode function = (FunctionNode) symbolTable.lookup(node.getFunctionName());
			SymbolTable functionScope = new SymbolTable(symbolTable);

			// Bind arguments
			for (int i = 0; i < function.getParameters().size(); i++) {
				String param = function.getParameters().get(i);
				Object argValue = visit(node.getParameters().get(i));
				functionScope.define(param, new LiteralNode(argValue.toString()));
			}

			InterpreterVisitor functionInterpreter = new InterpreterVisitor(functionScope, false);
			return functionInterpreter.visit(function.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Function call error: " + e.getMessage());
		}
	}

	@Override
	public Object visitNullNode(NullNode node) {
		return null;
	}

	@Override
	public Object visitBoolNode(BooleanNode node) {
		return node.getValue();
	}

	@Override
	public Object visitNotNode(NotNode node) {
		Object value = visit(node.getElement());
		if (value instanceof Boolean) {
			return !(Boolean) value;
		}
		throw new RuntimeException("Invalid type for 'not'. Expected Boolean.");
	}

	@Override
	public Object visitComparisonNode(ComparisonNode node) {
		double left = ((Number) visit(node.getLeftElement())).doubleValue();
		double right = ((Number) visit(node.getRightElement())).doubleValue();

		return switch (node.getComparison()) {
			case "equal" -> left == right;
			case "nonequal" -> left != right;
			case "less" -> left < right;
			case "lesseq" -> left <= right;
			case "greater" -> left > right;
			case "greatereq" -> left >= right;
			default -> throw new RuntimeException("Unknown comparison operator.");
		};
	}


	@Override
	public Object visitLogicalOperationNode(LogicalOperationNode node) {
		Object left = visit(node.getChildren().get(0));
		Object right = visit(node.getChildren().get(1));
		String operator = node.getOperator();

		if (left instanceof Boolean && right instanceof Boolean) {
			boolean l = (Boolean) left;
			boolean r = (Boolean) right;
			return switch (operator) {
				case "and" -> l && r;
				case "or" -> l || r;
				case "xor" -> (l || r) && !(l && r);
				case "nor" -> !(l || r);
				case "nand" -> !(l && r);
				case "xnor" -> !((l || r) && !(l && r));
				default -> throw new RuntimeException("Unknown logical operator: " + operator);
			};
		}
		throw new RuntimeException("Unknown logical operator: " + operator);
	}

	@Override
	public Object visitConsNode(ConsNode node) {
		Object head = visit(node.getHead());
		List<Object> tail = (List<Object>) visit(node.getTail());
		List<Object> result = new ArrayList<>();
		result.add(head);
		result.addAll(tail);
		return result;
	}

	@Override
	public Object visitHeadNode(HeadNode node) {
		List<Object> list = (List<Object>) visit(node.getHead());
		if (list.isEmpty()) {
			throw new RuntimeException("Cannot get head of an empty list.");
		}
		return list.get(0);
	}

	@Override
	public Object visitTailNode(TailNode node) {
		List<Object> list = (List<Object>) visit(node.getTail());
		if (list.isEmpty()) {
			throw new RuntimeException("Cannot get tail of an empty list.");
		}
		return list.subList(1, list.size());
	}

	@Override
	public Object visitLambdaNode(LambdaNode node) {
		return node;
	}

	@Override
	public Object visitLambdaCallNode(LambdaCallNode node) {
		try {
			LambdaNode lambda = (LambdaNode) visit(symbolTable.lookup(node.getLambdaName()));
			SymbolTable lambdaScope = new SymbolTable(symbolTable);

			for (int i = 0; i < lambda.getParameters().size(); i++) {
				String param = lambda.getParameters().get(i);
				Object argValue = visit(node.getParameters().get(i));
				lambdaScope.define(param, new LiteralNode(argValue.toString()));
			}

			InterpreterVisitor lambdaInterpreter = new InterpreterVisitor(lambdaScope);
			return lambdaInterpreter.visit(lambda.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Lambda call exc: ");
		}
	}

	@Override
	public Object visitReturnNode(ReturnNode node) {
		return visit(node.getReturnValue());
	}

	@Override
	public Object visitBreakNode(BreakNode node) {
		return true;
	}

	@Override
	public Object visitEvalNode(EvalNode node) {
		Object code = visit(node.getNode());
		if (code instanceof ASTNode) {
			return visit((ASTNode) code);
		}
		throw new RuntimeException("Eval expects an ASTNode as input.");
	}

	@Override
	public Object visitQuoteNode(QuoteNode node) {
		return node.getQuotedExpr();
	}

	@Override
	public Object visitConditionBranch(ConditionBranch node) {
		if ((Boolean) visit(node.getCondition())) {
			return visit(node.getAction());
		}
		return null;
	}

	@Override
	public Object visitListNode(ListNode node) {
		List<Object> values = new ArrayList<>();
		for (ASTNode element : node.getElements()) {
			values.add(visit(element));
		}
		return values;
	}

	private Object visit(ASTNode node) {
		return node.accept(this);
	}
}

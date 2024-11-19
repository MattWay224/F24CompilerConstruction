package visitors;

import ast.nodes.*;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class InterpreterVisitor implements ASTVisitor<Object> {
	private final SymbolTable symbolTable;

	public InterpreterVisitor(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	@Override
	public Object visitAssignmentNode(AssignmentNode node) {
		ASTNode value = node.getChildren().getFirst();
		symbolTable.define(node.getVariable(), value);
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
		return node.getValue();
	}

	@Override
	public Object visitOperationNode(OperationNode node) {
		List<ASTNode> operands = node.getOperands();

		if (operands.stream().allMatch(ASTNode::isInt)) {
			return evalInt(node.getOperator(), operands);
		} else if (operands.stream().anyMatch(ASTNode::isReal)) {
			return evalReal(node.getOperator(), operands);
		}


		throw new RuntimeException("Unsupported operand types for operator " + node.getOperator());
	}

	private Number evalInt(String operator, List<ASTNode> operands) {
		switch (operator) {
			case "plus" -> {
				int count = 0;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					count += Integer.parseInt(opp.getValue());
				}
				return count;
			}
			case "minus" -> {
				int count = 0;
				boolean flag = true;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					if (flag) {
						count += Integer.parseInt(opp.getValue());
						flag = false;
					} else {
						count -= Integer.parseInt(opp.getValue());
					}
				}
				return count;
			}
			case "times" -> {
				int count = 1;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					count *= Integer.parseInt(opp.getValue());
				}
				return count;
			}
			case "divide" -> {
				double count = 1;
				boolean flag = true;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					if (flag) {
						count = Integer.parseInt(opp.getValue());
						flag = false;
					} else {
						if (Integer.parseInt(opp.getValue()) == 0) {
							throw new RuntimeException("Division by zero: " + operator);
						}
						count /= Integer.parseInt(opp.getValue());
					}
				}
				return count;
			}
			default -> throw new RuntimeException("Unknown operator: " + operator);
		}
	}

	private Number evalReal(String operator, List<ASTNode> operands) {
		switch (operator) {
			case "plus" -> {
				double count = 0;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					count += Integer.parseInt(opp.getValue());
				}
				return count;
			}
			case "minus" -> {
				double count = 0;
				boolean flag = true;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					if (flag) {
						count += Integer.parseInt(opp.getValue());
						flag = false;
					} else {
						count -= Integer.parseInt(opp.getValue());
					}
				}
				return count;
			}
			case "times" -> {
				double count = 1;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					count *= Integer.parseInt(opp.getValue());
				}
				return count;
			}
			case "divide" -> {
				double count = 1;
				boolean flag = true;
				for (ASTNode op : operands) {
					LiteralNode opp = (LiteralNode) op;
					if (flag) {
						count = Integer.parseInt(opp.getValue());
						flag = false;
					} else {
						if (Integer.parseInt(opp.getValue()) == 0) {
							throw new RuntimeException("Division by zero: " + operator);
						}
						count /= Integer.parseInt(opp.getValue());
					}
				}
				return count;
			}
			default -> throw new RuntimeException("Unknown operator: " + operator);
		}
	}

	@Override
	public Object visitProgNode(ProgNode node) {
		Object result = null;
		for (ASTNode statement : node.getStatements()) {
			result = visit(statement);
		}
		return result;
	}

	@Override
	public Object visitConditionNode(ConditionNode node) {
		return node.getChildren().get(1);
	}

	@Override
	public Object visitWhileNode(WhileNode node) {
		List<Object> smth = new ArrayList<>();
		while ((boolean) visit(node.getCondition())) {
			for (ASTNode child : node.getBody()) {
				smth.add(visit(child));
			}
		}
		return smth;
	}

	@Override
	public Object visitFunctionNode(FunctionNode node) {
		symbolTable.define(node.getFunctionName(), node);
		return null; // Store function definition in the symbol table
	}

	@Override
	public Object visitFunctionCallNode(FunctionCallNode node) {
		try {
			FunctionNode function = (FunctionNode) symbolTable.lookup(node.getFunctionName());
			SymbolTable functionScope = new SymbolTable(symbolTable); // New scope for function call

			// Bind arguments
			for (int i = 0; i < function.getParameters().size(); i++) {
				String param = function.getParameters().get(i);
				Object argValue = visit(node.getParameters().get(i));
				functionScope.define(param, new LiteralNode(argValue.toString()));
			}

			InterpreterVisitor functionInterpreter = new InterpreterVisitor(functionScope);
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
	public Object visitBoolNode(BooleanNode booleanNode) {
		return booleanNode.getValue();
	}

	@Override
	public Object visitNotNode(NotNode node) {
		Object value = visit(node.getElement());
		if (value instanceof Boolean) {
			return !(Boolean) value;
		}
		throw new RuntimeException("Invalid type for 'not' operation. Expected Boolean.");
	}

	@Override
	public Object visitComparisonNode(ComparisonNode node) {
		ASTNode leftOperand = node.getLeftElement();
		ASTNode rightOperand = node.getRightElement();

		if (leftOperand.isConstant() && rightOperand.isConstant()) {
			double left = Double.parseDouble(((LiteralNode) leftOperand).getValue());
			double right = Double.parseDouble(((LiteralNode) rightOperand).getValue());
			return switch (node.getComparison()) {
				case "equal" -> left == right;
				case "nonequal" -> left != right;
				case "less" -> left < right;
				case "lesseq" -> left <= right;
				case "greater" -> left > right;
				case "greatereq" -> left >= right;
				default -> throw new RuntimeException("Incompatible types for comparison.");
			};
		}
		return false;
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

		throw new RuntimeException("Invalid types for logical operation. Expected Booleans.");
	}

	@Override
	public Object visitConsNode(ConsNode node) {
		Object head = visit(node.getHead());
		Object tail = visit(node.getTail());

		if (tail instanceof List<?>) {
			List<Object> newList = new ArrayList<>((List<?>) tail);
			newList.add(0, head);
			return newList;
		}

		throw new RuntimeException("Invalid type for 'cons'. Tail must be a list.");
	}

	@Override
	public Object visitHeadNode(HeadNode node) {
		return visit(node.getHead());
	}

	@Override
	public Object visitTailNode(TailNode node) {
		Object list = visit(node.getTail());

		if (list instanceof List<?> castedList) {
			if (!castedList.isEmpty()) {
				return castedList.subList(1, castedList.size());
			}
			throw new RuntimeException("Cannot get tail of an empty list.");
		}

		throw new RuntimeException("Invalid type for 'tail'. Expected List.");
	}

	@Override
	public Object visitPredicateNode(PredicateNode node) {
		Object value = visit(node.getElement());
		return value != null;
	}

	@Override
	public Object visitLambdaNode(LambdaNode node) {
		return node; // Return the LambdaNode itself; it can be invoked later.
	}

	@Override
	public Object visitLambdaCallNode(LambdaCallNode node) {
		try {
			LambdaNode lambda = (LambdaNode) symbolTable.lookup(node.getLambdaName());
			SymbolTable lambdaScope = new SymbolTable(symbolTable); // Create new scope for lambda
			for (int i = 0; i < lambda.getParameters().size(); i++) {
				String param = lambda.getParameters().get(i);
				Object argValue = visit(node.getParameters().get(i));
				lambdaScope.define(param, new LiteralNode(argValue.toString()));
			}

			InterpreterVisitor lambdaInterpreter = new InterpreterVisitor(lambdaScope);
			return lambdaInterpreter.visit(lambda.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Function call error: " + e.getMessage());
		}
	}

	@Override
	public Object visitReturnNode(ReturnNode node) {
		return visit(node.getReturnValue()); // Evaluate and return the expression
	}

	@Override
	public Object visitBreakNode(BreakNode node) {
		throw new RuntimeException("Break encountered"); // Use custom exception for control flow
	}

	@Override
	public Object visitEvalNode(EvalNode node) {
		Object code = visit(node.getNode());
		if (code instanceof ASTNode) {
			return visit((ASTNode) code); // Evaluate dynamically
		}
		throw new RuntimeException("Eval expects an ASTNode as input.");
	}

	@Override
	public Object visitQuoteNode(QuoteNode node) {
		return node.getQuotedExpr(); // Return the raw quoted ASTNode
	}

	@Override
	public Object visitConditionBranch(ConditionBranch node) {
		if ((boolean) visit(node.getCondition())) {
			return visit(node.getAction());
		}
		return null; // No match
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

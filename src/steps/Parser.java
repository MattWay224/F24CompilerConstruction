package steps;

import ast.ASTNodeFactory;
import ast.nodes.ASTNode;
import ast.nodes.ConditionBranch;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final ASTNodeFactory factory;
	private final List<Token> tokens;
	private int currentTokenIndex;
	private boolean insideCond;


	public Parser(List<Token> tokens, ASTNodeFactory factory) {
		this.tokens = tokens;
		this.currentTokenIndex = 0;
		this.factory = factory;
	}

	private Token peek() {
		return tokens.get(currentTokenIndex);
	}

	private void eat(TokenType tokenType) throws Exception {
		if (peek().getType().equals(tokenType)) {
			currentTokenIndex++;
		} else {
			throw new Exception("Unexpected token: " + peek());
		}
	}

	public List<ASTNode> parse() throws Exception {
		List<ASTNode> statements = new ArrayList<>();
		while (!isAtEnd()) {
			ASTNode node = parseExpr();
			statements.add(node);
		}
		return statements;
	}

	private ASTNode parseExpr() throws Exception {
		Token currentToken = peek();

		return switch (currentToken.type) {
			case LPAREN -> parseParenthesizedExpr();
			case QUOTE -> {
				advance();
				yield parseExpr();
			}
			case INTEGER, REAL, BOOLEAN -> {
				advance();
				yield factory.createLiteralNode(currentToken.value);
			}
			case ATOM -> {
				advance();
				yield factory.createAtomNode(currentToken.value);
			}
			case LESS, LESSEQ, GREATER, GREATEREQ, EQUAL, NONEQUAL -> parseComparison(currentToken.value);
			case PLUS, MINUS, TIMES, DIVIDE -> parseOperation(currentToken.value);
			default -> throw new Exception("UNEXPECTED TOKEN: " + currentToken);
		};
	}

	private boolean isAtEnd() {
		return currentTokenIndex >= tokens.size() || peek().type == TokenType.EOF;
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	private Token previous() {
		return tokens.get(currentTokenIndex - 1);
	}

	private Token advance() {
		if (!isAtEnd()) {
			return tokens.get(currentTokenIndex++);
		}
		return null;
	}

	private Token consume(TokenType type, String errorMessage) throws Exception {
		if (check(type)) return advance();
		throw new Exception(errorMessage + ". FOUND: " + peek());
	}

	private ASTNode parseParenthesizedExpr() throws Exception {
		consume(TokenType.LPAREN, "EXPECTED (");

		Token operatorToken = peek();

		if (operatorToken.type == TokenType.INTEGER ||
				operatorToken.type == TokenType.REAL ||
				operatorToken.type == TokenType.BOOLEAN ||
				operatorToken.type == TokenType.ATOM) {
			// if literal or atom assume a list of literals
			return parseLiteralList();
		}

		return switch (operatorToken.value) {
			case "setq" -> parseSETQ();
			case "func" -> parseFUNC();
			case "cond" -> parseCOND();
			case "prog" -> parsePROG();
			case "plus", "minus", "times", "divide" -> parseOperation(operatorToken.value);
			case "head" -> parseHeadOrTail("head");
			case "tail" -> parseHeadOrTail("tail");
			case "cons" -> parseCons();
			case "while" -> parseWHILE();
			case "return" -> parseRETURN();
			case "break" -> parseBREAK();
			case "isint", "isreal", "isbool", "isnull", "isatom", "islist" -> parsePredicate(operatorToken.value);
			case "equal", "nonequal", "less", "lesseq", "greater", "greatereq" -> parseComparison(operatorToken.value);
			case "and", "or", "xor" -> parseLogicalOperator(operatorToken.value);
			case "not" -> parseNot();
			case "lambda" -> parseLambda();
			default -> parseFuncCall(operatorToken.value);
		};
	}

	private ASTNode parseBREAK() throws Exception {
		advance();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER BREAK");
		return factory.createBreakNode();
	}

	private ASTNode parseRETURN() throws Exception {
		advance();
		ASTNode returnValue = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER RETURN");
		return factory.createReturnNode(returnValue);
	}

	private ASTNode parseWHILE() throws Exception {
		advance();
		consume(TokenType.LPAREN, "EXPECTED ( AFTER WHILE");
		ASTNode condition = parseExpr();
		List<ASTNode> body = new ArrayList<>();
		while (!check(TokenType.RPAREN)) {
			body.add(parseExpr());
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER WHILE BODY");
		return factory.createWhileNode(condition, body);
	}

	private ASTNode parsePROG() throws Exception {
		advance();
		List<ASTNode> statements = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			statements.add(parseExpr());
		}

		List<ASTNode> finalExpression = new ArrayList<>();
		while (!check(TokenType.RPAREN)) {
			finalExpression.add(parseExpr());
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER FINAL EXPRESSION");

		return factory.createProgNode(statements, finalExpression);
	}

	private ASTNode parseLiteralList() throws Exception {
		List<ASTNode> elements = new ArrayList<>();
		while (!check(TokenType.RPAREN)) {
			elements.add(parseExpr()); // add each literal to list
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER LITERAL LIST");
		return factory.createListNode(elements);
	}

	private ASTNode parseLambda() throws Exception {
		advance();
		List<String> parameters = parseParameterList();
		ASTNode body = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER LAMBDA BODY");
		return factory.createLambdaNode(parameters, body);
	}

	private ASTNode parseLogicalOperator(String operator) throws Exception {
		advance();
		ASTNode leftElement = parseExpr();
		ASTNode rightElement = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + operator);
		return factory.createLogicalOperationNode(operator, leftElement, rightElement);
	}

	private ASTNode parseNot() throws Exception {
		advance();
		ASTNode element = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER NOT");
		return factory.createNotNode(element);
	}

	private ASTNode parseComparison(String comparison) throws Exception {
		advance();
		ASTNode leftElement = parseExpr();
		ASTNode rightElement = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + comparison);
		return factory.createComparisonNode(comparison, leftElement, rightElement);
	}

	//issmth
	private ASTNode parsePredicate(String predicate) throws Exception {
		advance();
		ASTNode element = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + predicate);
		return factory.createPredicateNode(predicate, element);
	}

	private ASTNode parseHeadOrTail(String type) throws Exception {
		advance();
		ASTNode listExpr = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + type);
		if (type.equals("head")) {
			return factory.createHeadNode(listExpr);
		} else {
			return factory.createTailNode(listExpr);
		}
	}

	private ASTNode parseCons() throws Exception {
		advance();
		ASTNode item = parseExpr();//what to add
		ASTNode list = parseExpr();//to list
		consume(TokenType.RPAREN, "EXPECTED ) AFTER CONS");
		return factory.createConsNode(item, list);
	}

	private ASTNode parseOperation(String operator) throws Exception {
		advance();
		List<ASTNode> operands = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			operands.add(parseExpr());
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");

		if (operands.size() < 2 && !(insideCond && (operator.equals("plus") || operator.equals("minus")))) {
			throw new Exception("IMPOSSIBLE OPERATION");
		}
		return factory.createOperationNode(operator, operands, false);
	}

	private ASTNode parseFuncCall(String functionName) throws Exception {
		advance();
		List<ASTNode> operands = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			operands.add(parseExpr());
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");
		return factory.createFunctionCallNode(functionName, operands);
	}

	private ASTNode parseSETQ() throws Exception {
		advance();
		String variable = consume(TokenType.ATOM, "EXPECTED VARIABLE FOR SETQ").value;
		ASTNode value = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER SETQ ASSIGNMENT");
		return factory.createAssignmentNode(variable, value);
	}

	private ASTNode parseFUNC() throws Exception {
		advance();
		String functionName = consume(TokenType.ATOM, "MISSING FUNCTION NAME").value;
		List<String> parameters = parseParameterList();
		ASTNode body = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER FUNCTION BODY");
		return factory.createFunctionNode(functionName, parameters, body);
	}

	private ASTNode parseCOND() throws Exception {
		advance();
		List<ConditionBranch> branches = new ArrayList<>();

		if (isAtEnd()) throw new Exception("Unexpected end of input while parsing condition-action pairs.");
		ASTNode condition = parseExpr();

		if (isAtEnd()) throw new Exception("Expected action after condition, but reached end of input.");
		ASTNode action = parseExpr();
		branches.add(factory.createConditionBranch(condition, action));

		//default case
		ASTNode defaultAction = null;
		if (check(TokenType.LPAREN)) {
			defaultAction = parseExpr();
		}
		consume(TokenType.RPAREN, "MISSING ) AFTER COND EXPR");
		return factory.createConditionNode(branches, defaultAction);
	}


	private List<String> parseParameterList() throws Exception {
		List<String> parameters = new ArrayList<>();
		consume(TokenType.LPAREN, "EXPECTED ( BEFORE PARAMETER LIST");
		while (!check(TokenType.RPAREN)) {
			parameters.add(consume(TokenType.ATOM, "EXPECTED PARAMETER").value);
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER PARAMETER LIST");
		return parameters;
	}
}

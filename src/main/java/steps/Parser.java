package steps;

import ast.ASTNodeFactory;
import ast.nodes.*;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Parser {
	private final ASTNodeFactory factory;
	private List<Token> tokens;
	private int currentTokenIndex;
	private final SymbolTable globalScope = new SymbolTable(null);
	private SymbolTable currentScope = globalScope;

	public Parser() {
		factory = new ASTNodeFactory();
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
		this.currentTokenIndex = 0;
	}

	private Token peek() {
		return tokens.get(currentTokenIndex);
	}

	public ASTNode parse() throws Exception {
		List<ASTNode> statements = new ArrayList<>();
		while (!isAtEnd()) {
			ASTNode node = parseExpr();
			statements.add(node);
		}
		ASTNode prognode = factory.createProgNode(statements, 0, 0);
		prognode.setType(ASTNode.NodeType.PROG);

		return prognode;
	}

	public ASTNode parseExpr() throws Exception {
		Token currentToken = peek();

		return switch (currentToken.type) {
			case LPAREN -> parseParenthesizedExpr();
			case QUOTE -> parseQuoteWithoutBrackets();
			case INTEGER -> {
				advance();
				ASTNode intnode = factory.createLiteralNode(currentToken.value);
				intnode.setType(ASTNode.NodeType.ATOM);
				yield intnode;
			}
			case REAL -> {
				advance();
				ASTNode realnode = factory.createLiteralNode(currentToken.value);
				realnode.setType(ASTNode.NodeType.ATOM);
				yield realnode;
			}
			case NULL -> {
				advance();
				ASTNode nullnode = factory.createNullNode(currentToken.value, currentToken.line);
				nullnode.setType(ASTNode.NodeType.NULL);
				yield nullnode;
			}
			case BOOLEAN -> {
				advance();
				ASTNode boolnode = factory.createBooleanNode(Boolean.parseBoolean(currentToken.value), currentToken.line);
				boolnode.setType(ASTNode.NodeType.BOOL);
				yield boolnode;
			}
			case ATOM -> {
				advance();

				if (currentScope.isDefined(currentToken.value)) {
					ASTNode atomnode = factory.createAtomNode(currentToken.value, currentToken.line);
					atomnode.setType(ASTNode.NodeType.ATOM);
					yield atomnode;
				} else {
					throw new Exception("Undefined variable " + currentToken.value + " in line " + currentToken.line);
				}
			}
			case LESS, LESSEQ, GREATER, GREATEREQ, EQUAL, NONEQUAL -> parseComparison(currentToken.value);
			case PLUS, MINUS, TIMES, DIVIDE -> parseOperation(currentToken.value);
			default -> throw new Exception("UNEXPECTED TOKEN: " + currentToken + " in line " + currentToken.line);
		};
	}

	private ASTNode parseQuoteWithoutBrackets() throws Exception {
		Token quoteToken = advance();
		ASTNode quotedExpr = parseExpr();
		ASTNode quotednode = factory.createQuoteNode(quotedExpr, quoteToken.line);
		quotednode.addChild(quotedExpr);
		quotednode.setType(ASTNode.NodeType.QUOTE);
		return quotednode;
	}
	private ASTNode parseQuote() throws Exception {
		Token quoteToken = advance();
		ASTNode quotedExpr = parseExpr();
		ASTNode quotednode = factory.createQuoteNode(quotedExpr, quoteToken.line);
		quotednode.addChild(quotedExpr);
		quotednode.setType(ASTNode.NodeType.QUOTE);
		consume(TokenType.RPAREN, "EXPECTED ) AFTER QUOTE");
		return quotednode;
	}

	private boolean isAtEnd() {
		return currentTokenIndex >= tokens.size() || peek().type == TokenType.EOF;
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	public Token advance() {
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
				operatorToken.type == TokenType.BOOLEAN) {
			// if literal or atom assume a list of literals
			return parseLiteralList();
		} else if (operatorToken.type == TokenType.ATOM) {
			String operatorValue = operatorToken.value;
			if (globalScope.isDefined(operatorValue) && globalScope.lookup(operatorValue) instanceof FunctionNode) {
				return parseFuncCall(operatorValue);
			} else if (globalScope.isDefined(operatorValue) && globalScope.lookup(operatorValue) instanceof LambdaNode) {
				return parseLambdaCall(operatorValue);
			} else {
				return parseLiteralList();
			}
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
			case ")" -> parseLiteralList();
			case "quote" -> parseQuote();
			case "eval" -> parseEval();
			default -> parseFuncCall(operatorToken.value);
		};
	}

	private ASTNode parseEval() throws Exception {
		Token op = advance();
		ASTNode q = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER EVAL");
		ASTNode evalnode = factory.createEvalNode(q, op.line);
		evalnode.addChild(q);
		evalnode.setType(ASTNode.NodeType.EVAL);
		return evalnode;
	}

	private ASTNode parseBREAK() throws Exception {
		Token op = advance();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER BREAK");
		ASTNode breaknode = factory.createBreakNode(op.line);
		breaknode.setType(ASTNode.NodeType.BREAK);
		return breaknode;
	}

	private ASTNode parseRETURN() throws Exception {
		Token op = advance();
		ASTNode returnValue = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER RETURN");
		ASTNode returnnode = factory.createReturnNode(returnValue, op.line);
		returnnode.addChild(returnValue);
		returnnode.setType(ASTNode.NodeType.RETURN);
		return returnnode;
	}

	private ASTNode parseWHILE() throws Exception {
		Token op = advance();
		consume(TokenType.LPAREN, "EXPECTED ( AFTER WHILE");
		ASTNode condition = parseExpr();
		List<ASTNode> body = new ArrayList<>();
		while (!check(TokenType.RPAREN)) {
			body.add(parseExpr());
		}
		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER WHILE BODY");
		ASTNode whilenode = factory.createWhileNode(condition, body, op.line, clo.line);
		whilenode.addChild(condition);
		for (ASTNode statement : body) {
			whilenode.addChild(statement);
		}
		return whilenode;
	}

	private ASTNode parsePROG() throws Exception {
		Token op = advance();

		//enter new scope
		SymbolTable previousScope = currentScope;
		currentScope = new SymbolTable(previousScope);

		consume(TokenType.LPAREN, "EXPECTED ( AFTER prog");
		while (!check(TokenType.RPAREN)) {
			String varName = consume(TokenType.ATOM, "EXPECTED ATOM FOR LOCAL VARIABLE").value;
			currentScope.define(varName, null);
		}

		consume(TokenType.RPAREN, "EXPECTED ) AFTER LOCAL VARIABLE LIST");

		List<ASTNode> statements = new ArrayList<>();
		while (!check(TokenType.RPAREN)) {
			statements.add(parseExpr());
		}

		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER PROG BLOCK");
		//exit scope
		currentScope = previousScope;

		ASTNode prognode = factory.createProgNode(statements, op.line, clo.line);
		prognode.setType(ASTNode.NodeType.PROG);
		return prognode;
	}

	private ASTNode parseFUNC() throws Exception {
		Token op = advance();

		SymbolTable previousScope = currentScope;
		currentScope = new SymbolTable(previousScope);

		String functionName = consume(TokenType.ATOM, "MISSING FUNCTION NAME").value;

		FunctionNode placeholderFunctionNode = new FunctionNode(functionName, new ArrayList<>(), null, op.line, op.line);
		placeholderFunctionNode.setType(ASTNode.NodeType.FUNC);

		globalScope.define(functionName, placeholderFunctionNode);
		currentScope.define(functionName, placeholderFunctionNode);
		List<String> parameters = new ArrayList<>();
		consume(TokenType.LPAREN, "EXPECTED ( AFTER FUNCTION NAME");
		while (!check(TokenType.RPAREN)) {
			String varName = consume(TokenType.ATOM, "EXPECTED ATOM FOR LOCAL PARAMETER").value;
			parameters.add(varName);
			currentScope.define(varName, null);
		}

		placeholderFunctionNode = new FunctionNode(functionName, parameters, null, op.line, op.line);
		globalScope.define(functionName, placeholderFunctionNode);
		currentScope.define(functionName, placeholderFunctionNode);

		consume(TokenType.RPAREN, "EXPECTED ) AFTER FUNCTION PARAMETER LIST");
		List<ASTNode> bodyExpressions = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			ASTNode expr = parseExpr();
			bodyExpressions.add(expr);
		}
		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER FUNCTION BODY");

		ASTNode body = factory.createProgNode(bodyExpressions, op.getLine(), clo.getLine());
		body.setType(ASTNode.NodeType.PROG);
		currentScope = previousScope;

		ASTNode functionNode = factory.createFunctionNode(functionName, parameters, body, op.line, clo.line);
		for (ASTNode expr : bodyExpressions) {
			functionNode.addChild(expr);
		}
		functionNode.setType(ASTNode.NodeType.FUNC);
		globalScope.define(functionName, functionNode);
		currentScope.define(functionName, functionNode);
		return functionNode;
	}

	private ASTNode parseLambda() throws Exception {
		Token op = advance();

		SymbolTable previousScope = currentScope;
		currentScope = new SymbolTable(previousScope);

		List<String> parameters = new ArrayList<>();
		consume(TokenType.LPAREN, "EXPECTED ( AFTER LAMBDA");
		while (!check(TokenType.RPAREN)) {
			String varName = consume(TokenType.ATOM, "EXPECTED ATOM FOR LOCAL PARAMETER").value;
			parameters.add(varName);
			currentScope.define(varName, null);
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER LAMBDA PARAMETER LIST");

		ASTNode body = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER LAMBDA BODY");
		currentScope = previousScope;

		ASTNode lambdanode = factory.createLambdaNode(parameters, body, op.line);
		lambdanode.addChild(body);
		lambdanode.setType(ASTNode.NodeType.LAMBDA);
		return lambdanode;
	}

	private ASTNode parseLiteralList() throws Exception {
		List<ASTNode> elements = new ArrayList<>();

		if (check(TokenType.RPAREN)) {
			Token clo = advance();
			ASTNode listNode = factory.createListNode(elements, clo.line);
			listNode.setType(ASTNode.NodeType.LIST);
			return listNode;
		}

		while (!check(TokenType.RPAREN)) {
			elements.add(parseExpr()); // add each literal to list
		}
		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER LITERAL LIST");
		ASTNode listnode = factory.createListNode(elements, clo.line);
		for (ASTNode el : elements) {
			listnode.addChild(el);
		}
		listnode.setType(ASTNode.NodeType.LIST);
		return listnode;
	}

	private ASTNode parseLogicalOperator(String operator) throws Exception {
		Token op = advance();
		ASTNode leftElement = parseExpr();
		ASTNode rightElement = parseExpr();
		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER " + operator);

		ASTNode logicalopnode = factory.createLogicalOperationNode(operator, leftElement, rightElement, op.line, clo.line);
		logicalopnode.addChild(leftElement);
		logicalopnode.addChild(rightElement);
		logicalopnode.setType(ASTNode.NodeType.LOGICALOP);
		return logicalopnode;
	}

	private ASTNode parseNot() throws Exception {
		Token op = advance();
		ASTNode element = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER NOT");
		ASTNode notnode = factory.createNotNode(element, op.line);
		notnode.addChild(element);
		notnode.setType(ASTNode.NodeType.NOT);
		return notnode;
	}

	private ASTNode parseComparison(String comparison) throws Exception {
		Token op = advance();
		ASTNode leftElement = parseExpr();
		ASTNode rightElement = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + comparison);

		ASTNode compnode = factory.createComparisonNode(comparison, leftElement, rightElement, op.line);
		compnode.addChild(leftElement);
		compnode.addChild(rightElement);
		compnode.setType(ASTNode.NodeType.BOOL);
		return compnode;
	}

	//issmth
	private ASTNode parsePredicate(String predicate) throws Exception {
		Token op = advance();
		ASTNode element = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + predicate);
		ASTNode predicatenode = factory.createPredicateNode(predicate, element, op.line);
		predicatenode.addChild(element);
		predicatenode.setType(ASTNode.NodeType.PREDICATE);
		return predicatenode;
	}

	private ASTNode parseHeadOrTail(String type) throws Exception {
		Token op = advance();
		ASTNode listExpr = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER " + type);
		if (type.equals("head")) {
			ASTNode headnode = factory.createHeadNode(listExpr, op.line);
			headnode.addChild(listExpr);
			headnode.setType(ASTNode.NodeType.HEAD);
			return headnode;
		} else {
			ASTNode tailnode = factory.createTailNode(listExpr, op.line);
			tailnode.addChild(listExpr);
			tailnode.setType(ASTNode.NodeType.TAIL);
			return tailnode;
		}
	}

	private ASTNode parseCons() throws Exception {
		Token op = advance();
		ASTNode item = parseExpr();//what to add
		ASTNode list = parseExpr();//to list
		consume(TokenType.RPAREN, "EXPECTED ) AFTER CONS");
		ASTNode consnode = factory.createConsNode(item, list, op.line);
		consnode.addChild(item);
		consnode.addChild(list);
		consnode.setType(ASTNode.NodeType.CONS);
		return consnode;
	}

	private ASTNode parseOperation(String operator) throws Exception {
		Token op = advance();
		List<ASTNode> operands = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			ASTNode expr = parseExpr();
			operands.add(expr);
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");

		if (operands.size() < 2 && !(operator.equals("plus") || operator.equals("minus"))) {
			throw new Exception("IMPOSSIBLE OPERATION");
		}
		ASTNode opnode = factory.createOperationNode(operator, operands, false, op.line);
		for (ASTNode operand : operands) {
			opnode.addChild(operand);
		}
		opnode.setType(operands.getFirst().getType());
		return opnode;
	}

	private ASTNode parseFuncCall(String functionName) throws Exception {
		if (!globalScope.isDefined(functionName)) {
			throw new Exception("Undefined function " + functionName + " in line " + peek().line);
		}

		ASTNode functionNode = globalScope.lookup(functionName);
		if (!(functionNode instanceof FunctionNode)) {
			throw new Exception(functionName + " is not a function");
		}

		List<ASTNode> operands = new ArrayList<>();

		advance();

		while (!check(TokenType.RPAREN)) {
			ASTNode expr = parseExpr();
			operands.add(expr);
		}

		if (operands.size() != ((FunctionNode) functionNode).getParameters().size()) {
			throw new Exception("INCORRECT NUMBER OF PARAMETERS FOR FUNCTION " + functionName + "EXPECTED-GOT: " + ((FunctionNode) functionNode).getParameters().size() + "-" + operands.size());
		}

		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");
		return factory.createFunctionCallNode(functionName, operands, clo.line);
	}

	private ASTNode parseLambdaCall(String lambdaName) throws Exception {
		if (!globalScope.isDefined(lambdaName)) {
			throw new Exception("Undefined lambda " + lambdaName);
		}

		ASTNode lambdaNode = globalScope.lookup(lambdaName);
		if (!(lambdaNode instanceof LambdaNode)) {
			throw new Exception(lambdaName + " is not a lambda");
		}

		List<ASTNode> operands = new ArrayList<>();

		while (!check(TokenType.RPAREN)) {
			operands.add(parseExpr());
		}

		Token clo = consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");
		ASTNode lambdacallnode = factory.createLambdaCallNode(lambdaName, operands, clo.line);
		lambdacallnode.setType(ASTNode.NodeType.LAMBDACALL);
		return lambdacallnode;
	}

	private ASTNode parseSETQ() throws Exception {
		Token op = advance();

		String variable = consume(TokenType.ATOM, "EXPECTED VARIABLE FOR SETQ").value;

		currentScope.define(variable, null);
		ASTNode value = parseExpr();

		//add var to scope in symbol table
		currentScope.define(variable, value);

		consume(TokenType.RPAREN, "EXPECTED ) AFTER SETQ ASSIGNMENT");
		ASTNode assignmentnode = factory.createAssignmentNode(variable, value, op.line);
		assignmentnode.addChild(value);
		assignmentnode.setType(ASTNode.NodeType.ASSIGNMENT);
		return assignmentnode;
	}

	private ASTNode parseCOND() throws Exception {
		Token op = advance();
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
		Token clo = consume(TokenType.RPAREN, "MISSING ) AFTER COND EXPR");
		ASTNode condnode = factory.createConditionNode(branches, defaultAction, op.line, clo.line);
		condnode.addChild(condition);
		condnode.addChild(action);
		condnode.setType(ASTNode.NodeType.COND);
		return condnode;
	}
}

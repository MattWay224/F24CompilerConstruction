package steps;

import ast.ASTNodeFactory;
import ast.nodes.ASTNode;
import visitors.PrettyVisitor;

import java.util.ArrayList;
import java.util.List;

public class FSyntaxAnalysis {
	private static FSyntaxAnalysis instance = new FSyntaxAnalysis();
	private List<Token> tokens;
	private int currentPos = 0;
	private boolean insideCond = false;
	private PrettyVisitor visitor;
	private ASTNodeFactory factory;

	private FSyntaxAnalysis() {}

	public static FSyntaxAnalysis getInstance() {
		if (instance == null) {
			instance = new FSyntaxAnalysis();
		}
		return instance;
	}

	public void setter(List<Token> tokens, PrettyVisitor visitor, ASTNodeFactory factory) {
		this.tokens = tokens;
		this.visitor = visitor;
		this.factory = factory;
	}

	public List<ASTNode> parse() throws Exception {
		List<ASTNode> statements = new ArrayList<>();
		while (!isAtEnd()) {
			ASTNode node = parseExpr();
			System.out.println(node.accept(visitor)); // Use the PrettyVisitor here to print
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

	private Token peek() {
		return tokens.get(currentPos);
	}

	private boolean isAtEnd() {
		return currentPos >= tokens.size() || peek().type == TokenType.EOF;
	}

	private boolean check(TokenType type) {
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	private Token previous() {
		return tokens.get(currentPos - 1);
	}

	private Token advance() {
		if (!isAtEnd()) currentPos++;
		return previous();
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
			case "cond" -> throw new Exception("COND not yet implemented"); //parseCOND();
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
			default -> throw new Exception("UNEXPECTED OPERATOR: " + operatorToken.value);
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
		ASTNode body = parseExpr();
		consume(TokenType.RPAREN, "EXPECTED ) AFTER WHILE BODY");
		return factory.createWhileNode(condition, body);
	}

	private ASTNode parsePROG() throws Exception {
		advance();
		consume(TokenType.LPAREN, "EXPECTED ( AFTER PROG");
		List<ASTNode> statements = new ArrayList<>();
		Token token = peek();
		while (token.type != TokenType.RPAREN && token.type != TokenType.EOF) {
			statements.add(parseExpr());
			token = peek();
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER PROG STATEMENTS");
		ASTNode finalExpression = parseExpr();
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
		List<ASTNode> operands = new ArrayList<>();
		advance();
		while (!check(TokenType.RPAREN)) {
			operands.add(parseExpr());
		}
		consume(TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");

		if ((operands.size() < 2) && !(insideCond && (operator.equals("plus") || operator.equals("minus")))) {
			System.out.println(insideCond);
			throw new Exception("IMPOSSIBLE OPERATION");
		}
		return factory.createOperationNode(operator, operands, false);
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
/*
    private ASTNode parseCOND() throws Exception {
        advance();
        List<ConditionBranch> branches = new ArrayList<>();

        // loop thr condition-action pairs
        while (!check(Flexer.TokenType.RPAREN)) {
            consume(Flexer.TokenType.LPAREN, "MISSING ( BEFORE COND");
            ASTNode condition = parseExpr();
            ASTNode action = parseExpr();

            System.out.println("action" + action);
            // if action is plus or minus it is not op but sign of func parameter ne rabotaet
            if (action instanceof AtomNode) {
                String actionValue = ((AtomNode) action).value;
                if (actionValue.equals("plus") || actionValue.equals("minus")) {
                    //action = new SignNode(actionValue);
                    action = new OperationNode(actionValue, null, true);
                    insideCond = true;
                    System.out.println("aboba");
                }
            }

            branches.add(new ConditionBranch(condition, action));
            consume(Flexer.TokenType.RPAREN, "MISSING ) AFTER ACTION");
        }

        //default case
        ASTNode defaultAction = null;
        if (peek().type == Flexer.TokenType.LPAREN) {
            advance();
            defaultAction = parseExpr();
            if (defaultAction instanceof AtomNode) {
                String actionValue = ((AtomNode) defaultAction).value;
                if (actionValue.equals("plus") || actionValue.equals("minus")) {
                    //defaultAction = new SignNode(actionValue);
                    defaultAction = new OperationNode(actionValue, null, true);
                    System.out.println("aboba");
                    insideCond = true;
                }
            }
            consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER DEFAULT ACTION");
        }

        consume(Flexer.TokenType.RPAREN, "MISSING ) AFTER COND EXPR");
        return new ConditionNode(branches, defaultAction);
    }
*/

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

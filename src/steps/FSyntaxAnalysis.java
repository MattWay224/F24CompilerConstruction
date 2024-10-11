package steps;

import ast.ASTNodeFactory;
import ast.nodes.ASTNode;
import steps.Flexer;
import visitors.PrettyVisitor;

import java.util.ArrayList;
import java.util.List;

public class FSyntaxAnalysis {
    private final List<Flexer.Token> tokens;
    private int currentPos = 0;
    private boolean insideCond = false;
    private final PrettyVisitor visitor;
    private final ASTNodeFactory factory;

    public FSyntaxAnalysis(List<Flexer.Token> tokens, PrettyVisitor visitor, ASTNodeFactory factory) {
        this.tokens = tokens;
        this.visitor = visitor;
        this.factory = factory;
    }

    public List<ASTNode> parse() throws Exception {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            ASTNode node = parseExpr();
            System.out.println(node.accept(visitor)); // Use the PrettyVisitor here to print
            statements.add(parseExpr());
        }
        return statements;
    }

    private ASTNode parseExpr() throws Exception {
        Flexer.Token currentToken = peek();

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

    private Flexer.Token peek() {
        return tokens.get(currentPos);
    }

    private boolean isAtEnd() {
        return peek().type == Flexer.TokenType.EOF;
    }

    private boolean check(Flexer.TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Flexer.Token previous() {
        return tokens.get(currentPos - 1);
    }

    private Flexer.Token advance() {
        if (!isAtEnd()) currentPos++;
        return previous();
    }

    private Flexer.Token consume(Flexer.TokenType type, String errorMessage) throws Exception {
        if (check(type)) return advance();
        throw new Exception(errorMessage + ". FOUND: " + peek());
    }

    private ASTNode parseParenthesizedExpr() throws Exception {
        consume(Flexer.TokenType.LPAREN, "EXPECTED (");

        Flexer.Token operatorToken = peek();

        if (operatorToken.type == Flexer.TokenType.INTEGER ||
                operatorToken.type == Flexer.TokenType.REAL ||
                operatorToken.type == Flexer.TokenType.BOOLEAN ||
                operatorToken.type == Flexer.TokenType.ATOM) {
            // if literal or atom assume a list of literals
            return parseLiteralList();
        }

		return switch (operatorToken.value) {
			case "setq" -> parseSETQ();
			case "func" -> parseFUNC();
			case "cond" -> null; //parseCOND();
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
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER BREAK");
        return factory.createBreakNode();
    }

    private ASTNode parseRETURN() throws Exception {
        advance();
        ASTNode returnValue = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER RETURN");
        advance();
        return factory.createReturnNode(returnValue);
    }

    private ASTNode parseWHILE() throws Exception {
        advance();
        consume(Flexer.TokenType.LPAREN, "EXPECTED ( AFTER WHILE");
        ASTNode condition = parseExpr();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER WHILE BODY");
        return factory.createWhileNode(condition, body);
    }

    private ASTNode parsePROG() throws Exception {
        advance();
        consume(Flexer.TokenType.LPAREN, "EXPECTED ( AFTER PROG");
        List<ASTNode> statements = new ArrayList<>();
        Flexer.Token token = peek();
        while (token.type != Flexer.TokenType.RPAREN && token.type != Flexer.TokenType.EOF) {
            statements.add(parseExpr());
            token = peek();
        }
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER PROG STATEMENTS");
        ASTNode finalExpression = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER FINAL EXPRESSION");
        return factory.createProgNode(statements, finalExpression);
    }

    private ASTNode parseLiteralList() throws Exception {
        List<ASTNode> elements = new ArrayList<>();
        while (!check(Flexer.TokenType.RPAREN)) {
            elements.add(parseExpr()); // add each literal to list
        }
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER LITERAL LIST");
        return factory.createListNode(elements);
    }

    private ASTNode parseLambda() throws Exception {
        advance();
        List<String> parameters = parseParameterList();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER LAMBDA BODY");
        return factory.createLambdaNode(parameters, body);
    }

    private ASTNode parseLogicalOperator(String operator) throws Exception {
        advance();
        ASTNode leftElement = parseExpr();
        ASTNode rightElement = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + operator);
        return factory.createLogicalOperationNode(operator, leftElement, rightElement);
    }

    private ASTNode parseNot() throws Exception {
        advance();
        ASTNode element = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER NOT");
        return factory.createNotNode(element);
    }

    private ASTNode parseComparison(String comparison) throws Exception {
        advance();
        ASTNode leftElement = parseExpr();
        ASTNode rightElement = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + comparison);
        return factory.createComparisonNode(comparison, leftElement, rightElement);
    }

    //issmth
    private ASTNode parsePredicate(String predicate) throws Exception {
        advance();
        ASTNode element = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + predicate);

        return factory.createPredicateNode(predicate, element);
    }

    private ASTNode parseHeadOrTail(String type) throws Exception {
        advance();
        ASTNode listExpr = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + type);
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
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER CONS");
        return factory.createConsNode(item, list);
    }

    private ASTNode parseOperation(String operator) throws Exception {
        List<ASTNode> operands = new ArrayList<>();
        advance();
        while (!check(Flexer.TokenType.RPAREN)) {
            operands.add(parseExpr());
        }
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER OPERATION");

        if ((operands.size() < 2) && !(insideCond && (operator.equals("plus") || operator.equals("minus")))) {
            System.out.println(insideCond);
            throw new Exception("IMPOSSIBLE OPERATION");
        }
        return factory.createOperationNode(operator, operands, false);
    }

    private ASTNode parseSETQ() throws Exception {
        advance();
        String variable = consume(Flexer.TokenType.ATOM, "EXPECTED VARIABLE FOR SETQ").value;
        ASTNode value = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER SETQ ASSIGNMENT");
        return factory.createAssignmentNode(variable, value);
    }

    private ASTNode parseFUNC() throws Exception {
        advance();
        String functionName = consume(Flexer.TokenType.ATOM, "MISSING FUNCTION NAME").value;
        List<String> parameters = parseParameterList();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER FUNCTION BODY");
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
        consume(Flexer.TokenType.LPAREN, "EXPECTED ( BEFORE PARAMETER LIST");
        while (!check(Flexer.TokenType.RPAREN)) {
            parameters.add(consume(Flexer.TokenType.ATOM, "EXPECTED PARAMETER").value);
        }
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER PARAMETER LIST");
        return parameters;
    }
}

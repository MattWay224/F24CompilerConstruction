import java.util.ArrayList;
import java.util.List;

public class FSyntaxAnalysis {
    private List<Flexer.Token> tokens;
    private int currentPos = 0;
    private boolean insideCond = false;

    public FSyntaxAnalysis(List<Flexer.Token> tokens) {
        this.tokens = tokens;
    }

    public List<ASTNode> parse() throws Exception {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseExpr());
        }
        return statements;
    }

    private ASTNode parseExpr() throws Exception {
        Flexer.Token currentToken = peek();

        switch (currentToken.type) {
            case LPAREN:
                return parseParenthesizedExpr();
            case QUOTE:
                advance();
                return parseExpr();
            case INTEGER:
            case REAL:
            case BOOLEAN:
                advance();
                return new LiteralNode(currentToken.value);
            case ATOM:
                advance();
                return new AtomNode(currentToken.value);
            case LESS:
            case LESSEQ:
            case GREATER:
            case GREATEREQ:
            case EQUAL:
            case NONEQUAL:
                return parseComparison(currentToken.value);
            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
                return parseOperation(currentToken.value);
            default:
                throw new Exception("UNEXPECTED TOKEN: " + currentToken);
        }
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

        switch (operatorToken.value) {
            case "setq":
                return parseSETQ();
            case "func":
                return parseFUNC();
            case "cond":
                return parseCOND();
            case "prog":
                return parsePROG();
            case "plus":
            case "minus":
            case "times":
            case "divide":
                return parseOperation(operatorToken.value);
            case "head":
                return parseHeadOrTail("head");
            case "tail":
                return parseHeadOrTail("tail");
            case "cons":
                return parseCons();
            case "while":
                return parseWHILE();
            case "return":
                return parseRETURN();
            case "break":
                return parseBREAK();
            case "isint":
            case "isreal":
            case "isbool":
            case "isnull":
            case "isatom":
            case "islist":
                return parsePredicate(operatorToken.value);
            case "equal":
            case "nonequal":
            case "less":
            case "lesseq":
            case "greater":
            case "greatereq":
                return parseComparison(operatorToken.value);
            case "and":
            case "or":
            case "xor":
                return parseLogicalOperator(operatorToken.value);
            case "not":
                return parseNot();
            case "lambda":
                return parseLambda();
            default:
                throw new Exception("UNEXPECTED OPERATOR: " + operatorToken.value);
        }
    }

    private ASTNode parseBREAK() throws Exception {
        advance();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER BREAK");
        return new BreakNode();
    }

    private ASTNode parseRETURN() throws Exception {
        advance();
        ASTNode returnValue = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER RETURN");
        advance();
        return new ReturnNode(returnValue);
    }

    private ASTNode parseWHILE() throws Exception {
        advance();
        consume(Flexer.TokenType.LPAREN, "EXPECTED ( AFTER WHILE");
        ASTNode condition = parseExpr();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER WHILE BODY");
        return new WhileNode(condition, body);
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
        return new ProgNode(statements, finalExpression);
    }

    private ASTNode parseLiteralList() throws Exception {
        List<ASTNode> elements = new ArrayList<>();
        while (!check(Flexer.TokenType.RPAREN)) {
            elements.add(parseExpr()); // add each literal to list
        }
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER LITERAL LIST");
        return new ListNode(elements);
    }

    private ASTNode parseLambda() throws Exception {
        advance();
        List<String> parameters = parseParameterList();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER LAMBDA BODY");
        return new LambdaNode(parameters, body);
    }

    private ASTNode parseLogicalOperator(String operator) throws Exception {
        advance();
        ASTNode leftElement = parseExpr();
        ASTNode rightElement = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + operator);
        return new LogicalOperationNode(operator, leftElement, rightElement);
    }

    private ASTNode parseNot() throws Exception {
        advance();
        ASTNode element = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER NOT");
        return new NotNode(element);
    }

    private ASTNode parseComparison(String comparison) throws Exception {
        advance();
        ASTNode leftElement = parseExpr();
        ASTNode rightElement = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + comparison);
        return new ComparisonNode(comparison, leftElement, rightElement);
    }

    //issmth
    private ASTNode parsePredicate(String predicate) throws Exception {
        advance();
        ASTNode element = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + predicate);

        return new PredicateNode(predicate, element);
    }

    private ASTNode parseHeadOrTail(String type) throws Exception {
        advance();
        ASTNode listExpr = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER " + type);
        if (type.equals("head")) {
            return new HeadNode(listExpr);
        } else {
            return new TailNode(listExpr);
        }
    }

    private ASTNode parseCons() throws Exception {
        advance();
        ASTNode item = parseExpr();//what to add
        ASTNode list = parseExpr();//to list
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER CONS");
        return new ConsNode(item, list);
    }

    private OperationNode parseOperation(String operator) throws Exception {
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
        return new OperationNode(operator, operands, false);
    }

    private AssignmentNode parseSETQ() throws Exception {
        advance();
        String variable = consume(Flexer.TokenType.ATOM, "EXPECTED VARIABLE FOR SETQ").value;
        ASTNode value = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER SETQ ASSIGNMENT");
        return new AssignmentNode(variable, value);
    }

    private FunctionNode parseFUNC() throws Exception {
        advance();
        String functionName = consume(Flexer.TokenType.ATOM, "MISSING FUNCTION NAME").value;
        List<String> parameters = parseParameterList();
        ASTNode body = parseExpr();
        consume(Flexer.TokenType.RPAREN, "EXPECTED ) AFTER FUNCTION BODY");
        return new FunctionNode(functionName, parameters, body);
    }

    private ConditionNode parseCOND() throws Exception {
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

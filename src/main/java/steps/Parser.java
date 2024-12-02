package steps;

import ast.ASTNodeFactory;
import ast.nodes.ASTNode;
import ast.nodes.ConditionBranch;
import ast.nodes.FunctionNode;
import ast.nodes.LambdaNode;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final ASTNodeFactory factory;
    private List<Token> tokens;
    private int currentTokenIndex;
    private final SymbolTable globalScope;
    private SymbolTable currentScope;

    public Parser() {
        factory = new ASTNodeFactory();
        this.globalScope = new SymbolTable(null);
        this.currentScope = globalScope;
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
                ASTNode nullnode = factory.createNullNode(currentToken.line);
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
                    throw new Exception("ERROR: UNDEFINED VARIABLE " + currentToken.value + " at line " + currentToken.line);
                }
            }
            case LESS, LESSEQ, GREATER, GREATEREQ, EQUAL, NONEQUAL -> parseComparison(currentToken.value);
            case PLUS, MINUS, TIMES, DIVIDE -> parseOperation(currentToken.value);
            default ->
                    throw new Exception("ERROR: UNEXPECTED TOKEN: " + currentToken + " at line " + currentToken.line);
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
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER QUOTE at line: " + quoteToken.line);
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
        Token opTok = peek();
        throw new Exception(errorMessage + ". FOUND: " + opTok + " at line: " + opTok.getLine());
    }

    private ASTNode parseParenthesizedExpr() throws Exception {
        consume(TokenType.LPAREN, "ERROR: EXPECTED (");
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
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER EVAL at line: " + op.getLine());
        ASTNode evalnode = factory.createEvalNode(q, op.line);
        evalnode.addChild(q);
        evalnode.setType(ASTNode.NodeType.EVAL);
        return evalnode;
    }

    private ASTNode parseBREAK() throws Exception {
        Token op = advance();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER BREAK at line: " + op.getLine());
        ASTNode breaknode = factory.createBreakNode(op.line);
        breaknode.setType(ASTNode.NodeType.BREAK);
        return breaknode;
    }

    private ASTNode parseRETURN() throws Exception {
        Token op = advance();
        ASTNode returnValue = parseExpr();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER RETURN at line: " + op.getLine());
        ASTNode returnnode = factory.createReturnNode(returnValue, op.line);
        returnnode.addChild(returnValue);
        returnnode.setType(ASTNode.NodeType.RETURN);
        return returnnode;
    }

    private ASTNode parseWHILE() throws Exception {
        Token op = advance();
        consume(TokenType.LPAREN, "ERROR: EXPECTED ( AFTER WHILE at line: " + op.getLine());
        ASTNode condition = parseExpr();
        List<ASTNode> body = new ArrayList<>();
        while (!check(TokenType.RPAREN)) {
            body.add(parseExpr());
        }
        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER WHILE BODY at line: " + op.getLine());
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

        consume(TokenType.LPAREN, "ERROR: EXPECTED ( AFTER PROG at line: " + op.getLine());
        while (!check(TokenType.RPAREN)) {
            String varName = consume(TokenType.ATOM, "ERROR: EXPECTED ATOM FOR LOCAL VARIABLE at line: " + op.getLine()).value;
            currentScope.define(varName, null);
        }

        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER LOCAL VARIABLE LIST at line: " + op.getLine());

        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.RPAREN)) {
            statements.add(parseExpr());
        }

        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER PROG BLOCK at line: " + op.getLine());
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

        String functionName = consume(TokenType.ATOM, "ERROR: MISSING FUNCTION NAME at line: " + op.getLine()).value;

        FunctionNode placeholderFunctionNode = new FunctionNode(functionName, new ArrayList<>(), null, op.line, op.line);
        placeholderFunctionNode.setType(ASTNode.NodeType.FUNC);

        globalScope.define(functionName, placeholderFunctionNode);
        currentScope.define(functionName, placeholderFunctionNode);
        List<String> parameters = new ArrayList<>();
        consume(TokenType.LPAREN, "ERROR: EXPECTED ( AFTER FUNCTION NAME at line: " + op.getLine());
        while (!check(TokenType.RPAREN)) {
            String varName = consume(TokenType.ATOM, "ERROR: EXPECTED ATOM FOR LOCAL PARAMETER at line: " + op.getLine()).value;
            parameters.add(varName);
            currentScope.define(varName, null);
        }

        placeholderFunctionNode = new FunctionNode(functionName, parameters, null, op.line, op.line);
        globalScope.define(functionName, placeholderFunctionNode);
        currentScope.define(functionName, placeholderFunctionNode);

        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER FUNCTION PARAMETER LIST at line: " + op.getLine());
        List<ASTNode> bodyExpressions = new ArrayList<>();

        while (!check(TokenType.RPAREN)) {
            ASTNode expr = parseExpr();
            bodyExpressions.add(expr);
        }
        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER FUNCTION BODY at line: " + op.getLine());

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
        consume(TokenType.LPAREN, "ERROR: EXPECTED ( AFTER LAMBDA at line: " + op.getLine());
        while (!check(TokenType.RPAREN)) {
            String varName = consume(TokenType.ATOM, "ERROR: EXPECTED ATOM FOR LOCAL PARAMETER at line: " + op.getLine()).value;
            parameters.add(varName);
            currentScope.define(varName, null);
        }
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER LAMBDA PARAMETER LIST at line: " + op.getLine());

        ASTNode body = parseExpr();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER LAMBDA BODY at line: " + op.getLine());
        currentScope = previousScope;

        ASTNode lambdanode = factory.createLambdaNode(parameters, body, op.line);
        lambdanode.addChild(body);
        lambdanode.setType(ASTNode.NodeType.LAMBDA);
        return lambdanode;
    }

    private ASTNode parseLiteralList() throws Exception {
        List<ASTNode> elements = new ArrayList<>();

        int line = 0;
        if (check(TokenType.RPAREN)) {
            Token clo = advance();
            line = clo.getLine();
            ASTNode listNode = factory.createListNode(elements, clo.line);
            listNode.setType(ASTNode.NodeType.LIST);
            return listNode;
        }

        while (!check(TokenType.RPAREN)) {
            elements.add(parseExpr()); // add each literal to list
        }
        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER LITERAL LIST at line: " + line);
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
        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER " + operator + " at line: " + op.getLine());

        ASTNode logicalopnode = factory.createLogicalOperationNode(operator, leftElement, rightElement, op.line, clo.line);
        logicalopnode.addChild(leftElement);
        logicalopnode.addChild(rightElement);
        logicalopnode.setType(ASTNode.NodeType.LOGICALOP);
        return logicalopnode;
    }

    private ASTNode parseNot() throws Exception {
        Token op = advance();
        ASTNode element = parseExpr();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER NOT at line: " + op.getLine());
        ASTNode notnode = factory.createNotNode(element, op.line);
        notnode.addChild(element);
        notnode.setType(ASTNode.NodeType.NOT);
        return notnode;
    }

    private ASTNode parseComparison(String comparison) throws Exception {
        Token op = advance();
        ASTNode leftElement = parseExpr();
        ASTNode rightElement = parseExpr();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER " + comparison + " at line: " + op.getLine());

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
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER " + predicate + " at line: " + op.getLine());
        ASTNode predicatenode = factory.createPredicateNode(predicate, element, op.line);
        predicatenode.addChild(element);
        predicatenode.setType(ASTNode.NodeType.PREDICATE);
        return predicatenode;
    }

    private ASTNode parseHeadOrTail(String type) throws Exception {
        Token op = advance();
        ASTNode listExpr = parseExpr();
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER " + type + " at line: " + op.getLine());
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
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER CONS at line: " + op.getLine());
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
        consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER OPERATION at line: " + op.getLine());

        if (operands.size() < 2 && !(operator.equals("plus") || operator.equals("minus"))) {
            throw new Exception("ERROR: IMPOSSIBLE OPERATION at line: " + op.getLine());
        }
        ASTNode opnode = factory.createOperationNode(operator, operands, false, op.line);
        for (ASTNode operand : operands) {
            opnode.addChild(operand);
        }
        opnode.setType(operands.getFirst().getType());
        return opnode;
    }

    private ASTNode parseFuncCall(String functionName) throws Exception {
        int line = peek().line;
        if (!globalScope.isDefined(functionName)) {
            throw new Exception("ERROR: UNDEFINED FUNCTION " + functionName + " at line " + line);
        }

        ASTNode functionNode = globalScope.lookup(functionName);
        if (!(functionNode instanceof FunctionNode)) {
            throw new Exception("ERROR: " + functionName + " IS NOT A FUNCTION at line: " + line);
        }

        List<ASTNode> operands = new ArrayList<>();

        advance();

        while (!check(TokenType.RPAREN)) {
            ASTNode expr = parseExpr();
            operands.add(expr);
        }

        if (operands.size() != ((FunctionNode) functionNode).getParameters().size()) {
            throw new Exception("ERROR: INCORRECT NUMBER OF PARAMETERS FOR FUNCTION " + functionName +
                    "EXPECTED-GOT: " + ((FunctionNode) functionNode).getParameters().size() + "-" + operands.size() +
                    " at line: " + line);
        }

        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER OPERATION at line: " + line);
        return factory.createFunctionCallNode(functionName, operands, clo.line);
    }

    private ASTNode parseLambdaCall(String lambdaName) throws Exception {
        int line = peek().line;
        if (!globalScope.isDefined(lambdaName)) {
            throw new Exception("ERROR: UNDEFINED LAMBDA " + lambdaName + " at line: " + line);
        }

        ASTNode lambdaNode = globalScope.lookup(lambdaName);
        if (!(lambdaNode instanceof LambdaNode)) {
            throw new Exception("ERROR: " + lambdaName + " IS NOT A LAMBDA at line: " + line);
        }

        List<ASTNode> operands = new ArrayList<>();

        while (!check(TokenType.RPAREN)) {
            operands.add(parseExpr());
        }

        Token clo = consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER OPERATION at line: " + line);
        ASTNode lambdacallnode = factory.createLambdaCallNode(lambdaName, operands, clo.line);
        lambdacallnode.setType(ASTNode.NodeType.LAMBDACALL);
        return lambdacallnode;
    }

    private ASTNode parseSETQ() throws Exception {
        Token op = advance();
        String variable = consume(TokenType.ATOM, "ERROR: EXPECTED VARIABLE FOR SETQ at line: " + op.getLine()).value;
        currentScope.define(variable, null);
        ASTNode value = parseExpr();
        currentScope.define(variable, value); //add var to scope in symbol table
		consume(TokenType.RPAREN, "ERROR: EXPECTED ) AFTER SETQ ASSIGNMENT at line: " + op.getLine());
        ASTNode assignmentnode = factory.createAssignmentNode(variable, value, op.line);
        assignmentnode.addChild(value);
        assignmentnode.setType(ASTNode.NodeType.ASSIGNMENT);
        return assignmentnode;
    }

    private ASTNode parseCOND() throws Exception {
        Token op = advance();
        List<ConditionBranch> branches = new ArrayList<>();

        if (isAtEnd())
            throw new Exception("ERROR: UNEXPECTED END OF INPUT WHILE PARSING CONDITION-ACTION PAIRS at line: " + op.getLine());
        ASTNode condition = parseExpr();

        if (isAtEnd()) throw new Exception("ERROR: EXPECTED ACTION AFTER CONDITION at line: " + op.getLine());
        ASTNode action = parseExpr();
        branches.add(factory.createConditionBranch(condition, action));

        //default case
        ASTNode defaultAction = null;
        if (check(TokenType.LPAREN)) {
            defaultAction = parseExpr();
        }
        Token clo = consume(TokenType.RPAREN, "ERROR: MISSING ) AFTER COND EXPR at line: " + op.getLine());
        ASTNode condnode = factory.createConditionNode(branches, defaultAction, op.line, clo.line);
        condnode.addChild(condition);
        condnode.addChild(action);
        condnode.setType(ASTNode.NodeType.COND);
        return condnode;
    }
}

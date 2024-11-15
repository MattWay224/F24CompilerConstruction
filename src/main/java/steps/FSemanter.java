package steps;

import ast.nodes.*;
import things.FunctionScopeTable;
import things.SymbolTable;

import java.util.*;

public class FSemanter {
    private final SymbolTable symbolTable;
    private final FunctionScopeTable functionScopeTable = new FunctionScopeTable();


    public FSemanter(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void checkArithmeticOperation(ASTNode operation) throws Exception {
        String operator = ((OperationNode) operation).getOperator();
        List<ASTNode> operands = ((OperationNode) operation).getChildren();

        if (operands.isEmpty()) {
            throw new Exception("ARITHMETIC OPERATION " + operator + " HAS NO OPERANDS");
        }

        ASTNode.NodeType expectedType = operands.getFirst().getType();
        for (ASTNode operand : operands) {
            if (operand.getType() != expectedType) {
                throw new Exception("TYPE ERROR IN " + operator + " OPERATION: EXPECTED " + expectedType
                        + ", FOUND " + operand.getType() + " on line " + ((OperationNode) operation).getLine());
            }
        }

        simplifyExpression(operation);
    }

    private void checkLogicalOperation(LogicalOperationNode operation) throws Exception {
        String operator = operation.getOperator();
        ASTNode leftOperand = operation.getChildren().getFirst();
        ASTNode rightOperand = operation.getChildren().getLast();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("LOGICAL OPERATION " + operator + " HAS NO OPERANDS");
        }

        if (leftOperand.getType() != ASTNode.NodeType.BOOL || rightOperand.getType() != ASTNode.NodeType.BOOL) {
            throw new Exception("TYPE ERROR: LOGICAL OPERATORS REQUIRE BOOLEAN OPERANDS on line "
                    + operation.getLineClo());
        }

        evalLogicalOperation(operation);
    }

    private void checkPredicate(PredicateNode op) throws Exception {
        String predicate = op.getPredicate();
        ASTNode element = op.getElement();

        boolean result;
        switch (predicate) {
            case "isint" -> result = element.isInt();
            case "isreal" -> result = element.isReal();
            case "isbool" -> result = element.getType() == ASTNode.NodeType.BOOL;
            case "isnull" -> result = element.getType() == ASTNode.NodeType.NULL;
            case "isatom" ->
                    result = (element.getType() == ASTNode.NodeType.ATOM || element.getType() == ASTNode.NodeType.BOOL);
            case "islist" -> result = element instanceof ListNode || element instanceof ConsNode;
            default -> throw new Exception("Unsupported predicate: " + predicate);
        }

        BooleanNode constnode = new BooleanNode(result, op.getLine());
        constnode.setType(ASTNode.NodeType.BOOL);
        constnode.setEvaluated(true);
        constnode.setConstantValue(constnode);
        replaceNodeInParent(op.getParent(), op, constnode);
        op.setEvaluated(true);
        op.setConstantValue(constnode);
    }

    private void evalLogicalOperation(LogicalOperationNode operation) throws Exception {
        String operator = operation.getOperator();
        ASTNode leftOperand = operation.getChildren().getFirst();
        ASTNode rightOperand = operation.getChildren().size() > 1 ? operation.getChildren().getLast() : null;

        boolean leftValue = ((BooleanNode) leftOperand).getValue();
        boolean rightValue = rightOperand != null ? ((BooleanNode) rightOperand).getValue() : false;

        boolean result;
        switch (operator) {
            case "and" -> result = leftValue && rightValue;
            case "or" -> result = leftValue || rightValue;
            case "xor" -> result = leftValue ^ rightValue;
            default -> throw new Exception("Unsupported logical operator: " + operator);
        }

        BooleanNode constnode = new BooleanNode(result, operation.getLineClo());
        constnode.setType(ASTNode.NodeType.BOOL);
        constnode.setEvaluated(true);
        constnode.setConstantValue(constnode);
        replaceNodeInParent(operation.getParent(), operation, constnode);
        operation.setEvaluated(true);
        operation.setConstantValue(constnode);
    }

    private void checkNotNode(NotNode operation) throws Exception {
        ASTNode element = operation.getChildren().getFirst();
        boolean result = !((BooleanNode) element).getValue();
        BooleanNode constnode = new BooleanNode(result, operation.getLine());
        constnode.setType(ASTNode.NodeType.BOOL);
        constnode.setEvaluated(true);
        constnode.setConstantValue(constnode);
        replaceNodeInParent(operation.getParent(), operation, constnode);
        operation.setEvaluated(true);
        operation.setConstantValue(constnode);
    }

    private void checkComparisonOperation(ComparisonNode operation) throws Exception {
        String comparison = operation.getComparison();
        ASTNode leftOperand = operation.getChildren().getFirst();
        ASTNode rightOperand = operation.getChildren().getLast();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("COMPARISON " + comparison + " HAS NO OPERANDS");
        }

        if (!leftOperand.getClass().getSimpleName().equals(rightOperand.getClass().getSimpleName())) {
            throw new Exception("TYPE ERROR IN COMPARISON: " + leftOperand.getClass()
                    + " CANNOT BE COMPARED WITH " + rightOperand.getClass()
                    + " on line " + operation.getLine());
        }
    }

    public void analyze(ASTNode root) throws Exception {
        traverseAndCheck(root);
    }

    private void traverseAndCheck(ASTNode node) throws Exception {
        if (node == null) return;

//        if (node instanceof FunctionCallNode && !node.isEvaluated()) {
//            analyzeFuncCall((FunctionCallNode) node);
//        }else if (node.getClass().getSimpleName().equals("ConditionNode")) {
//            analyzeCond((ConditionNode) node);
//        }
        if (node instanceof FunctionNode) {
            return;
        }


        for (ASTNode child : node.getChildren()) {
            traverseAndCheck(child);
        }

        if (node.getClass().getSimpleName().equals("OperationNode") && !node.isConstant()) {
            checkArithmeticOperation(node);
        } else if (node.getClass().getSimpleName().equals("ComparisonNode") && !node.isConstant()) {
            checkComparisonOperation((ComparisonNode) node);
        } else if (node.getClass().getSimpleName().equals("LogicalOperationNode")) {
            checkLogicalOperation((LogicalOperationNode) node);
        } else if (node.getClass().getSimpleName().equals("PredicateNode")) {
            checkPredicate((PredicateNode) node);
        } else if (node.getClass().getSimpleName().equals("NotNode")) {
            checkNotNode((NotNode) node);
        } else if (node.getClass().getSimpleName().equals("ConditionNode")) {
            analyzeCond((ConditionNode) node);
        } else if (node.getClass().getSimpleName().equals("FunctionCallNode")) {
            analyzeFuncCall((FunctionCallNode) node);
        }

        simplifyExpression(node);

    }

    //Constant Expression Simplification
    public ASTNode simplifyExpression(ASTNode operation) throws Exception {
        ASTNode parent = operation.getParent();
        if (operation instanceof OperationNode) {
            OperationNode opNode = (OperationNode) operation;
            List<ASTNode> operands = new ArrayList<>();

            for (ASTNode operand : opNode.getChildren()) {
                ASTNode simplified = simplifyExpression(operand);
                if (simplified instanceof FunctionCallNode) {
                    analyzeFuncCall((FunctionCallNode) simplified);
                }
                if (simplified == null) {
                    System.err.println("Warning: Simplified operand is null for operation: " + opNode.getOperator());
                }
                operands.add(simplified);
            }
            if (operands.stream().allMatch(ASTNode::isInt)) {
                Number result = evalInt(opNode.getOperator(), operands);
                LiteralNode constantNode = new LiteralNode(result.toString(), opNode.getLine());
                constantNode.setType(ASTNode.NodeType.ATOM);
                replaceNodeInParent(parent, opNode, constantNode);
                return constantNode;
            } else if (operands.stream().anyMatch(ASTNode::isReal)) {
                Number result = evalReal(opNode.getOperator(), operands);
                LiteralNode constantNode = new LiteralNode(result.toString(), opNode.getLine());
                constantNode.setType(ASTNode.NodeType.ATOM);
                replaceNodeInParent(parent, opNode, constantNode);
                return constantNode;
            }
        } else if (operation instanceof ComparisonNode) {
            ComparisonNode compNode = (ComparisonNode) operation;
            ASTNode leftOperand = compNode.getChildren().getFirst();
            ASTNode rightOperand = compNode.getChildren().getLast();
            if (leftOperand.isConstant() && rightOperand.isConstant()) {
                boolean result = evalComp(compNode);
                BooleanNode constNode = new BooleanNode(result, compNode.getLine());
                constNode.setType(ASTNode.NodeType.BOOL);
                replaceNodeInParent(parent, compNode, constNode);
                compNode.setConstantValue(new BooleanNode(result, compNode.getLine()));
                replaceNodeInParent(compNode, compNode, constNode);
                return constNode;
            }
        } else if (operation instanceof ConsNode) {
            constructListFromCons((ConsNode) operation);
        } else if (operation instanceof HeadNode) {
            //ASTNode simplifiedHead =
            evalHead((HeadNode) operation);
            //replaceNodeInParent(parent, operation, simplifiedHead);
            //return simplifiedHead;
        } else if (operation instanceof TailNode) {
            //ASTNode simplifiedTail =
            evalTail((TailNode) operation);
            //replaceNodeInParent(parent, operation, simplifiedTail);
            //return simplifiedTail;
        } else if (operation instanceof AtomNode) {
            if (symbolTable.isDefined(((AtomNode) operation).getValue())) {
                return symbolTable.lookup(((AtomNode) operation).getValue());
            } else if (operation.getParent().getParent() instanceof FunctionCallNode) {
                return functionScopeTable.get((FunctionCallNode) operation.getParent().getParent(), ((AtomNode) operation).getValue());
            }
        }

        return operation;
    }

    private void evalHead(HeadNode headNode) throws Exception {
        ASTNode listNode = headNode.getChildren().getFirst();
        List<ASTNode> elements = ((ListNode) listNode).getElements();
        if (!elements.isEmpty()) {
            if (elements.getFirst() instanceof LiteralNode) {
                LiteralNode newnode = new LiteralNode(((LiteralNode) elements.getFirst()).getValue(), headNode.getLine());
                headNode.setConstantValue((ASTNode) newnode);
                replaceNodeInParent(headNode.getParent(), headNode, newnode);
                //headNode.getParent().setConstantValue(newnode);
            }
        } else {
            throw new Exception("CAN NOT TAKE HEAD OF EMPTY LIST on line " + headNode.getLine());
        }
    }

    private void evalTail(TailNode tailNode) throws Exception {
        ASTNode listNode = tailNode.getConstantValue();
        List<ASTNode> elements;
        try {
            elements = ((ListNode) listNode).getElements();
        } catch (Exception e) {
            elements = ((ListNode) tailNode.getChildren().getFirst()).getElements();
        }
        if (elements.size() > 1) {
            ListNode newnode = new ListNode(elements.subList(1, elements.size()), tailNode.getLine());
            replaceNodeInParent(tailNode.getParent(), tailNode, newnode);
            tailNode.setConstantValue(newnode);
            //tailNode.getParent().setConstantValue(newnode);
        } else {
            ListNode newnode = new ListNode(new ArrayList<>(), tailNode.getLine());
            replaceNodeInParent(tailNode.getParent(), tailNode, newnode);
            tailNode.setConstantValue(newnode);
            //tailNode.getParent().setConstantValue(newnode);
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

    private boolean evalComp(ComparisonNode operation) throws Exception {
        ASTNode leftOperand = operation.getChildren().getFirst();
        ASTNode rightOperand = operation.getChildren().getLast();

        if (leftOperand.isConstant() && rightOperand.isConstant()) {
            double left = Double.parseDouble(((LiteralNode) leftOperand).getValue());
            double right = Double.parseDouble(((LiteralNode) rightOperand).getValue());
            boolean res = switch (operation.getComparison()) {
                case "equal" -> left == right;
                case "nonequal" -> left != right;
                case "less" -> left < right;
                case "lesseq" -> left <= right;
                case "greater" -> left > right;
                case "greatereq" -> left >= right;
                default -> false;
            };
            operation.setConstantValue(new BooleanNode(res, operation.getLine()));
            return res;
        }
        return false;
    }

    private List<ASTNode> extractElementsFromCons(ConsNode node) throws Exception {
        List<ASTNode> elements = new ArrayList<>();

        while (node != null) {
            if (node instanceof ConsNode consNode) {
                ASTNode head = consNode.getHead();
                if (head instanceof LiteralNode) {
                    elements.add(head);
                } else if (head instanceof ConsNode) {
                    elements.addAll(extractElementsFromCons((ConsNode) head));
                } else {
                    elements.add(evaluateToLiteralNode(head));
                }

                ASTNode tail = consNode.getTail();
                if (tail instanceof ConsNode) {
                    node = (ConsNode) tail;
                } else if (tail instanceof ListNode) {
                    for (ASTNode listElement : ((ListNode) tail).getElements()) {
                        elements.add(evaluateToLiteralNode(listElement));
                    }
                    break;
                } else {
                    elements.add(evaluateToLiteralNode(tail));
                    break;
                }
            } else {
                throw new Exception("Expected a ConsNode for list construction, but found " + node.getClass().getSimpleName());
            }
        }

        return elements;
    }

    private ASTNode evaluateToLiteralNode(ASTNode node) throws Exception {
        if (node instanceof LiteralNode) {
            return node;
        }
        throw new Exception("Non-literal node encountered in list construction.");
    }

    public ASTNode constructListFromCons(ConsNode consNode) throws Exception {
        List<ASTNode> elements = extractElementsFromCons(consNode);
        ListNode listNode = new ListNode(elements, consNode.getLine());
        replaceNodeInParent(consNode.getParent(), consNode, listNode);
        return listNode;
    }

    private void analyzeFuncCall(FunctionCallNode functionCall) throws Exception {
        functionScopeTable.enterScope(functionCall);

        String functionName = functionCall.getFunctionName();
        List<ASTNode> parameters = new ArrayList<>();
        for (int i = 1; i < functionCall.getChildren().size(); i++) {
            parameters.add(simplifyExpression(functionCall.getChildren().get(i)));
        }

        List<ASTNode> evaluatedParams = new ArrayList<>();

        FunctionNode functionDefinition = (FunctionNode) symbolTable.lookup(functionName);
        FunctionNode clonedFunction = functionDefinition.clone();
        List<String> funcDefParams = clonedFunction.getParameters();

        if (parameters.size() != funcDefParams.size()) {
            throw new Exception("PARAMETERS MISMATCH IN FUNCTION CALL :" + functionName + ", EXPECTED " + funcDefParams.size() + ", FOUND " + parameters.size());
        }

        for (int i = 0; i < parameters.size(); i++) {
            functionScopeTable.put(functionCall, funcDefParams.get(i), parameters.get(i));
        }

        List<ASTNode> expressions = clonedFunction.getChildren();
        for (ASTNode expression : expressions) {
            if (expression instanceof AssignmentNode) {
                functionScopeTable.put(functionCall, ((AssignmentNode) expression).getVariable(), ((AssignmentNode) expression).getValue());
            }
        }

        Map<String, ASTNode> paramArgMap = functionScopeTable.getMappingsForScope(functionCall);

        for (ASTNode expr : expressions) {
            replaceParamsWithArgs(expr, paramArgMap, new HashSet<>());
        }

        for (ASTNode expr : expressions) {
            if (expr instanceof ConditionNode) {
                analyzeCond((ConditionNode) expr);
            } else {
                traverseAndCheck(expr);
            }

            //if ((expr instanceof ReturnNode && expr.isEvaluated())) {
            if ((expr.isEvaluated())) {
                functionCall.setEvaluated(true);
                functionCall.setConstantValue(expr.getChildren().getFirst());
                replaceNodeInParent(functionCall.getParent(), functionCall, expr.getChildren().getFirst());
                break;
            }
        }
        functionScopeTable.exitScope(functionCall);
    }

    private void handleSetq(FunctionCallNode functionCall, ASTNode setqNode) throws Exception {
        if (setqNode instanceof OperationNode && ((OperationNode) setqNode).getOperator().equals("setq")) {
            List<ASTNode> children = setqNode.getChildren();
            if (children.size() != 2) {
                throw new Exception("INVALID SETQ SYNTAX: EXPECTED (setq name value)");
            }

            String varName = ((AtomNode) children.get(0)).getValue();
            ASTNode valueNode = simplifyExpression(children.get(1));

            // Add the variable to the current function's scope
            functionScopeTable.put(functionCall, varName, valueNode);
        }
    }

    private ASTNode analyzeCond(ConditionNode conditionNode) throws Exception {
        ASTNode defaultAction = conditionNode.getChildren().getLast();
        boolean branchExecuted = false;

        ASTNode condition = conditionNode.getChildren().getFirst();
        ASTNode action = conditionNode.getChildren().get(1);

        traverseAndCheck(condition);
        if (condition.isEvaluated() && condition instanceof ComparisonNode) {
            //BooleanNode boolCondition = (BooleanNode) condition;
            if ((((ComparisonNode) condition).getConstantValue()).getValue()) {
                traverseAndCheck(action);
                conditionNode.setEvaluated(true);
                conditionNode.setConstantValue(action);
                replaceNodeInParent(conditionNode.getParent(), conditionNode, action);
                if (action instanceof ReturnNode && ((ReturnNode) action).getChildren().getFirst() != null) {
                    replaceNodeInParent(conditionNode, conditionNode.getChildren().getFirst(), ((ReturnNode) action).getChildren().getFirst());
                }
                branchExecuted = true;
                return conditionNode;
            } else if (defaultAction != null) {
                traverseAndCheck(defaultAction);
                conditionNode.setEvaluated(true);
                conditionNode.setConstantValue(defaultAction);

                replaceNodeInParent(conditionNode.getParent(), conditionNode, defaultAction);
                if (defaultAction instanceof ReturnNode && ((ReturnNode) defaultAction).getChildren().getFirst() != null) {
                    replaceNodeInParent(conditionNode, conditionNode.getChildren().getFirst(), ((ReturnNode) defaultAction).getChildren().getFirst());
                }

                branchExecuted = true;
                return conditionNode;
            }
        }
        return null;
    }

    private void replaceParamsWithArgs(ASTNode node, Map<String, ASTNode> paramArgMap, Set<ASTNode> visited) throws Exception {
        if (node == null || visited.contains(node)) return;

        // Mark the node as visited
        visited.add(node);

        if (node instanceof AtomNode) {
            String varName = ((AtomNode) node).getValue();
            if (paramArgMap.containsKey(varName)) {
                ASTNode argument = paramArgMap.get(varName);

                if (argument instanceof LiteralNode) {
                    (argument).setType(ASTNode.NodeType.ATOM);
                } else if (argument instanceof BooleanNode) {
                    (argument).setType(ASTNode.NodeType.BOOL);
                }
                replaceNodeInParent(node.getParent(), node, argument);
            }
        } else if (node instanceof OperationNode && node.getChildren().stream().noneMatch(child -> child instanceof FunctionCallNode)) {
            for (ASTNode child : new ArrayList<>(node.getChildren())) {
                replaceParamsWithArgs(child, paramArgMap, visited);
            }
        }

        for (ASTNode child : node.getChildren()) {
            replaceParamsWithArgs(child, paramArgMap, visited);
        }
    }

    private void replaceNodeInParent(ASTNode parent, ASTNode oldNode, ASTNode newNode) throws Exception {
        if (parent == null) return;
        List<ASTNode> children = parent.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == oldNode) {
                children.set(i, newNode);
                newNode.setEvaluated(true);
                newNode.setParent(parent);

                if (newNode instanceof LiteralNode) {
                    parent.setConstantValue((LiteralNode) newNode);
                    parent.setEvaluated(true);
                }

                break;
            }
        }
    }
}

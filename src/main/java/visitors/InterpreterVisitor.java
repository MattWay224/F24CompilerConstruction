package visitors;

import ast.nodes.*;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class InterpreterVisitor implements ASTVisitor<Object> {
    private final SymbolTable symbolTable;
    private final boolean globalScope; // Flag to track whether in global scope


    public InterpreterVisitor(SymbolTable symbolTable, boolean globalScope) {
        this.symbolTable = symbolTable;
        this.globalScope = globalScope;
    }

    @Override
    public Object visitAssignmentNode(AssignmentNode node) {
        ASTNode action = node.getChildren().getFirst();
        Object value = visit(action);
        if (action instanceof QuoteNode) {
            symbolTable.define(node.getVariable(), new QuoteNode((ASTNode) value, ((QuoteNode) action).getLine()));
        } else if (action instanceof LambdaNode) {
            symbolTable.define(node.getVariable(), new LambdaNode(((LambdaNode) action).getParameters(),
                    ((LambdaNode) action).getBody(), ((LambdaNode) action).getLine()));
        } else {
            symbolTable.define(node.getVariable(), new LiteralNode(value.toString()));
        }
        return null;
    }

    @Override
    public Object visitAtomNode(AtomNode node) {
        try {
            return visit(symbolTable.lookup(node.getValue()));
        } catch (Exception e) {
            throw new RuntimeException("ERROR: " + e.getMessage() + " at line: " + node.getLine());
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

    private boolean isInteger(double value) {
        return value == Math.floor(value);
    }

    private Number evalOperation(String operator, List<Object> operands) {
        List<Double> numericOperands = operands.stream()
                .map(o -> ((Number) o).doubleValue()) // Ensure all are Numbers
                .toList();

        switch (operator) {
            case "plus" -> {
                double result = numericOperands.stream().mapToDouble(Double::doubleValue).sum();
                if (isInteger(result)) {
                    return (int) result;
                } else return result;
            }
            case "minus" -> {
                double result = numericOperands.get(0);
                for (int i = 1; i < numericOperands.size(); i++) {
                    result -= numericOperands.get(i);
                }
                if (isInteger(result)) {
                    return (int) result;
                } else return result;
            }
            case "times" -> {
                double result = 1.0;
                for (Double operand : numericOperands) {
                    result *= operand;
                }
                if (isInteger(result)) {
                    return (int) result;
                } else return result;
            }
            case "divide" -> {
                double result = numericOperands.get(0);
                for (int i = 1; i < numericOperands.size(); i++) {
                    double divisor = numericOperands.get(i);
                    if (divisor == 0) {
                        throw new RuntimeException("ERROR: DIVISION BY ZERO");
                    }
                    result /= divisor;
                }
                if (isInteger(result)) {
                    return (int) result;
                } else return result;
            }
            default -> throw new RuntimeException("ERROR: UNKNOWN OPERATOR " + operator);
        }
    }


    @Override
    public Object visitProgNode(ProgNode node) {
        Object result = null;
        for (ASTNode statement : node.getStatements()) {
            if (statement instanceof ProgNode) {
                InterpreterVisitor localVisitor = new InterpreterVisitor(symbolTable, false);
                result = localVisitor.visit(statement);
            } else {
                result = visit(statement);
            }
            if (globalScope && result != null) { // Only print in global scope
                System.out.println(result);
            }
        }
        return result;
    }

    @Override
    public Object visitConditionNode(ConditionNode node) {
        List<ConditionBranch> branches = node.getBranches();

        if ((boolean) branches.get(0).getCondition().accept(this)) {
            return branches.get(0).getAction().accept(this);
        } else try {
            return node.getDefaultAction().accept(this);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Object visitWhileNode(WhileNode node) {
        boolean breakflag = false;
        while ((Boolean) visit(node.getCondition())) {
            if (breakflag) break;
            for (ASTNode stmt : node.getBody()) {
                visit(stmt);
                if (stmt instanceof BreakNode) {
                    breakflag = true;
                    break;
                }
            }
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

            Object function = symbolTable.lookup(node.getFunctionName());
            SymbolTable functionScope = new SymbolTable(symbolTable);

            if (function instanceof LambdaNode) {
                LambdaNode f = (LambdaNode) function;
                for (int i = 0; i < f.getParameters().size(); i++) {
                    String param = f.getParameters().get(i);
                    Object argValue = visit(node.getParameters().get(i + 1));
                    functionScope.define(param, new LiteralNode(argValue.toString()));
                }

                InterpreterVisitor functionInterpreter = new InterpreterVisitor(functionScope, false);
                return functionInterpreter.visit(f.getBody());
            } else if (function instanceof FunctionNode) {
                FunctionNode f = (FunctionNode) function;
                for (int i = 0; i < f.getParameters().size(); i++) {
                    String param = f.getParameters().get(i);
                    Object argValue = visit(node.getParameters().get(i));
                    functionScope.define(param, new LiteralNode(argValue.toString()));
                }

                InterpreterVisitor functionInterpreter = new InterpreterVisitor(functionScope, false);
                return functionInterpreter.visit(f.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("ERROR: IN FUNCTION CALL " + e.getMessage() + " at line: " + node.getLine());
        }
        return null;
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
        throw new RuntimeException("ERROR: EXPECTED BOOLEAN FOR NOT at line: " + node.getLine());
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
            default -> throw new RuntimeException("ERROR: UNKNOWN COMPARISON OPERATOR at line: " + node.getLine());
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
                default -> throw new RuntimeException("ERROR: UNKNOWN LOGICAL OPERATOR " + operator + " at line: " +
                        node.getLineOp());
            };
        }
        throw new RuntimeException("ERROR: UNKNOWN LOGICAL OPERATOR " + operator + " at line: " + node.getLineOp());
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
            throw new RuntimeException("ERROR: EMPTY LIST at line: " + node.getLine());
        }
        return list.getFirst();
    }

    @Override
    public Object visitTailNode(TailNode node) {
        List<Object> list = (List<Object>) visit(node.getTail());
        if (list.isEmpty()) {
            throw new RuntimeException("ERROR: EMPTY LIST at line: " + node.getLine());
        }
        return list.subList(1, list.size());
    }

    @Override
    public Object visitLambdaNode(LambdaNode node) {
        return null;
    }

    @Override
    public Object visitLambdaCallNode(LambdaCallNode node) {
        return null;
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
        throw new RuntimeException("ERROR: UNEXPECTED ARGUMENT FOR EVAL at line: " + node.getLine());
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

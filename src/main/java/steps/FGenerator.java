package steps;

import ast.nodes.*;
import things.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FGenerator {

    private final SymbolTable symbolTable;
    private final FSemanter semanter;

    private int stackPointer;

    private final List<String> generatedMethods;
    private final Map<String, Integer> variableOffsets;

    public FGenerator(SymbolTable symbolTable, FSemanter semanter) throws Exception {
        this.semanter = semanter;
        this.symbolTable = symbolTable;
        this.variableOffsets = new HashMap<>();
        this.stackPointer = 0;
        this.generatedMethods = new ArrayList<>();
    }

    public void generate(ASTNode node) throws Exception {
        symbolTable.simplifyAllSymbols(semanter);
        visit(node);
        for (String method : generatedMethods) {
            System.out.println(method);
        }
    }

    private Object visit(ASTNode node) throws Exception {
        Object res = null;
        switch (node.getClass().getSimpleName()) {
            case "AssignmentNode" -> visitAssignment((AssignmentNode) node);
            case "AtomNode" -> visitAtom((AtomNode) node);
            case "LiteralNode" -> res=visitLiteral((LiteralNode) node);
            case "BooleanNode" -> res=visitBool((BooleanNode) node);
            case "BreakNode" -> visitBreak((BreakNode) node);
            case "ComparisonNode" -> res=visitComparison((ComparisonNode) node);
            case "ConditionNode" -> res=visitCondition((ConditionNode) node);
            case "ConsNode" -> res=visitCons((ConsNode) node);
            case "EvalNode" -> visitEval((EvalNode) node);
            case "FunctionNode" -> visitFunction((FunctionNode) node);
            case "FunctionCallNode" -> res=visitFunctionCall((FunctionCallNode) node);
            case "HeadNode" -> res=visitHead((HeadNode) node);
            case "LambdaNode" -> visitLambda((LambdaNode) node);
            case "LambdaCallNode" -> visitLambdaCall((LambdaCallNode) node);
            case "ListNode" -> res=visitList((ListNode) node);
            case "LogicalOperationNode" -> res=visitLogicalOperation((LogicalOperationNode) node);
            case "NotNode" -> res=visitNot((NotNode) node);
            case "NullNode" -> res=visitNull((NullNode) node);
            case "OperationNode" -> res=visitOperation((OperationNode) node);
            case "PredicateNode" -> res=visitPredicate((PredicateNode) node);
            case "ProgNode" -> visitProg((ProgNode) node);
            case "QuoteNode" -> visitQuote((QuoteNode) node);
            case "ReturnNode" -> visitReturn((ReturnNode) node);
            //case SIGN
            case "TsilNode" -> res=visitTail((TailNode) node);
            case "WhileNode" -> visitWhile((WhileNode) node);
            case "PrintNode" -> visitPrint((PrintNode) node);
            //case VOID -> visitVoid((Void));
            default -> throw new UnsupportedOperationException("Unknown node type: " + node.getType());
        }
        return res;
    }

    private void visitBreak(BreakNode node) {
    }

    private boolean visitComparison(ComparisonNode node) {
        if (node.getParent().isEvaluated()) {
            return ((BooleanNode) node.getParent().getChildren().getFirst()).getValue();
        }
        return false;
    }

    private String visitCondition(ConditionNode node) throws Exception {
        if (node.isEvaluated()) {
            if (node.getConstantValue() instanceof LiteralNode) {
                return visitLiteral((LiteralNode) node.getConstantValue());
            } else {
                visit(node.getConstantValue());
            }
        }
        return "";
    }

    private ArrayList<Object> visitCons(ConsNode node) throws Exception {
        //System.out.println(node.getParent().isEvaluated());
        if (node.getParent().isEvaluated()) {
            try {
                //if print
                return (ArrayList<Object>) visitList(((ListNode) node.getParent().getChildren().getFirst()));
            } catch (Exception e) {
            }
        }
        if (node.isEvaluated()) {
            //if just evaluate
            return (ArrayList<Object>) visitList(((ConsNode) node).getConstantValue());
        }
        return null;
    }

    private void visitEval(EvalNode node) {
    }

    private void visitFunction(FunctionNode node) throws Exception {
    }

    private Object visitFunctionCall(FunctionCallNode node) throws Exception {
        String functionName = node.getFunctionName();
        List<ASTNode> arguments = (node.getParameters());

        if (node.getConstantValue() instanceof LiteralNode) {
            return visitLiteral((LiteralNode) node.getConstantValue());
        }
        return null;
    }

    private String visitHead(HeadNode node) throws Exception {
        if (node.getConstantValue() instanceof LiteralNode) {
            return visitLiteral((LiteralNode) node.getConstantValue());
        }
        return null;
    }

    private void visitLambda(LambdaNode node) {
    }

    private void visitLambdaCall(LambdaCallNode node) {
    }

    private List<Object> visitList(ListNode node) {
        List<Object> result = new ArrayList<>();

        for (ASTNode element : node.getElements()) {
            if (element instanceof LiteralNode) {
                String value = ((LiteralNode) element).getValue();
                try {
                    int intValue = Integer.parseInt(value);
                    result.add(intValue);
                } catch (NumberFormatException eInt) {
                    try {
                        double doubleValue = Double.parseDouble(value);
                        result.add(doubleValue);
                    } catch (NumberFormatException eDouble) {
                        System.err.println("Invalid number format for value: " + value);
                        result.add(value);
                    }
                }
            } else if (element instanceof BooleanNode) {
                Boolean value = ((BooleanNode) element).getValue();
                result.add(value);
            } else {
                System.err.println("Unsupported node type in list: " + element.getClass().getSimpleName());

            }
        }
        return result;
    }

    private boolean visitLogicalOperation(LogicalOperationNode node) {
        return node.getConstantValue().getValue();
    }

    private boolean visitNot(NotNode node) {
        return node.getConstantValue().getValue();
    }

    private String visitNull(NullNode node) {
        return null;
    }

    private String visitOperation(OperationNode node) {
        if (node.getParent().isEvaluated()) {
            return (((LiteralNode) node.getParent().getChildren().getFirst()).getValue());
        }
        return null;
    }

    private Boolean visitPredicate(PredicateNode node) throws Exception {
        return node.getConstantValue().getValue();
    }

    private void visitProg(ProgNode node) throws Exception {
        for (ASTNode statement : node.getStatements()) {
            visit(statement);
        }
    }

    private void visitQuote(QuoteNode node) {
    }

    private void visitReturn(ReturnNode node) {
    }

    private List<Object> visitTail(TailNode node) {
        return visitList(((TailNode) node).getConstantValue());
    }

    private void visitWhile(WhileNode node) {
    }

    private void visitPrint(PrintNode node) throws Exception {
        // System.out.println("print:" + node.getExpression().getType() + "  " + node.getChildren().getFirst().getType());
        System.out.println("print:" + node.getExpression().getClass().getSimpleName() + "  " + node.getChildren().getFirst().getClass().getSimpleName());
        //switch (node.getExpression().getType()) {
        switch (node.getChildren().getFirst().getClass().getSimpleName()) {
//            case "AtomNode" -> {
//                if (node.getChildren().getFirst().getClass().getSimpleName().equals("AtomNode")) {
//                    String atom_class = symbolTable.lookup(((AtomNode) node.getChildren().getFirst()).getValue()).getClass().getSimpleName();
//                    ASTNode atom = symbolTable.lookup(((AtomNode) node.getChildren().getFirst()).getValue());
//                    if (atom_class.equals("BooleanNode")) {
//                        //System.out.println(((BooleanNode) atom).getValue());
//                        System.out.println(visitBool((BooleanNode) atom));
//                    } else if (atom_class.equals("NullNode")) {
//                        System.out.println(visitNull((NullNode) atom));
//                    } else if (atom_class.equals("ConsNode")) {
//                        System.out.println(visitCons((ConsNode) atom));
//                    }
//                } else if (node.getChildren().getFirst().getClass().getSimpleName().equals("FunctionCallNode")) {
//                    System.out.println(visitFunctionCall((FunctionCallNode) node.getChildren().getFirst()));
//                } else {
//                    System.out.println(((LiteralNode) node.getChildren().getFirst()).getValue());
//                }
//            }
            case "OperationNode" -> System.out.println(visitOperation((OperationNode) node.getChildren().getFirst()));
            case "BooleanNode" -> System.out.println(visitBool((BooleanNode) node.getChildren().getFirst()));
            case "ComparisonNode" ->
                    System.out.println(visitComparison((ComparisonNode) node.getChildren().getFirst()));
            case "ConsNode" -> System.out.println(visitCons((ConsNode) node.getChildren().getFirst()));
            case "TailNode" -> System.out.println(visitTail((TailNode) node.getChildren().getFirst()));
            case "HeadNode" -> System.out.println(visitHead((HeadNode) node.getChildren().getFirst()));
            case "ListNode" -> System.out.println(visitList((ListNode) node.getChildren().getFirst()));
            case "PredicateNode" -> System.out.println(visitPredicate((PredicateNode) node.getChildren().getFirst()));
            case "LogicalOperationNode" ->
                    System.out.println(visitLogicalOperation((LogicalOperationNode) node.getChildren().getFirst()));
            case "NotNode" -> System.out.println(visitNot((NotNode) node.getChildren().getFirst()));
            case "ConditionNode" -> System.out.println(visitCondition((ConditionNode) node.getChildren().getFirst()));
            case "LiteralNode" -> System.out.println(visitLiteral((LiteralNode) node.getChildren().getFirst()));
            case "NullNode" -> System.out.println(visitNull((NullNode) node.getChildren().getFirst()));
            case "AtomNode" -> System.out.println(visit(symbolTable.lookup(((AtomNode) node.getChildren().getFirst()).getValue())));
        }

    }

    private boolean visitBool(BooleanNode node) {
        return node.getValue();
    }

    private void visitAtom(AtomNode node) {
    }

    private String visitLiteral(LiteralNode node) {
        return node.getValue();
    }

    private void visitAssignment(AssignmentNode node) throws Exception {
    }
}

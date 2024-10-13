package visitors;

import ast.nodes.*;

import java.util.stream.Collectors;

public class PrettyVisitor implements ASTVisitor<String> {
    private int depth = 0;

    private String indent() {
        return "  ".repeat(depth);  // indentation based on depth
    }

    @Override
    public String visitAssignmentNode(AssignmentNode node) {
        return indent() + "AssignmentNode(variable=" + node.getVariable() + ", value=" + node.getValue().accept(this) + "), line: " + node.getLine();
    }

    @Override
    public String visitAtomNode(AtomNode node) {
        return indent() + "AtomNode(" + node.getValue() + "), line: " + node.getLine();
    }

    @Override
    public String visitBreakNode(BreakNode node) {
        return indent() + "BreakNode(), line: " + node.getLine();
    }

    @Override
    public String visitComparisonNode(ComparisonNode node) {
        String left = node.getLeftElement().accept(this);
        String right = node.getRightElement().accept(this);

        return indent() + "ComparisonNode(comparison=" + node.getComparison() +
                ", leftElement=" + left + ", rightElement=" + right + "), line: " + node.getLine();
    }

    @Override
    public String visitConditionBranch(ConditionBranch branch) {
        String condition = branch.getCondition().accept(this);
        String action = branch.getAction().accept(this);

        return indent() + "ConditionBranch(condition=" + condition + ", action=" + action + ")";
    }

    @Override
    public String visitConditionNode(ConditionNode node) {
        String branchesStr = node.getBranches().stream()
                .map(branch -> branch.accept(this))
                .collect(Collectors.joining(",\n" + indent()));

        String defaultActionStr = node.getDefaultAction() != null
                ? node.getDefaultAction().accept(this)
                : "null";

        return indent() + "ConditionNode(branches=[" + branchesStr + "], defaultAction=[" + defaultActionStr + "]), line: " + node.getLineOp() + "-" + node.getLineClo();
    }

    @Override
    public String visitConsNode(ConsNode node) {
        String head = node.getHead().accept(this);
        String tail = node.getTail().accept(this);

        return indent() + "ConsNode(head=" + head + ", tail=" + tail + "), line: " + node.getLine();
    }

    @Override
    public String visitFunctionNode(FunctionNode node) {
        depth++;
        String params = String.join(", ", node.getParameters());
        String body = node.getBody().accept(this);
        depth--;
        return indent() + "FunctionNode(functionName=" + node.getFunctionName() +
                ", parameters=[" + params + "], body=" + body + "), line: " + node.getLineOp() + "-" + node.getLineClo();
    }

    @Override
    public String visitFunctionCallNode(FunctionCallNode node) {
        String params = node.getParameters().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(", "));
        return indent() + "FunctionCallNode(functionName=" + node.getFunctionName() +
                ", parameters=[" + params + "]), line: " + node.getLine();
    }

    @Override
    public String visitHeadNode(HeadNode node) {
        String head = node.getHead().accept(this);
        return indent() + "HeadNode(list=" + head + "), line: " + node.getLine();
    }

    @Override
    public String visitLambdaNode(LambdaNode node) {
        depth++;
        String body = node.getBody().accept(this);
        depth--;
        return indent() + "LambdaNode(parameters=" + node.getParameters() + ", body=" + body + "), line: " + node.getLine();
    }

    @Override
    public String visitListNode(ListNode node) {
        String elementsStr = node.getElements().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(",\n" + indent()));
        return indent() + "ListNode(elements=[" + elementsStr + "]), line: " + node.getLine();
    }

    @Override
    public String visitLiteralNode(LiteralNode node) {
        return indent() + "LiteralNode(" + node.getValue() + "), line:" + node.getLine();
    }

    @Override
    public String visitLogicalOperationNode(LogicalOperationNode node) {
        String leftElement = node.getLeftElement().accept(this);
        String rightElement = node.getRightElement().accept(this);

        return indent() + "LogicalOperationNode(operator=" + node.getOperator() + ", leftElement=" +
                leftElement + ", rightElement=" + rightElement + "), line: " + node.getLineOp() + "-" + node.getLineClo();
    }

    @Override
    public String visitNotNode(NotNode node) {
        String element = node.getElement().accept(this);
        return indent() + "NotNode(element=" + element + "), line: " + node.getLine();
    }

    @Override
    public String visitOperationNode(OperationNode node) {
        String operandsStr = node.getOperands().stream()
                .map(operand -> operand.accept(this))
                .collect(Collectors.joining(", "));
        return indent() + "OperationNode(operator=" + node.getOperator() + ", operands=[" + operandsStr + "]), line: " + node.getLine();
    }

    @Override
    public String visitPredicateNode(PredicateNode node) {
        String element = node.getElement().accept(this);
        return indent() + "PredicateNode(predicate=" + node.getPredicate() + ", element=" + element + "), line:" + node.getLine();
    }

    @Override
    public String visitProgNode(ProgNode node) {
        depth++;
        String statementsStr = node.getStatements().stream()
                .map(statement -> statement.accept(this))
                .collect(Collectors.joining(",\n" + indent()));

        String finalExpressionStr = node.getStatements().stream()
                .map(statement -> statement.accept(this))
                .collect(Collectors.joining(",\n" + indent()));
        depth--;
        return indent() + "ProgNode(statements=[" + statementsStr + "], finalExpression=" + finalExpressionStr + "), line: " + node.getLineOp() + "-" + node.getLineClo();
    }

    @Override
    public String visitReturnNode(ReturnNode node) {
        String returnValue = node.getReturnValue().accept(this);
        return indent() + "ReturnNode(returnValue=" + returnValue + "), line:" + node.getLine();
    }

    @Override
    public String visitSignNode(SignNode node) {
        return indent() + "SignNode(sign=" + node.getSign() + ")";
    }

    @Override
    public String visitTailNode(TailNode node) {
        String tail = node.getTail().accept(this);
        return indent() + "TailNode(list=" + tail + "), line: " + node.getLine();
    }

    @Override
    public String visitWhileNode(WhileNode node) {
        depth++;
        String condition = node.getCondition().accept(this);
        String body = node.getBody().stream()
                .map(statement -> statement.accept(this))
                .collect(Collectors.joining(",\n" + indent()));
        depth--;
        return indent() + "WhileNode(condition=" + condition + ", body=" + body + "), line: " + node.getLineOp() + "-" + node.getLineClo();
    }
}

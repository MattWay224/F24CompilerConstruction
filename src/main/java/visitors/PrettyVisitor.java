package visitors;

import ast.nodes.*;

import java.util.stream.Collectors;

public class PrettyVisitor implements ASTVisitor<String> {
    @Override
    public String visitAssignmentNode(AssignmentNode node) {
        return "AssignmentNode(variable=" + node.getVariable() + ", value=" + node.getValue().accept(this) + ")";
    }

    @Override
    public String visitAtomNode(AtomNode node) {
        return "AtomNode(" + node.getValue() + ")";
    }

    @Override
    public String visitBreakNode(BreakNode node) {
        return "BreakNode()";
    }

    @Override
    public String visitComparisonNode(ComparisonNode node) {
        String left = node.getLeftElement().accept(this);
        String right = node.getRightElement().accept(this);

        return "ComparisonNode(comparison=" + node.getComparison() +
                ", leftElement=" + left + ", rightElement=" + right + ")";
    }

    @Override
    public String visitConditionBranch(ConditionBranch branch) {
        String condition = branch.getCondition().accept(this);
        String action = branch.getAction().accept(this);

        return "ConditionBranch(condition=" + condition + ", action=" + action + ")";
    }

    @Override
    public String visitQuoteNode(QuoteNode node) {
        return "QuoteNode(QuotedExpr:" + node.getQuotedExpr().accept(this) + ")";
    }

    @Override
    public String visitLambdaCallNode(LambdaCallNode node) {
        String params = node.getParameters().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(", "));
        return "FunctionCallNode(functionName=" + node.getLambdaName() +
                ", parameters=[" + params + "])";
    }

    @Override
    public String visitConditionNode(ConditionNode node) {
        String branchesStr = node.getBranches().stream()
                .map(branch -> branch.accept(this))
                .collect(Collectors.joining(",\n"));

        String defaultActionStr = node.getDefaultAction() != null
                ? node.getDefaultAction().accept(this)
                : "null";

        return "ConditionNode(branches=[" + branchesStr + "], defaultAction=[" + defaultActionStr + "])";
    }

    @Override
    public String visitConsNode(ConsNode node) {
        String head = node.getHead().accept(this);
        String tail = node.getTail().accept(this);
        return "ConsNode(head=" + head + ", tail=" + tail + ")";
    }

    @Override
    public String visitFunctionNode(FunctionNode node) {
        String params = String.join(", ", node.getParameters());
        //String body = node.getBody().accept(this);
        return "FunctionNode(functionName=" + node.getFunctionName() +
                ", parameters=[" + params + "], body=" ;
                //body + ")";
    }

    @Override
    public String visitFunctionCallNode(FunctionCallNode node) {
        String params = node.getParameters().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(", "));
        return "FunctionCallNode(functionName=" + node.getFunctionName() +
                ", parameters=[" + params + "])";
    }

    @Override
    public String visitHeadNode(HeadNode node) {
        String head = node.getHead().accept(this);
        return "HeadNode(list=" + head + ")";
    }

    @Override
    public String visitLambdaNode(LambdaNode node) {
        String body = node.getBody().accept(this);
        return "LambdaNode(parameters=" + node.getParameters() + ", body=" + body + ")";
    }

    @Override
    public String visitListNode(ListNode node) {
        String elementsStr = node.getElements().stream()
                .map(element -> element.accept(this))
                .collect(Collectors.joining(","));
        return "ListNode(elements=[" + elementsStr + "])";
    }

    @Override
    public String visitLiteralNode(LiteralNode node) {
        return "LiteralNode(" + node.getValue() + ")";
    }

    @Override
    public String visitLogicalOperationNode(LogicalOperationNode node) {
        String leftElement = node.getLeftElement().accept(this);
        String rightElement = node.getRightElement().accept(this);

        return "LogicalOperationNode(operator=" + node.getOperator() + ", leftElement=" +
                leftElement + ", rightElement=" + rightElement + ")";
    }

    @Override
    public String visitNotNode(NotNode node) {
        String element = node.getElement().accept(this);
        return "NotNode(element=" + element + ")";
    }

    @Override
    public String visitOperationNode(OperationNode node) {
        String operandsStr = node.getOperands().stream()
                .map(operand -> operand.accept(this))
                .collect(Collectors.joining(", "));
        return "OperationNode(operator=" + node.getOperator() + ", operands=[" + operandsStr + "])";
    }

    @Override
    public String visitPredicateNode(PredicateNode node) {
        String element = node.getElement().accept(this);
        return "PredicateNode(predicate=" + node.getPredicate() + ", element=" + element + ")";
    }

    @Override
    public String visitProgNode(ProgNode node) {
        String statementsStr = node.getStatements().stream()
                .map(statement -> statement.accept(this))
                .collect(Collectors.joining(", "));
        return "ProgNode(statements=[" + statementsStr + "]" + ")";
    }

    @Override
    public String visitReturnNode(ReturnNode node) {
        String returnValue = node.getReturnValue().accept(this);
        return "ReturnNode(returnValue=" + returnValue + ")";
    }

    @Override
    public String visitSignNode(SignNode node) {
        return "SignNode(sign=" + node.getSign() + ")";
    }

    @Override
    public String visitTailNode(TailNode node) {
        String tail = node.getTail().accept(this);
        return "TailNode(list=" + tail + ')';
    }

    @Override
    public String visitWhileNode(WhileNode node) {
        String condition = node.getCondition().accept(this);
        String body = node.getBody().stream()
                .map(statement -> statement.accept(this))
                .collect(Collectors.joining(",\n"));
        return "WhileNode(condition=" + condition + ", body=" + body + ")";
    }

    @Override
    public String visitEvalNode(EvalNode node) {
        String subnode = node.getNode().accept(this);
        return "EvalNode(node=" + subnode + ")";
    }

    @Override
    public String visitNullNode(NullNode nullNode) {
        return "NullNode(nullNode=" + nullNode + ")";
    }

    @Override
    public String visitBoolNode(BooleanNode booleanNode) {
        return "BoolNode(booleanNode=" + booleanNode.getValue() + ")";
    }

    @Override
    public String visitPrintNode(PrintNode printNode) {
        return "Printnode:";
    }
}

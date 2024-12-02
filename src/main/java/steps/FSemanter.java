package steps;

import ast.nodes.*;

import java.util.List;

public class FSemanter {
    public void checkArithmeticOperation(ASTNode operation) throws Exception {
        String operator = ((OperationNode) operation).getOperator();
        List<ASTNode> operands = ((OperationNode) operation).getOperands();

        if (operands.isEmpty()) {
            throw new Exception("ERROR: ARITHMETIC OPERATION " + operator + " HAS NO OPERANDS" + " at line: " +
                    ((OperationNode) operation).getLine());
        }
    }

    private void checkLogicalOperation(LogicalOperationNode operation) throws Exception {
        String operator = operation.getOperator();
        ASTNode leftOperand = operation.getLeftElement();
        ASTNode rightOperand = operation.getRightElement();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("ERROR: LOGICAL OPERATION " + operator + " HAS NO OPERANDS at line: " +
                    operation.getLineOp());
        }
    }

    private void checkComparisonOperation(ComparisonNode operation) throws Exception {
        String comparison = operation.getComparison();
        ASTNode leftOperand = operation.getLeftElement();
        ASTNode rightOperand = operation.getRightElement();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("ERROR: COMPARISON " + comparison + " HAS NO OPERANDS at line: " + operation.getLine());
        }

        if (leftOperand.getType() != rightOperand.getType()) {
            throw new Exception("ERROR: " + leftOperand.getType()
                    + " CANNOT BE COMPARED WITH " + rightOperand.getType()
                    + " at line " + operation.getLine());
        }
    }


    public void analyze(ASTNode root) throws Exception {
        traverseAndCheck(root);
    }

    private void traverseAndCheck(ASTNode node) throws Exception {
        if (node == null) return;
        else if (node.getClass().getSimpleName().equals("OperationNode")) {
            checkArithmeticOperation(node);

        } else if (node.getClass().getSimpleName().equals("ComparisonNode")) {
            checkComparisonOperation((ComparisonNode) node);

        } else if (node.getClass().getSimpleName().equals("LogicalOperationNode")) {
            checkLogicalOperation((LogicalOperationNode) node);
        }
        for (ASTNode child : node.getChildren()) {
            traverseAndCheck(child);
        }
    }
}

package steps;

import ast.nodes.*;

import java.util.List;

public class FSemanter {
    public void checkOperation(ASTNode operation) throws Exception {
        if (operation instanceof LogicalOperationNode) {
            checkLogicalOperation((LogicalOperationNode) operation);
        } else if (operation instanceof OperationNode) {
            checkArithmeticOperation((OperationNode) operation);
        } else if (operation instanceof ComparisonNode) {
            checkComparisonOperation((ComparisonNode) operation);
        }
    }

    public void checkArithmeticOperation(ASTNode operation) throws Exception {
        String operator = ((OperationNode) operation).getOperator();
        List<ASTNode> operands = ((OperationNode) operation).getOperands();

        if (operands.isEmpty()) {
            throw new Exception("ARITHMETIC OPERATION " + operator + " HAS NO OPERANDS");
        }

        ASTNode.NodeType expectedType = operands.get(0).getType();
        for (ASTNode operand : operands) {
            if (operand.getType() != expectedType) {
                throw new Exception("TYPE ERROR IN " + operator + " OPERATION: EXPECTED " + expectedType
                        + ", FOUND " + operand.getType() + " on line " + ((OperationNode) operation).getLine());
            }
        }
    }

    private void checkLogicalOperation(LogicalOperationNode operation) throws Exception {
        String operator = operation.getOperator();
        ASTNode leftOperand = operation.getLeftElement();
        ASTNode rightOperand = operation.getRightElement();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("LOGICAL OPERATION " + operator + " HAS NO OPERANDS");
        }

        if (leftOperand.getType() != ASTNode.NodeType.BOOL || rightOperand.getType() != ASTNode.NodeType.BOOL) {
            throw new Exception("TYPE ERROR: LOGICAL OPERATORS REQUIRE BOOLEAN OPERANDS on line "
                    + operation.getLineClo());
        }
    }

    private void checkComparisonOperation(ComparisonNode operation) throws Exception {
        String comparison = operation.getComparison();
        ASTNode leftOperand = operation.getLeftElement();
        ASTNode rightOperand = operation.getRightElement();

        if (leftOperand == null || rightOperand == null) {
            throw new Exception("COMPARISON " + comparison + " HAS NO OPERANDS");
        }

        if (leftOperand.getType() != rightOperand.getType()) {
            throw new Exception("TYPE ERROR IN COMPARISON: " + leftOperand.getType()
                    + " CANNOT BE COMPARED WITH " + rightOperand.getType()
                    + " on line " + operation.getLine());
        }
    }

    public void analyze(ASTNode root) throws Exception {
        traverseAndCheck(root);
    }

    private void traverseAndCheck(ASTNode node) throws Exception {
        if (node == null) return;

        if (node.getClass().getSimpleName().equals("OperationNode")) {
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

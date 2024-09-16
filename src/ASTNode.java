import java.util.List;
import java.util.stream.Collectors;

abstract class ASTNode {
    public abstract String toString();
}

//func
class FunctionNode extends ASTNode {
    String functionName;
    List<String> parameters;
    ASTNode body;

    public FunctionNode(String functionName, List<String> parameters, ASTNode body) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        String params = parameters.stream().collect(Collectors.joining(", "));
        return "FunctionNode(functionName=" + functionName + ", parameters=[" + params + "], body=" + body + ")";
    }
}

//setq
class AssignmentNode extends ASTNode {
    String variable;
    ASTNode value;

    public AssignmentNode(String variable, ASTNode value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssignmentNode(variable=" + variable + ", value=" + value + ")";
    }
}

//cond
class ConditionNode extends ASTNode {
    List<ConditionBranch> branches;
    ASTNode defaultAction;

    public ConditionNode(List<ConditionBranch> branches, ASTNode defaultAction) {
        this.branches = branches;
        this.defaultAction = defaultAction;
    }

    @Override
    public String toString() {
        String branchStr = branches.stream().map(ConditionBranch::toString).collect(Collectors.joining(", "));
        return "ConditionNode(branches=[" + branchStr + "]), defaultAction=[" + defaultAction + "]";
    }
}

//single cond branch
class ConditionBranch {
    ASTNode condition;
    ASTNode action;

    public ConditionBranch(ASTNode condition, ASTNode action) {
        this.condition = condition;
        this.action = action;
    }

    @Override
    public String toString() {
        return "ConditionBranch(condition=" + condition + ", action=" + action + ")";
    }
}

//literals
class LiteralNode extends ASTNode {
    String value;

    public LiteralNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralNode(" + value + ")";
    }
}

//atom
class AtomNode extends ASTNode {
    String value;

    public AtomNode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AtomNode(" + value + ")";
    }
}

class OperationNode extends ASTNode {
    String operator;
    List<ASTNode> operands;
    Boolean sign;

    public OperationNode(String operator, List<ASTNode> operands, Boolean sign) {
        this.operator = operator;
        this.operands = operands;
        this.sign = sign;
    }

    @Override
    public String toString() {
        String operandsStr = operands.stream().map(ASTNode::toString).collect(Collectors.joining(", "));
        return "OperationNode(operator=" + operator + ", operands=[" + operandsStr + "])";
    }
}

class ConsNode extends ASTNode {
    private ASTNode item;
    private ASTNode list;

    public ConsNode(ASTNode item, ASTNode list) {
        this.item = item;
        this.list = list;
    }

    @Override
    public String toString() {
        return "ConsNode(head=" + item.toString() + ", tail=" + list.toString() + ")";
    }
}

class HeadNode extends ASTNode {
    ASTNode list;

    public HeadNode(ASTNode list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "HeadNode(list=" + list.toString() + ")";
    }
}

class TailNode extends ASTNode {
    ASTNode list;

    public TailNode(ASTNode list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "TailNode(list=" + list.toString() + ")";
    }
}

class PredicateNode extends ASTNode {
    String predicate;
    ASTNode element;

    public PredicateNode(String predicate, ASTNode element) {
        this.predicate = predicate;
        this.element = element;
    }

    @Override
    public String toString() {
        return "PredicateNode(predicate=" + predicate + ", element=" + element.toString() + ")";
    }
}

class ComparisonNode extends ASTNode {
    String comparison;
    ASTNode leftElement;
    ASTNode rightElement;

    public ComparisonNode(String comparison, ASTNode leftElement, ASTNode rightElement) {
        this.comparison = comparison;
        this.leftElement = leftElement;
        this.rightElement = rightElement;
    }

    @Override
    public String toString() {
        return "ComparisonNode(comparison=" + comparison + ", leftElement=" + leftElement.toString() + ", rightElement=" + rightElement.toString() + ")";
    }
}

class LogicalOperationNode extends ASTNode {
    String operator;
    ASTNode leftElement;
    ASTNode rightElement;

    public LogicalOperationNode(String operator, ASTNode leftElement, ASTNode rightElement) {
        this.operator = operator;
        this.leftElement = leftElement;
        this.rightElement = rightElement;
    }

    @Override
    public String toString() {
        return "LogicalOperationNode(operator=" + operator + ", leftElement=" + leftElement.toString() + ", rightElement=" + rightElement.toString() + ")";
    }
}

class NotNode extends ASTNode {
    ASTNode element;

    public NotNode(ASTNode element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "NotNode(element=" + element.toString() + ")";
    }
}

class LambdaNode extends ASTNode {
    List<String> parameters;
    ASTNode body;

    public LambdaNode(List<String> parameters, ASTNode body) {
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public String toString() {
        return "LambdaNode(parameters=" + parameters + ", body=" + body + ")";
    }
}

class ListNode extends ASTNode {
    List<ASTNode> elements;

    public ListNode(List<ASTNode> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        String elementsStr = elements.stream()
                .map(ASTNode::toString)
                .collect(Collectors.joining(", "));
        return "ListNode(elements=[" + elementsStr + "])";
    }
}

class SignNode extends ASTNode {
    private final String sign;

    public SignNode(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "SignNode(sign=" + sign + ")";
    }
}

class ProgNode extends ASTNode {
    private List<ASTNode> statements;
    private ASTNode finalExpression;

    public ProgNode(List<ASTNode> statements, ASTNode finalExpression) {
        this.statements = statements;
        this.finalExpression = finalExpression;
    }

    @Override
    public String toString() {
        return "ProgNode(statements=" + statements + ", finalExpression=" + finalExpression + ")";
    }
}

class WhileNode extends ASTNode {
    private ASTNode condition;
    private ASTNode body;

    public WhileNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode(condition=" + condition + ", body=" + body + ")";
    }
}

class ReturnNode extends ASTNode {
    private ASTNode returnValue;

    public ReturnNode(ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public String toString() {
        return "ReturnNode(returnValue=" + returnValue + ")";
    }
}

class BreakNode extends ASTNode {
    @Override
    public String toString() {
        return "BreakNode()";
    }
}
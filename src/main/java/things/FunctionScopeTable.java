package things;

import ast.nodes.ASTNode;
import ast.nodes.FunctionCallNode;
import ast.nodes.LambdaCallNode;
import ast.nodes.ProgNode;

import java.util.HashMap;
import java.util.Map;

public class FunctionScopeTable {
    // Maps each FunctionCallNode to its scope (a map of variables)
    private final Map<FunctionCallNode, Map<String, ASTNode>> functionScopes;
    private final Map<LambdaCallNode, Map<String, ASTNode>> lambdaScopes;
    private final Map<ProgNode, Map<String, ASTNode>> progScopes;

    public FunctionScopeTable() {
        this.lambdaScopes = new HashMap<>();
        this.functionScopes = new HashMap<>();
        this.progScopes = new HashMap<>();
    }

    // Creates a new scope for a function call
    public void enterScope(FunctionCallNode funcCallNode) {
        functionScopes.put(funcCallNode, new HashMap<>());
    }

    public void enterScope(LambdaCallNode lambdaCallNode) {
        lambdaScopes.put(lambdaCallNode, new HashMap<>());
    }

    public void enterScope(ProgNode progNode) {
        progScopes.put(progNode, new HashMap<>());
    }

    // Removes the scope for a function call (when the function call is complete)
    public void exitScope(FunctionCallNode funcCallNode) {
        functionScopes.remove(funcCallNode);
    }

    public void exitScope(LambdaCallNode lambdaCallNode) {
        lambdaScopes.remove(lambdaCallNode);
    }

    public void exitScope(ProgNode progNode) {
        progScopes.remove(progNode);
    }

    // Adds a variable to the scope associated with the specified FunctionCallNode
    public void put(FunctionCallNode funcCallNode, String name, ASTNode value) {
        Map<String, ASTNode> scope = functionScopes.get(funcCallNode);
        if (scope != null) {
            scope.put(name, value);
        }
    }

    public void put(LambdaCallNode lambdaCallNode, String name, ASTNode value) {
        Map<String, ASTNode> scope = lambdaScopes.get(lambdaCallNode);
        if (scope != null) {
            scope.put(name, value);
        }
    }

    public void put(ProgNode progNode, String name, ASTNode value) {
        Map<String, ASTNode> scope = progScopes.get(progNode);
        if (scope != null) {
            scope.put(name, value);
        }
    }

    // Retrieves a variable from the scope associated with the specified FunctionCallNode
    public ASTNode get(FunctionCallNode funcCallNode, String name) {
        Map<String, ASTNode> scope = functionScopes.get(funcCallNode);
        return (scope != null) ? scope.get(name) : null;
    }

    public ASTNode get(LambdaCallNode lambdaCallNode, String name) {
        Map<String, ASTNode> scope = lambdaScopes.get(lambdaCallNode);
        return (scope != null) ? scope.get(name) : null;
    }

    public ASTNode get(ProgNode progNode, String name) {
        Map<String, ASTNode> scope = progScopes.get(progNode);
        return (scope != null) ? scope.get(name) : null;
    }

//    // check if a variable exists in the specified function scope
//    public boolean contains(FunctionCallNode funcCallNode, String name) {
//        Map<String, ASTNode> scope = functionScopes.get(funcCallNode);
//        return scope != null && scope.containsKey(name);
//    }

    // Retrieves the entire scope map for a given function call node
    public Map<String, ASTNode> getMappingsForScope(FunctionCallNode funcCallNode) {
        // Return a copy of the map to avoid external modification
        Map<String, ASTNode> scope = functionScopes.get(funcCallNode);
        return (scope != null) ? new HashMap<>(scope) : null;
    }

    public Map<String, ASTNode> getMappingsForScope(LambdaCallNode lambdaCallNode) {
        // Return a copy of the map to avoid external modification
        Map<String, ASTNode> scope = lambdaScopes.get(lambdaCallNode);
        return (scope != null) ? new HashMap<>(scope) : null;
    }

    public Map<String, ASTNode> getMappingsForScope(ProgNode progNode) {
        // Return a copy of the map to avoid external modification
        Map<String, ASTNode> scope = progScopes.get(progNode);
        return (scope != null) ? new HashMap<>(scope) : null;
    }


//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder("FunctionScopeTable:\n");
//        for (Map.Entry<FunctionCallNode, Map<String, ASTNode>> entry : functionScopes.entrySet()) {
//            sb.append("Function Call: ").append(entry.getKey()).append("\n");
//            sb.append("Scope:\n");
//            for (Map.Entry<String, ASTNode> scopeEntry : entry.getValue().entrySet()) {
//                sb.append("  ").append(scopeEntry.getKey()).append(" -> ").append(scopeEntry.getValue()).append("\n");
//            }
//        }
//        return sb.toString();
//    }

}

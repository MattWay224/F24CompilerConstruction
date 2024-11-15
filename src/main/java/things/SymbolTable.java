package things;

import ast.nodes.ASTNode;
import steps.FSemanter;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolTable parent;
    Map<String, ASTNode> symbols;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public void define(String name, ASTNode value) {
        symbols.put(name, value);
    }

    public ASTNode lookup(String name) throws Exception {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else if (parent != null) {
            return parent.lookup(name);
        } else {
            throw new Exception("Undefined variable: " + name);
        }
    }

    public boolean isDefined(String name) {
        return symbols.containsKey(name) || (parent != null && parent.isDefined(name));
    }

    public void simplifyAllSymbols(FSemanter semanter) throws Exception {
        for (Map.Entry<String, ASTNode> entry : symbols.entrySet()) {
            ASTNode simplifiedNode = semanter.simplifyExpression(entry.getValue());
            if (simplifiedNode != null) {
                entry.setValue(simplifiedNode);
            }
        }
    }
}

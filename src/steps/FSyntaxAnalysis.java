package steps;

import ast.ASTNodeFactory;
import ast.nodes.ASTNode;
import visitors.PrettyVisitor;

import java.util.List;

public class FSyntaxAnalysis {
	private static FSyntaxAnalysis instance = new FSyntaxAnalysis();
	private Parser parser;
	private List<Token> tokens;
	private int currentPos = 0;
	private PrettyVisitor visitor;
	private ASTNodeFactory factory;

	private FSyntaxAnalysis() {}

	public static FSyntaxAnalysis getInstance() {
		if (instance == null) {
			instance = new FSyntaxAnalysis();
		}
		return instance;
	}

	public void setter(List<Token> tokens, PrettyVisitor visitor, ASTNodeFactory factory) {
		this.tokens = tokens;
		this.visitor = visitor;
		this.factory = factory;
		this.parser = new Parser(tokens, factory);
	}

	public List<ASTNode> parse() throws Exception {
		return parser.parse();
	}
}

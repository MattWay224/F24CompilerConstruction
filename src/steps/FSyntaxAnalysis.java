package steps;

import ast.nodes.ASTNode;

import java.util.List;

public class FSyntaxAnalysis {
	private static FSyntaxAnalysis instance = new FSyntaxAnalysis();
	private Parser parser;
	private FSyntaxAnalysis() {
		this.parser = Parser.getInstance();
	}

	public static FSyntaxAnalysis getInstance() {
		if (instance == null) {
			instance = new FSyntaxAnalysis();
		}
		return instance;
	}

	public void setter(List<Token> tokens) {
		this.parser = Parser.getInstance();
		this.parser.setter(tokens);
	}

	public ASTNode parse() throws Exception {
		return parser.parse();
	}
}

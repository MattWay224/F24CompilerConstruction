package testing;

import ast.nodes.ASTNode;
import steps.FSemanter;
import steps.Flexer;
import steps.Parser;
import steps.Token;
import things.ASTPrinter;
import things.InputFileReader;
import visitors.PrettyVisitor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class FsemanterTest {
	private Parser parser;

	public String test(String testFilePath) throws Exception {
		String input = InputFileReader.readInputFromFile(testFilePath);
		this.parser = new Parser();
		Flexer flexer = new Flexer();
		FSemanter semanter = new FSemanter();
		flexer.setInput(input);
		List<Token> tokens = flexer.tokenize();
		ASTNode ast = parseTokens(tokens);
		semanter.analyze(ast);
		return printAST(ast);
	}

	private ASTNode parseTokens(List<Token> tokens) throws Exception {
		parser.setTokens(tokens);
		return parser.parse();
	}

	private String printAST(ASTNode ast) throws IOException {
		Writer stringWriter = new StringWriter();
		PrettyVisitor prettyVisitor = new PrettyVisitor();
		ASTPrinter.printAST(stringWriter, ast, prettyVisitor, 0);
		String output = stringWriter.toString();
		stringWriter.flush();
		return output;
	}
}
